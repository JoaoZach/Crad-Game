package com.example.unogame.ui.theme.game

import com.example.unogame.models.Card
import com.example.unogame.models.CardColor
import com.example.unogame.models.CardValue

object UnoDeck {

    fun createDeck(): MutableList<Card> {
        val deck = mutableListOf<Card>()

        CardColor.values().forEach { color ->
            if (color != CardColor.WILD) {
                CardValue.values().take(13).forEach { value ->
                    deck.add(Card(color, value))
                }
            }
        }

        repeat(4) {
            deck.add(Card(CardColor.WILD, CardValue.WILD))
            deck.add(Card(CardColor.WILD, CardValue.WILD_DRAW_FOUR))
        }

        deck.shuffle()
        return deck
    }
}