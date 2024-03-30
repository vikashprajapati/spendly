package dev.nomadicprogrammer.spendly.smsparser.model

class SmsRegex(
    val positiveSenderRegex : String? = null,
    val negativeSenderRegex : String? = null,
    val positiveMsgBodyRegex : String? = null,
    val negativeMsgBodyRegex : String? = null
) {
    fun isPositiveSender(senderId : String) : Boolean {
        return positiveSenderRegex?.toRegex()?.matches(senderId) ?: true
    }

    fun isNegativeSender(senderId : String) : Boolean {
        return negativeSenderRegex?.toRegex()?.matches(senderId) ?: false
    }

    fun isPositiveMsgBody(msgBody : String) : Boolean {
        return positiveMsgBodyRegex?.toRegex()?.matches(msgBody) ?: true
    }

    fun isNegativeMsgBody(msgBody : String) : Boolean {
        return negativeMsgBodyRegex?.toRegex()?.matches(msgBody) ?: false
    }
}