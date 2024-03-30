package dev.nomadicprogrammer.spendly.smsparser.usecases

import dev.nomadicprogrammer.spendly.smsparser.model.SmsRegex

interface RegexProvider {
    fun getRegex() : SmsRegex?
}

class NetworkRegexProvider : RegexProvider {
    override fun getRegex(): SmsRegex? {
        TODO("Not yet implemented")
    }
}

class LocalRegexProvider : RegexProvider {
    override fun getRegex(): SmsRegex {
        return SmsRegex(
            positiveSenderRegex = "^[^+]{0,9}$"
        )
    }
}