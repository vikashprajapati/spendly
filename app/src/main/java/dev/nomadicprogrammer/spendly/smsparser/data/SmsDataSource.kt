package dev.nomadicprogrammer.spendly.smsparser.data

import dev.nomadicprogrammer.spendly.smsparser.model.Range
import dev.nomadicprogrammer.spendly.smsparser.model.Sms
import kotlinx.coroutines.flow.Flow

interface SmsDataSource {
    fun readSms(range: Range, sortOrder : String) : Flow<Triple<Int, Int, Sms>>
}