package com.example.unogame.models

data class Card(
    val color: CardColor = CardColor.RED,
    var value: CardValue = CardValue.ZERO
)