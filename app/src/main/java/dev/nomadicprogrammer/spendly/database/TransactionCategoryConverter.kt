package dev.nomadicprogrammer.spendly.database

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.Categories
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.TransactionCategory
import javax.inject.Inject

@ProvidedTypeConverter
class TransactionCategoryConverter @Inject constructor(val categories: Categories) {

    @TypeConverter
    fun fromTransactionCategory(category: TransactionCategory): String {
        return category.name
    }

    @TypeConverter
    fun toTransactionCategory(name: String): TransactionCategory {
        return when (name) {
            "Other" -> TransactionCategory.Other
            else -> if (name in categories.cashInflow.map { it.name }) TransactionCategory.CashInflow(name)
            else TransactionCategory.CashOutflow(name)
        }
    }
}