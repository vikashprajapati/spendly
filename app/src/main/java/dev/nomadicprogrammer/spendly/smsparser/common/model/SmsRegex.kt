package dev.nomadicprogrammer.spendly.smsparser.common.model

import java.util.regex.Pattern

class SmsRegex(
    val positiveSenderRegex : String? = null,
    val negativeSenderRegex : String? = null,
    val positiveMsgBodyRegex : String? = null,
    val negativeMsgBodyRegex : String? = null
) {
    fun isPositiveSender(senderId : String) : Boolean {
        return positiveSenderRegex?.let { Filter.isMatch(senderId, it) } ?: true
    }

    fun isNegativeSender(senderId : String) : Boolean {
        return negativeSenderRegex?.let { Filter.isMatch(senderId, it) } ?: false
    }

    fun isPositiveMsgBody(msgBody : String) : Boolean {
        return positiveMsgBodyRegex?.let { Filter.isMatch(msgBody, it) } ?: true
    }

    fun isNegativeMsgBody(msgBody : String) : Boolean {
        return negativeMsgBodyRegex?.let { Filter.isMatch(msgBody, it) } ?: false
    }
}

object Filter{
    fun isMatch(text : String, regex: String) : Boolean{
        val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(text)
        return matcher.find()
    }
}