package dev.nomadicprogrammer.spendly.smsparser.common.data

import dev.nomadicprogrammer.spendly.smsparser.common.model.Range
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import kotlinx.coroutines.flow.Flow

interface SmsDataSource {
    fun readSms(range: Range, sortOrder : String) : Flow<Triple<Int, Int, Sms>>

    fun getSmsById(id: Int) : Sms?

    fun getSmsByIds(ids: List<Int>) : List<Sms>?
}