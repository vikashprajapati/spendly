package dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier

import android.provider.Telephony
import android.util.Log
import dev.nomadicprogrammer.spendly.base.DateUtils
import dev.nomadicprogrammer.spendly.smsparser.common.exceptions.RegexFetchException
import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.common.model.Range
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.common.model.SmsRegex
import dev.nomadicprogrammer.spendly.smsparser.parsers.Parser
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.RegexProvider
import dev.nomadicprogrammer.spendly.smsparser.common.base.SmsUseCase
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.CREDIT
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.DEBIT
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
import java.util.Calendar

class TransactionalSmsClassifier(
    private val regexProvider: RegexProvider,
    private val amountParser: Parser,
    private val bankNameParser: Parser,
    private val dateParser: Parser
) : SmsUseCase<TransactionalSms> {
    private val TAG = TransactionalSmsClassifier::class.simpleName

    private val personalSmsExclusionRegex by lazy { regexProvider.getRegex() ?: throw RegexFetchException("Regex not found") }
    private val debitTransactionIdentifierRegex by lazy { regexProvider.getDebitTransactionIdentifierRegex() ?: throw RegexFetchException("Regex not found") }
    private val creditTransactionIdentifierRegex by lazy { regexProvider.getCreditTransactionIdentifierRegex() ?: throw RegexFetchException("Regex not found") }
    private var filteredSms: List<TransactionalSms> = emptyList()

    override fun getRegex(): SmsRegex {
        return personalSmsExclusionRegex
    }

    override fun inboxReadSortOrder(): String {
        return "${ Telephony.Sms.Inbox.DATE} ASC"
    }

    override fun readSmsRange(): Range {
        val sixMonthsBefore = Calendar.getInstance().run {
            add(Calendar.DAY_OF_MONTH, -10)
            time
        }
        return Range(sixMonthsBefore.time, System.currentTimeMillis())
    }

    override fun onProgress(progress: Int) {
        Log.d(TAG, "Progress: $progress")
    }

    override fun filterMap(sms: Sms): TransactionalSms? {
        val transactionType = when {
            isDebitTransaction(sms) -> DEBIT
            isCreditTransaction(sms) -> CREDIT
            else -> return null
        }

        val currencyAmount = CurrencyAmount.parse(sms.msgBody, amountParser)
        val bankName = bankNameParser.parse(sms.msgBody)?:run {
            Log.d(TAG, "Bank name not found in sms: ${sms.msgBody}, sms doesn't seems valid")
            return null
        }
        val transactionDate = dateParser.parse(sms.msgBody)?.let { DateUtils.Local.getFormattedDate(it) }

        return TransactionalSms.create(
            type = transactionType,
            sms = sms,
            currencyAmount = currencyAmount,
            bank = bankName,
            transactionDate = transactionDate
        )
    }

    private fun isDebitTransaction(sms: Sms): Boolean {
        return debitTransactionIdentifierRegex.isPositiveMsgBody(sms.msgBody)
    }

    private fun isCreditTransaction(sms: Sms): Boolean {
        return creditTransactionIdentifierRegex.isPositiveMsgBody(sms.msgBody)
    }

    override fun onComplete(filteredSms: List<TransactionalSms>) {
        Log.d(TAG, "Filtered Sms: $filteredSms")
        this.filteredSms = filteredSms
    }

    override fun getFilteredResult(): List<TransactionalSms> {
        return filteredSms
    }

    override fun onError(throwable: Throwable) {
        Log.e(TAG, "Error: ${throwable.message}", throwable)
    }
}