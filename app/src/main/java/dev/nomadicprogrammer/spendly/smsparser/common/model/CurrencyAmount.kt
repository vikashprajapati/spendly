package dev.nomadicprogrammer.spendly.smsparser.common.model

import dev.nomadicprogrammer.spendly.smsparser.parsers.Parser
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.DEFAULT_CURRENCY
import java.text.NumberFormat
import java.util.Locale

data class CurrencyAmount(val currency: String = DEFAULT_CURRENCY, val amount: Double?){

    companion object CurrencyAmountParser{
        fun parse(messageBody: String, amountParser : Parser): CurrencyAmount {
            val parts = amountParser.parse(messageBody)?.split("\\s")
            val indianLocale = Locale("en", "IN")
            val indianFormat: NumberFormat = NumberFormat.getInstance(indianLocale)

            return if (parts != null && parts.size == 2) {
                CurrencyAmount(currency = parts[0], amount = parts[1].toDoubleOrNull())
            } else {
                val amountNumber: Double? = parts?.get(0)?.let {
                    indianFormat.parse(it)?.toDouble() ?: it.toDoubleOrNull()
                }
                CurrencyAmount(amount = amountNumber)
            }
        }
    }
}
