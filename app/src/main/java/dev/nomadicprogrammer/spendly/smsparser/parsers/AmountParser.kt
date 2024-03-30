package dev.nomadicprogrammer.spendly.smsparser.parsers

import java.util.regex.Pattern

class AmountParser : Parser {
    private val amountRegex = "[([rR][sS])|\$|₹]?(\\s+(\\d+(\\.\\d{1,2})?)\\s+)"
    private val pattern: Pattern = Pattern.compile(amountRegex)
    override fun parse(text: String): String? {
        val matcher = pattern.matcher(text)

        return if (matcher.find()) {
            "${matcher.group(1)} ${matcher.group(2)}"
        } else {
            null
        }
    }
}