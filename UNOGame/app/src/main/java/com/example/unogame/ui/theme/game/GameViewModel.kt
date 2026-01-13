package com.example.unogame.ui.theme.game

import androidx.lifecycle.ViewModel
import com.example.unogame.models.Card
import com.example.unogame.models.CardColor
import com.example.unogame.models.CardValue
import com.example.unogame.models.Game
import com.example.unogame.repositories.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GameViewModel : ViewModel() {

    private val repository = GameRepository()

    private val _game = MutableStateFlow<Game?>(null)
    val game: StateFlow<Game?> = _game

    // ───────────────── CREATE / JOIN ─────────────────

    fun createGame(playerId: String, onGameCreated: (String) -> Unit) {
        repository.createGame(playerId) { gameId ->
            listenGame(gameId)
            onGameCreated(gameId)
        }
    }

    fun joinGame(gameId: String, playerId: String) {
        repository.joinGame(gameId, playerId) {
            listenGame(gameId)
        }
    }

    // ───────────────── START GAME ─────────────────

    fun startGame() {
        val current = _game.value ?: return
        if (current.started || current.players.size < 2) return

        // Create a new shuffled deck
        val deck = UnoDeck.createDeck().shuffled()
        var deckIndex = 0

        // Deal 7 cards to each player
        val hands = current.players.associateWith {
            val hand = deck.subList(deckIndex, deckIndex + 7)
            deckIndex += 7
            hand
        }

        // Get the first card for the table
        val topCard = deck[deckIndex++]

        // Create the final game state
        val updatedGame = current.copy(
            playersHands = hands,
            cardsOnTable = listOf(topCard),
            deck = deck.drop(deckIndex), // The rest of the deck
            started = true,
            currentTurn = 0,
            direction = 1
        )

        _game.value = updatedGame
        repository.updateGame(updatedGame)
    }


    // ───────────────── PLAY CARD ─────────────────

    fun playCard(
        playerId: String,
        card: Card,
        chosenColor: CardColor? = null
    ) {
        val current = _game.value ?: return
        if (current.players[current.currentTurn] != playerId) return

        val topCard = current.cardsOnTable.lastOrNull() ?: return

        val isWild = card.value == CardValue.WILD || card.value == CardValue.WILD_DRAW_FOUR

        val validMove =
            isWild ||
                    card.color == topCard.color ||
                    (
                            topCard.value != CardValue.WILD &&
                                    topCard.value != CardValue.WILD_DRAW_FOUR &&
                                    card.value == topCard.value
                            )
        if (!validMove) return

        // If it's a wild card, its color is the one chosen by the player for the next turn
        val playedCard = if (isWild) card.copy(color = chosenColor ?: CardColor.WILD) else card

        // --- Start building the new state ---
        var newDeck = current.deck
        var newDirection = current.direction
        var nextTurn = nextPlayerIndex(current)

        // 1. Remove the played card from the player's hand
        val updatedHands = current.playersHands.toMutableMap()
        val playerHand = updatedHands[playerId]?.toMutableList() ?: return
        playerHand.remove(card)
        updatedHands[playerId] = playerHand

        // 2. Handle card special actions
        when (playedCard.value) {
            CardValue.SKIP -> {
                nextTurn = nextPlayerIndex(current, 2)
            }

            CardValue.REVERSE -> {
                newDirection *= -1
                // We need a temporary game state to calculate the next turn with the new direction
                nextTurn = nextPlayerIndex(current.copy(direction = newDirection))
            }

            CardValue.DRAW_TWO -> {
                val victimId = current.players[nextPlayerIndex(current)]
                val victimHand = (updatedHands[victimId] ?: emptyList()).toMutableList()

                // Take 2 cards from the deck and give to victim
                val drawnCards = newDeck.take(2)
                victimHand.addAll(drawnCards)
                updatedHands[victimId] = victimHand

                // IMPORTANT: Update the deck
                newDeck = newDeck.drop(2)
                // Skip the victim's turn
                nextTurn = nextPlayerIndex(current, 2)
            }

            CardValue.WILD_DRAW_FOUR -> {
                val victimId = current.players[nextPlayerIndex(current)]
                val victimHand = (updatedHands[victimId] ?: emptyList()).toMutableList()

                // Take 4 cards from the deck and give to victim
                val drawnCards = newDeck.take(4)
                victimHand.addAll(drawnCards)
                updatedHands[victimId] = victimHand

                // IMPORTANT: Update the deck
                newDeck = newDeck.drop(4)
                // Skip the victim's turn
                nextTurn = nextPlayerIndex(current, 2)
            }

            else -> Unit // No special action
        }

        // 3. Create the final updated game object
        val updatedGame = current.copy(
            cardsOnTable = current.cardsOnTable + playedCard,
            playersHands = updatedHands,
            deck = newDeck, // Set the updated deck
            currentTurn = nextTurn,
            direction = newDirection
        )

        // 4. Update state and repository ONCE
        _game.value = updatedGame
        repository.updateGame(updatedGame)
    }

    // ───────────────── DRAW CARD ─────────────────

    fun drawCard(playerId: String) {
        val current = _game.value ?: return
        // Check if it's the player's turn
        if (current.players.getOrNull(current.currentTurn) != playerId) return
        // Check if the deck is empty
        if (current.deck.isEmpty()) return

        // --- ATOMIC OPERATION STARTS HERE ---

        // 1. Take one card from the top of the deck.
        val drawnCard = current.deck.first()
        // 2. Create the new deck with the card removed.
        val remainingDeck = current.deck.drop(1)

        // 3. Add the drawn card to the player's hand.
        val newHand = (current.playersHands[playerId] ?: emptyList()) + drawnCard

        // 4. Create the new map of all players' hands.
        val updatedHands = current.playersHands.toMutableMap().apply {
            this[playerId] = newHand
        }

        // 5. Calculate the next player's turn.
        val nextTurn = nextPlayerIndex(current)

        // 6. Create the final, updated game state in a single step.
        val updatedGame = current.copy(
            deck = remainingDeck,
            playersHands = updatedHands,
            currentTurn = nextTurn
        )

        // 7. Update the local state and push the complete, new state to Firestore ONCE.
        _game.value = updatedGame
        repository.updateGame(updatedGame)
    }

    // ───────────────── TURN HELPERS ─────────────────

    private fun nextPlayerIndex(game: Game, steps: Int = 1): Int {
        val size = game.players.size
        // This ensures the result is always positive, preventing issues with negative direction
        return (game.currentTurn + (steps * game.direction) % size + size) % size
    }

    // ───────────────── FIREBASE LISTENER ─────────────────

    private fun listenGame(gameId: String) {
        repository.listenGame(gameId) { game ->
            _game.value = game
        }
    }
}
