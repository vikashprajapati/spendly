package dev.nomadicprogrammer.spendly.notification.categories

import dev.nomadicprogrammer.spendly.smsparser.common.model.TransactionCategory

class TransactionCategoryProvider {
    companion object {
        val categories = TransactionCategory.entries.map { it.name }
    }
}