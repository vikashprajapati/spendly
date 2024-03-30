package dev.nomadicprogrammer.spendly.smsparser.parsers

interface Parser {
    fun parse(text: String): String?
}