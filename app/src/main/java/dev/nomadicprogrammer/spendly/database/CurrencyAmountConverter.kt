package dev.nomadicprogrammer.spendly.database

import androidx.room.TypeConverter
import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount

class CurrencyAmountConverter {
    @TypeConverter
    fun fromCurrencyAmount(amount: CurrencyAmount): String {
        return "${amount.amount},${amount.currency}" // Adjust format as needed
    }

    @TypeConverter
    fun toCurrencyAmount(data: String): CurrencyAmount {
        val parts = data.split(",")
        return CurrencyAmount(parts[0], parts[1].toDouble())
    }
}