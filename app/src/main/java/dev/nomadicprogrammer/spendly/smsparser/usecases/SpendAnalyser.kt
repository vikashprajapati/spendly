package dev.nomadicprogrammer.spendly.smsparser.usecases

import android.provider.Telephony
import android.util.Log
import dev.nomadicprogrammer.spendly.smsparser.exceptions.RegexFetchException
import dev.nomadicprogrammer.spendly.smsparser.model.Range
import dev.nomadicprogrammer.spendly.smsparser.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.model.SmsRegex
import dev.nomadicprogrammer.spendly.smsparser.usecases.base.SmsUseCase
import java.util.Calendar

class SpendAnalyser(
    val regexProvider: RegexProvider
) : SmsUseCase {
    private val TAG = SpendAnalyser::class.simpleName

    val regex by lazy { regexProvider.getRegex() ?: throw RegexFetchException("Regex not found") }

    override fun inboxReadSortOrder(): String {
        return "${ Telephony.Sms.Inbox.DATE} ASC"
    }

    override fun readSmsRange(): Range {
        val sixMonthsBefore = Calendar.getInstance().run {
            add(Calendar.DAY_OF_MONTH, -3)
            time
        }
        return Range(sixMonthsBefore.time, System.currentTimeMillis())
    }

    override fun onProgress(progress: Int) {
        Log.d(TAG, "Progress: $progress")
    }

    override fun onComplete(filteredSms: List<Sms>) {
        Log.d(TAG, "Filtered Sms: $filteredSms")
    }

    override fun onError(throwable: Throwable) {
        Log.e(TAG, "Error: ${throwable.message}", throwable)
    }
}