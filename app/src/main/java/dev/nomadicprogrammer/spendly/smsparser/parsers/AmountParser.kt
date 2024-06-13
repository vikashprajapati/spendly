package dev.nomadicprogrammer.spendly.smsparser.parsers

import java.util.regex.Pattern
import javax.inject.Inject

class AmountParser @Inject constructor() : Parser {
    private val amountRegex = "(\\s(([rR][sS])\\s)|\\$\\s|₹\\s|[iInNrR]{3}\\s)?((?:\\d+(?:,\\d+)*(?:,\\d+)?)?(?:\\.\\d{1,2}))"
    private val pattern: Pattern = Pattern.compile(amountRegex)
    override fun parse(text: String): String? {
        val matcher = pattern.matcher(text)

        return if (matcher.find()) {
            val amountWithCurrency = matcher.group()
            val currency = matcher.group(1)?.trim()
            val amount = matcher.group(4)?.trim()

            return if (currency != null && amount != null) {
                "$currency $amount"
            } else amount ?: amountWithCurrency
        } else {
            null
        }
    }
}