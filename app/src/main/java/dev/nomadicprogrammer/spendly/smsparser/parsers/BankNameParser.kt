package dev.nomadicprogrammer.spendly.smsparser.parsers

import javax.inject.Inject

class BankNameParser @Inject constructor() : Parser {
    private val banks = mapOf(
        "ICICI" to "ICICI",
        "HDFC" to "HDFC",
        "SBI" to "SBI",
        "AXIS" to "AXIS",
        "KOTAK" to "KOTAK",
        "BOI" to "BOI",
        "PNB" to "PNB",
        "CITI" to "CITI",
        "BOB" to "BOB",
        "UBI" to "UBI",
        "IDBI" to "IDBI",
        "YES" to "YES",
        "INDUSIND" to "INDUSIND",
        "BANK OF INDIA" to "BOI",
        "BANK OF BARODA" to "BOB",
        "UNITED BANK OF INDIA" to "UBI",
        "INDUSTRIAL DEVELOPMENT BANK OF INDIA" to "IDBI"
    )

    override fun parse(text: String): String? {
        return banks.entries.firstOrNull { it.key in text }?.value
    }
}