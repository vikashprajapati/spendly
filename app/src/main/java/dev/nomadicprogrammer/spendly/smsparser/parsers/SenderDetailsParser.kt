package dev.nomadicprogrammer.spendly.smsparser.parsers

import java.util.regex.Pattern

class SenderDetailsParser : Parser {
    private val regex = "\\s(transfer|trf)\\sto\\b\\s(\\w+)\\b"
    private val pattern: Pattern = Pattern.compile(regex)
    override fun parse(text: String): String? {
        val matcher = pattern.matcher(text)
        return if (matcher.find()) {
            matcher.group(2)
        } else {
            null
        }
    }
}