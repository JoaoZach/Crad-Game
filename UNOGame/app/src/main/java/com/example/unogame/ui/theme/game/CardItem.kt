package com.example.unogame.ui.theme.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.unogame.R
import com.example.unogame.models.Card
import com.example.unogame.models.CardColor
import com.example.unogame.models.CardValue



@Composable
fun UnoCardSprite(
    color: CardColor,
    value: CardValue,
    modifier: Modifier = Modifier.size(70.dp, 100.dp)
) {
    val spriteSheet = ImageBitmap.imageResource(id = R.drawable.cards_deck)

    val rows = 8
    val columns = 14

    val cardWidth = spriteSheet.width / columns
    val cardHeight = spriteSheet.height / rows

    val baseRow = when (color) {
        CardColor.RED -> 0
        CardColor.YELLOW -> 1
        CardColor.GREEN -> 2
        CardColor.BLUE -> 3
        else -> 0
    }

    val colIndex = when (value) {
        CardValue.ZERO -> 0
        CardValue.ONE -> 1
        CardValue.TWO -> 2
        CardValue.THREE -> 3
        CardValue.FOUR -> 4
        CardValue.FIVE -> 5
        CardValue.SIX -> 6
        CardValue.SEVEN -> 7
        CardValue.EIGHT -> 8
        CardValue.NINE -> 9
        CardValue.SKIP -> 10
        CardValue.REVERSE -> 11
        CardValue.DRAW_TWO -> 12
        CardValue.WILD,
        CardValue.WILD_DRAW_FOUR -> 13
    }

    val rowIndex = when (value) {
        CardValue.WILD -> 0
        CardValue.WILD_DRAW_FOUR -> 4
        else -> baseRow
    }

    Canvas(modifier = modifier) {
        drawImage(
            image = spriteSheet,
            srcOffset = IntOffset((colIndex * cardWidth) + colIndex, (rowIndex * cardHeight) + rowIndex),
            srcSize = IntSize(cardWidth, cardHeight),
            dstSize = IntSize(size.width.toInt(), size.height.toInt())
        )
    }

}

@Composable
fun CardItem(
    card: Card,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .padding(6.dp)
            .width(70.dp)
            .height(100.dp)
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 6.dp,
        shadowElevation = 6.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
           UnoCardSprite(color = card.color, value = card.value)
        }
    }
}
@Composable
@Preview(showBackground = true)
fun CardItemPreview() {
    CardItem(card = Card(CardColor.WILD, CardValue.WILD_DRAW_FOUR))
}