package dev.nomadicprogrammer.spendly.base

import android.content.Context
import androidx.compose.ui.graphics.Color
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.TransactionCategory
import dev.nomadicprogrammer.spendly.ui.theme.brighten
import dev.nomadicprogrammer.spendly.ui.theme.randomPastelColor

class TransactionCategoryResourceProvider(@ApplicationContext private val context: Context) {
    fun getResource(transactionCategory: TransactionCategory): TransactionCategoryResource {
        val name = transactionCategory.name
        return TransactionCategoryResource(transactionCategory, getIcon(name), getColor(name))
    }

    private fun getColor(name: String): Color {
        return randomPastelColor()
    }

    private fun getIcon(name: String): Int {
        val found = context.resources.getIdentifier("${name}_icon".lowercase(), "drawable", context.packageName)
        if (found == 0) {
            return context.resources.getIdentifier("dollar_icon", "drawable", context.packageName)
        }
        return found
    }
}

data class TransactionCategoryResource(val transactionCategory: TransactionCategory, val icon: Int, val color: Color, val iconTint : Color = color.brighten())