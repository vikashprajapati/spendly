package dev.nomadicprogrammer.spendly.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun MultipleFloatingActionButton(
    mainFab : FabMainItem,
    isFabOpen : Boolean,
    fabActionItems : List<FabActionItem>,
    onClick: () -> Unit,
) {
    @Composable fun MainFab() {
        FloatingActionButton(onClick = {
            onClick()
        }, shape = MaterialTheme.shapes.large) {
            Icon(
                imageVector = mainFab.icon,
                contentDescription = mainFab.contentDescription
            )
        }
    }

    when {
        isFabOpen -> {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                val fabBgColor = MaterialTheme.colorScheme.surface
                val fabContentColor = MaterialTheme.colorScheme.onSurface
                fabActionItems.forEach {
                    ExtendedFloatingActionButton(
                        text = { Text(text = it.contentDescription) },
                        icon = { it.icon() },
                        containerColor = it.actionBgColor ?: fabBgColor,
                        contentColor = it.actionContentColor ?: fabContentColor,
                        onClick = {
                            it.onClick()
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                MainFab()
            }
        }
        else -> {
            MainFab()
        }
    }
}

data class FabMainItem(
    val icon : ImageVector,
    val contentDescription : String
)

data class FabActionItem(
    val icon : @Composable () -> Unit,
    val contentDescription : String,
    val actionBgColor : Color? = null,
    val actionContentColor : Color?= null,
    val onClick: () -> Unit
)