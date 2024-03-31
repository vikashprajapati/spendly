package dev.nomadicprogrammer.spendly.smsparser.parsers

import java.util.regex.Pattern

class AmountParser : Parser {
    private val amountRegex = "(\\s(([rR][sS])\\s)|\\\$|₹|[iInNrR]{3})?((?:\\d{1,3}(?:,\\d{1,3})*(?:,\\d{1,3})?)?(?:\\.\\d{1,2}))"
    private val pattern: Pattern = Pattern.compile(amountRegex)
    override fun parse(text: String): String? {
        val matcher = pattern.matcher(text)

        return if (matcher.find()) {
            val amountWithCurrency = matcher.group()
            val currency = matcher.group(1)?.trim()
            val amount = matcher.group(2)?.trim()

            return if (currency != null && amount != null) {
                "$currency $amount"
            } else amount ?: amountWithCurrency
        } else {
            null
        }
    }
}