package dev.nomadicprogrammer.spendly.smsparser.parsers

import android.util.Log
import java.util.regex.Pattern

class ReceiverDetailsParser : Parser {
    private val regex = "\\s(transfer|trf)\\sto\\b\\s(\\w+(\\s*\\w+)*)"
    private val pattern: Pattern = Pattern.compile(regex)
    override fun parse(text: String): String? {
        val matcher = pattern.matcher(text)
        return if (matcher.find()) {
            val h = matcher.group(2)?.split("ref\\w*", "Ref\\w*", "REF\\w*")
            Log.d("ReceiverDetailsParser", "parse: $h")
            h?.first()?.trim()
        } else {
            null
        }
    }
}