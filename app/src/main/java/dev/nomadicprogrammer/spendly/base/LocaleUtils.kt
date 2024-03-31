package dev.nomadicprogrammer.spendly.base

import java.text.NumberFormat
import java.util.Locale

object LocaleUtils {
    val currentLocale = Locale("en", "IN")
    val localeNumberFormat: NumberFormat = NumberFormat.getInstance(currentLocale)

    fun parseNumber(number: String): Result<Double> {
        return localeNumberFormat.parse(number)?.toDouble()
            ?.let { Result.success(it) }
            ?: Result.failure(NumberFormatException("Cannot parse $number"))
    }
}