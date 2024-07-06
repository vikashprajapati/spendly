package dev.nomadicprogrammer.spendly.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ScreenHeader(modifier: Modifier = Modifier, screenHeaderState: ScreenHeaderState) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        screenHeaderState.run {
            leftHeaderButton?.let { leftButton -> leftButton() }
            title?.let { title -> title() }
            rightHeaderButton?.let { rightButton -> rightButton() }
        }
    }
}

@Composable
@Preview
fun ScreenHeaderPreview() {
    ScreenHeader(
        screenHeaderState = ScreenHeaderState(
            title = { Text(text = "Title") },
            leftHeaderButton = { ScreenHeaderDefault.BackHeaderButton { } },
            rightHeaderButton = { ScreenHeaderDefault.MenuHeaderButton { } }
        )
    )
}

data class ScreenHeaderState(
    val title : (@Composable () -> Unit)? = null,
    val leftHeaderButton : (@Composable () -> Unit)? = null,
    val rightHeaderButton : (@Composable () -> Unit)?= null
)

object ScreenHeaderDefault{
    @Composable
    fun BackHeaderButton(
        tint : Color = Color.Black,
        onClick: () -> Unit
    ){
        IconButton(onClick = { onClick() }) {
            Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = tint)
        }
    }

    @Composable
    fun MenuHeaderButton(
        tint : Color = Color.Black,
        onClick: () -> Unit
    ){
        IconButton(onClick = { onClick() }) {
            Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = "Menu", tint = tint)
        }
    }
}