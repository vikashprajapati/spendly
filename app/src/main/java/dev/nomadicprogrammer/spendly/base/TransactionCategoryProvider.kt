package dev.nomadicprogrammer.spendly.base

class TransactionCategoryProvider {
    companion object {
        val categories = TransactionCategory.allCategories

        fun provideCategoriesForNotificationActions(): List<TransactionCategory> {
            return categories.shuffled().take(3) // TODO: Enhance to use a more sophisticated algorithm
        }
    }
}