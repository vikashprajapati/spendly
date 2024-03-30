package dev.nomadicprogrammer.spendly.smsparser.usecases.base

import dev.nomadicprogrammer.spendly.smsparser.model.Range
import dev.nomadicprogrammer.spendly.smsparser.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.model.SmsRegex

interface SmsUseCase {
    fun readSmsRange() : Range
    fun inboxReadSortOrder(): String
    fun onProgress(progress: Int)

    fun filter(sms : Sms): Boolean
    fun onComplete(filteredSms: List<Sms>)
    fun onError(throwable: Throwable)
}