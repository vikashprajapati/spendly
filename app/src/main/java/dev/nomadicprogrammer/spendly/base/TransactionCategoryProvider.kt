package dev.nomadicprogrammer.spendly.base

class TransactionCategoryProvider {
    companion object {
        val categories = TransactionCategory.entries.map { it.name }

        fun provideCategoriesForNotificationActions(): List<String> {
            return categories.shuffled().take(3) // TODO: Enhance to use a more sophisticated algorithm
        }
    }
}