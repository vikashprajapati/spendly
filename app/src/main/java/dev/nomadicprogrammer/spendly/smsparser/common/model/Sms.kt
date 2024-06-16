package dev.nomadicprogrammer.spendly.smsparser.common.model

data class Sms(
    val id: String,
    val senderId : String,
    val msgBody : String,
    val date : Long
)

val DEFAULT_UNDEFINED_SMS = Sms(
    id = "-1",
    senderId = "Unknown",
    msgBody = "Unable to find linked SMS ",
    date = System.currentTimeMillis()
)