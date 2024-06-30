package dev.nomadicprogrammer.spendly.smsparser.common.model

data class Sms(
    val id: String,
    val senderId : String,
    val msgBody : String,
    val date : Long
)