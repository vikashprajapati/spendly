package dev.nomadicprogrammer.spendly.smsparser.parsers

import dev.nomadicprogrammer.spendly.smsparser.model.Sms

class BankNameParser : Parser {
    override fun parse(text: String): String? {
        return when {
            text.contains("ICICI") -> "ICICI"
            text.contains("HDFC") -> "HDFC"
            text.contains("SBI") -> "SBI"
            text.contains("AXIS") -> "AXIS"
            text.contains("KOTAK") -> "KOTAK"
            text.contains("BOI") -> "BOI"
            text.contains("PNB") -> "PNB"
            text.contains("CITI") -> "CITI"
            text.contains("BOB") -> "BOB"
            text.contains("UBI") -> "UBI"
            text.contains("IDBI") -> "IDBI"
            text.contains("YES") -> "YES"
            text.contains("INDUSIND") -> "INDUSIND"
            text.contains("BANK OF INDIA") -> "BOI"
            text.contains("BANK OF BARODA") -> "BOB"
            text.contains("UNITED BANK OF INDIA") -> "UBI"
            text.contains("INDUSTRIAL DEVELOPMENT BANK OF INDIA") -> "IDBI"
            else -> null
        }
    }
}