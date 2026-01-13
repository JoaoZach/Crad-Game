package com.example.unogame.ui.theme.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unogame.models.Card
import com.example.unogame.models.CardColor
import com.example.unogame.models.CardValue
import com.google.firebase.auth.FirebaseAuth

@Composable
fun GameScreen(
    playerId: String,
    gameId: String,
    viewModel: GameViewModel = viewModel()
) {
    var localGameId by remember { mutableStateOf(gameId) }
    var joinGameId by remember { mutableStateOf("") }

    var showWildDialog by remember { mutableStateOf(false) }
    var pendingWildCard by remember { mutableStateOf<Card?>(null) }

    LaunchedEffect(localGameId) {
        if (localGameId.isNotEmpty()) {
            viewModel.joinGame(localGameId, playerId)
        }
    }

    val game by viewModel.game.collectAsState()

    val isOwner = game?.players?.firstOrNull() == playerId
    val isStarted = game?.started ?: false
    val currentTurnPlayerId =
        game?.players?.getOrNull(game?.currentTurn ?: 0)

    val playerHand = game?.playersHands?.get(playerId) ?: emptyList()
    val cardsOnTable = game?.cardsOnTable ?: emptyList()

    val topCard = cardsOnTable.lastOrNull()

    val activeColor =
        if (
            topCard?.value == CardValue.WILD ||
            topCard?.value == CardValue.WILD_DRAW_FOUR
        ) {
            topCard.color
        } else null



    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text("UNO Game", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(24.dp))

            // ───────────────── CREATE / JOIN ─────────────────
            if (localGameId.isEmpty()) {

                Text("Create or Join a Game", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.createGame(playerId) { newGameId ->
                            localGameId = newGameId
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create New Game")
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = joinGameId,
                    onValueChange = { joinGameId = it },
                    label = { Text("Enter Game ID") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (joinGameId.isNotBlank()) {
                            localGameId = joinGameId
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Join Game")
                }
            }

            // ───────────────── GAME ─────────────────
            else if (game != null) {

                Spacer(Modifier.height(16.dp))
                Text("Game ID:", style = MaterialTheme.typography.labelMedium)
                Text(localGameId, style = MaterialTheme.typography.bodySmall)

                Spacer(Modifier.height(16.dp))

                Text("Players", style = MaterialTheme.typography.titleMedium)
                game!!.players.forEachIndexed { index, id ->
                    val turnMark =
                        if (id == currentTurnPlayerId && isStarted) "⬅ TURN" else ""
                    val you = if (id == playerId) " (You)" else ""
                    Text("Player ${index + 1}$you $turnMark")
                }

                Spacer(Modifier.height(24.dp))

                if (!isStarted) {
                    Text("Waiting for players to join...")
                    Spacer(Modifier.height(16.dp))

                    if (isOwner && game!!.players.size >= 2) {
                        Button(onClick = { viewModel.startGame() }) {
                            Text("Start Game")
                        }
                    } else if (isOwner) {
                        Text("Need at least 2 players to start")
                    }
                } else {

                    // ───────── ACTIVE COLOR INDICATOR ─────────
                    if (activeColor != null && activeColor != CardColor.WILD) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(bottom = 12.dp)
                                .background(
                                    activeColor.toComposeColor().copy(alpha = 0.2f),
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .background(activeColor.toComposeColor(), CircleShape)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Active color: ${activeColor.name}",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }

                    Text("Cards on Table", style = MaterialTheme.typography.titleMedium)
                    LazyRow {
                        items(cardsOnTable) { card ->
                            CardItem(card = card)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Text("Your Hand", style = MaterialTheme.typography.titleMedium)
                    LazyRow {
                        items(playerHand) { card ->
                            CardItem(
                                card = card,
                                onClick =
                                    if (playerId == currentTurnPlayerId) {
                                        {
                                            if (
                                                card.value == CardValue.WILD ||
                                                card.value == CardValue.WILD_DRAW_FOUR
                                            ) {
                                                pendingWildCard = card
                                                showWildDialog = true
                                            } else {
                                                viewModel.playCard(playerId, card)
                                            }
                                        }
                                    } else null
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.drawCard(playerId) },
                        enabled = playerId == currentTurnPlayerId
                    ) {
                        Text(
                            if (playerId == currentTurnPlayerId)
                                "Draw Card"
                            else
                                "Waiting for opponent..."
                        )
                    }
                }
            }
        }
    }

    // ───────────────── WILD COLOR PICKER ─────────────────
    if (showWildDialog && pendingWildCard != null) {
        AlertDialog(
            onDismissRequest = { showWildDialog = false },
            title = { Text("Choose a color") },
            text = {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(
                        CardColor.RED,
                        CardColor.YELLOW,
                        CardColor.GREEN,
                        CardColor.BLUE
                    ).forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clickable {
                                    viewModel.playCard(
                                        playerId = playerId,
                                        card = pendingWildCard!!,
                                        chosenColor = color
                                    )
                                    pendingWildCard = null
                                    showWildDialog = false
                                }
                                .background(color.toComposeColor())
                        )
                    }
                }
            },
            confirmButton = {}
        )
    }

}

@Composable
fun WildColorButton(
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(50.dp)
            .clickable { onClick() },
        shape = CircleShape,
        color = color,
        shadowElevation = 6.dp
    ) {}
}


fun CardColor.toComposeColor(): Color = when(this) {
    CardColor.RED -> Color.Red
    CardColor.BLUE -> Color.Blue
    CardColor.GREEN -> Color.Green
    CardColor.YELLOW -> Color.Yellow
    CardColor.WILD -> Color.Black
}

