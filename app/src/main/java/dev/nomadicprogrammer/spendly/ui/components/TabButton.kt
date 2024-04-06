package dev.nomadicprogrammer.spendly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TabButton(
    isSelected: Boolean,
    text : String,
    icon : ImageVector? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Tab(
        selected = isSelected,
        modifier = modifier
            .width(IntrinsicSize.Min)
            .background(MaterialTheme.colorScheme.surface, MaterialTheme.shapes.large)
            .border(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.large
            ),
        onClick = onClick
    ){
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    modifier = Modifier.padding(end = 8.dp),
                    contentDescription = "Icon"
                )
            }
            Text(
                text = text, style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TabButtonPreview() {
    Row {
        TabButton(
            isSelected = true,
            text = "Daily",
            icon = Icons.Outlined.ShoppingCart,
            onClick = {}
        )

        Spacer(modifier = Modifier.width(16.dp))
        TabButton(
            isSelected = false,
            text = "Weekly",
            icon = Icons.Outlined.ShoppingCart,
            onClick = {}
        )

        Spacer(modifier = Modifier.width(16.dp))
        TabButton(
            isSelected = false,
            text = "Monthly",
            onClick = {}
        )
    }
}