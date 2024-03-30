package dev.nomadicprogrammer.spendly.smsparser.model

import dev.nomadicprogrammer.spendly.smsparser.parsers.Parser

data class CurrencyAmount(val currency: String = DEFAULT_CURRENCY, val amount: Double?){

    companion object CurrencyAmountParser{
        fun parse(messageBody: String, amountParser : Parser): CurrencyAmount {
            val parts = amountParser.parse(messageBody)?.split("\\s")
            return if (parts != null && parts.size == 2) {
                CurrencyAmount(currency = parts[0], amount = parts[1].toDoubleOrNull())
            } else {
                CurrencyAmount(amount = parts?.get(0)?.toDoubleOrNull())
            }
        }
    }
}
