package dev.nomadicprogrammer.spendly.smsparser.parsers

import java.util.regex.Pattern
import javax.inject.Inject

class DateParser @Inject constructor() : Parser{
    private val regex : String = "\\s\\d{1,2}[-\\/.]?([a-zA-Z]{3}|0[1-9]|[1-12]{2})[-\\/.]?\\d{2,4}\\s"
    private val pattern : Pattern = Pattern.compile(regex)

    override fun parse(text: String): String? {
        val matcher = pattern.matcher(text)
        return if (matcher.find()) {
            matcher.group().trim()
        } else {
            null
        }
    }
}