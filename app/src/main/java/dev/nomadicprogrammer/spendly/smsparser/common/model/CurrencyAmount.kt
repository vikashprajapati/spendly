package dev.nomadicprogrammer.spendly.smsparser.common.model

import dev.nomadicprogrammer.spendly.base.LocaleUtils
import dev.nomadicprogrammer.spendly.smsparser.parsers.Parser
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.DEFAULT_CURRENCY
import java.text.NumberFormat
import java.util.Locale

data class CurrencyAmount(val currency: String = DEFAULT_CURRENCY, val amount: Double){

    override fun toString(): String {
        return "$currency $amount"
    }

    companion object CurrencyAmountParser{
        fun parse(messageBody: String, amountParser : Parser): CurrencyAmount? {
            val parts = amountParser.parse(messageBody)?.split(" ")

            return if (parts != null && parts.size == 2) {
                val amountNumber: Double = parts[1].let { amount ->
                    LocaleUtils.parseNumber(amount).getOrNull()
                }?:return null
                CurrencyAmount(currency = parts[0], amount = amountNumber)
            } else {
                val amountNumber: Double = parts?.get(0)?.let { amount ->
                    LocaleUtils.parseNumber(amount).getOrNull()
                }?:return null
                CurrencyAmount(amount = amountNumber)
            }
        }
    }
}
