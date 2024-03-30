package dev.nomadicprogrammer.spendly.smsparser.parsers

import java.util.regex.Pattern

class DateParser : Parser{
    private val regex : String = "\\s\\d{1,2}[-/.]?([a-zA-Z]{3}|[1-12])[-/.]?\\d{2,4}"
    private val pattern : Pattern = Pattern.compile(regex)

    override fun parse(text: String): String? {
        val matcher = pattern.matcher(text)
        return if (matcher.find()) {
            matcher.group()
        } else {
            null
        }
    }
}