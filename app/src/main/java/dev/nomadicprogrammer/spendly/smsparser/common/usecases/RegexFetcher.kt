package dev.nomadicprogrammer.spendly.smsparser.common.usecases

import dev.nomadicprogrammer.spendly.smsparser.common.model.SmsRegex
import javax.inject.Inject

interface RegexProvider {
    fun getRegex() : SmsRegex?
    fun getDebitTransactionIdentifierRegex() : SmsRegex?
    fun getCreditTransactionIdentifierRegex() : SmsRegex?
}

class NetworkRegexProvider : RegexProvider {
    override fun getRegex(): SmsRegex? {
        TODO("Not yet implemented")
    }

    override fun getDebitTransactionIdentifierRegex(): SmsRegex? {
        TODO("Not yet implemented")
    }

    override fun getCreditTransactionIdentifierRegex(): SmsRegex? {
        TODO("Not yet implemented")
    }
}

class LocalRegexProvider @Inject constructor() : RegexProvider {
    override fun getRegex(): SmsRegex {
        return SmsRegex(
            positiveSenderRegex = "^[^+]{0,9}$",
            positiveMsgBodyRegex = "debited|spent|minus|deducted|withdrawn|charged"
        )
    }

    override fun getDebitTransactionIdentifierRegex(): SmsRegex {
        return SmsRegex(
            positiveMsgBodyRegex = "debited|spent|minus|deducted|withdrawn|charged"
        )
    }

    override fun getCreditTransactionIdentifierRegex(): SmsRegex {
        return SmsRegex(
            positiveMsgBodyRegex = "credited|deposited|refunded|received"
        )
    }
}