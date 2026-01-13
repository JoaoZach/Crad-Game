package com.example.unogame.models


data class Game(
    val gameId : String = "",
    val players : List<String> = emptyList(),
    val currentTurn : Int = 0,
    val direction : Int = 1,
    val cardsOnTable : List<Card> = emptyList(),
    val playersHands : Map<String, List<Card>> = emptyMap(),
    val deck: List<Card> = emptyList(),
    val started : Boolean = false
)

