package dev.nomadicprogrammer.spendly.smsparser.usecases.base

import dev.nomadicprogrammer.spendly.smsparser.model.Range
import dev.nomadicprogrammer.spendly.smsparser.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.model.SmsRegex

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