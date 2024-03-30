package dev.nomadicprogrammer.spendly.smsparser.usecases

import android.provider.Telephony
import android.util.Log
import dev.nomadicprogrammer.spendly.smsparser.exceptions.RegexFetchException
import dev.nomadicprogrammer.spendly.smsparser.model.Range
import dev.nomadicprogrammer.spendly.smsparser.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.usecases.base.SmsUseCase
import java.util.Calendar

class SpendAnalyser(
    private val regexProvider: RegexProvider
) : SmsUseCase {
    private val TAG = SpendAnalyser::class.simpleName

    val personalSmsExclusionRegex by lazy { regexProvider.getRegex() ?: throw RegexFetchException("Regex not found") }
    private val debitTransactionIdentifierRegex by lazy { regexProvider.getDebitTransactionIdentifierRegex() ?: throw RegexFetchException("Regex not found") }
    private val creditTransactionIdentifierRegex by lazy { regexProvider.getCreditTransactionIdentifierRegex() ?: throw RegexFetchException("Regex not found") }

    private var totalDebitAmount : Long = 0L
    private val debitTransactionList : MutableList<Sms> = mutableListOf()

    private var totalCreditAmount : Long = 0L
    private val creditTransactionList : MutableList<Sms> = mutableListOf()

    override fun inboxReadSortOrder(): String {
        return "${ Telephony.Sms.Inbox.DATE} ASC"
    }

    override fun readSmsRange(): Range {
        val sixMonthsBefore = Calendar.getInstance().run {
            add(Calendar.DAY_OF_MONTH, -2)
            time
        }
        return Range(sixMonthsBefore.time, System.currentTimeMillis())
    }

    override fun onProgress(progress: Int) {
        Log.d(TAG, "Progress: $progress")
    }

    override fun filter(sms: Sms): Boolean {
        val isDebitTransaction = debitTransactionIdentifierRegex.isPositiveMsgBody(sms.msgBody)
        if (isDebitTransaction){

            return true
        }
        val isCreditTransaction = creditTransactionIdentifierRegex.isPositiveMsgBody(sms.msgBody)
        if (isCreditTransaction){

            return true
        }

        return false
    }

    override fun onComplete(filteredSms: List<Sms>) {
        Log.d(TAG, "Filtered Sms: $filteredSms")
    }

    override fun onError(throwable: Throwable) {
        Log.e(TAG, "Error: ${throwable.message}", throwable)
    }
}