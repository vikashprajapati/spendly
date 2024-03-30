package dev.nomadicprogrammer.spendly.smsparser.common.base

import dev.nomadicprogrammer.spendly.smsparser.common.model.Range
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.common.model.SmsRegex

interface SmsUseCase<T> {
    fun readSmsRange() : Range
    fun inboxReadSortOrder(): String
    fun getRegex() : SmsRegex
    fun onProgress(progress: Int)
    fun filterMap(sms : Sms): T?
    fun onComplete(filteredSms: List<T>)

    fun getFilteredResult(): List<T>

    fun onError(throwable: Throwable)
}