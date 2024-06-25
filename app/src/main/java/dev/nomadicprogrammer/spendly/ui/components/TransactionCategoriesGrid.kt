package dev.nomadicprogrammer.spendly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.nomadicprogrammer.spendly.base.TransactionCategory

@Composable
fun TransactionCategoriesGrid(
    tagCategories: List<String> = TransactionCategory.entries.map { it.name },
    selectedCategory : String?,
    verticalItemSpacing : Dp = 8.dp,
    horizontalArrangement : Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    onCategorySelected : (String) -> Unit
) {
    LazyVerticalStaggeredGrid(
        modifier = Modifier.padding(vertical = 8.dp),
        columns = StaggeredGridCells.Adaptive(90.dp),
        verticalItemSpacing = verticalItemSpacing,
        horizontalArrangement = horizontalArrangement
    ) {
        items(tagCategories.size) { index ->
            val backgroundColor = if (selectedCategory != null && selectedCategory == tagCategories[index])
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.secondaryContainer
            Box(
                modifier = Modifier
                    .background(
                        backgroundColor,
                        MaterialTheme.shapes.small
                    )
                    .clickable { onCategorySelected(tagCategories[index]) }
                    .padding(8.dp)
                    .width(IntrinsicSize.Min)
            ) {
                Text(
                    text = tagCategories[index],
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Preview
@Composable
fun TransactionCategoriesGridPreview() {
    TransactionCategoriesGrid(selectedCategory = TransactionCategory.ENTERTAINMENT.name) {

    }
}
