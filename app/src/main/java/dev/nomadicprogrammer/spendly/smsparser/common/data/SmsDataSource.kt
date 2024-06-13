package dev.nomadicprogrammer.spendly.smsparser.common.data

import dev.nomadicprogrammer.spendly.smsparser.common.base.SmsUseCase
import dev.nomadicprogrammer.spendly.smsparser.common.model.Range
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.TransactionalSmsClassifier
import kotlinx.coroutines.flow.Flow

interface SmsDataSource<T> {
    fun <T> readSms(transactionalSmsClassifier: SmsUseCase<T>) : Flow<Triple<Int, Int, Sms>>

    fun getSmsById(id: Int) : Sms?

    fun getSmsByIds(ids: List<Int>) : List<Sms>?
}