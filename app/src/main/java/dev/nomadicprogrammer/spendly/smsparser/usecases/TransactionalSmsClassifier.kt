package dev.nomadicprogrammer.spendly.smsparser.usecases

import android.provider.Telephony
import android.util.Log
import dev.nomadicprogrammer.spendly.smsparser.exceptions.RegexFetchException
import dev.nomadicprogrammer.spendly.smsparser.model.CREDIT
import dev.nomadicprogrammer.spendly.smsparser.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.model.DEBIT
import dev.nomadicprogrammer.spendly.smsparser.model.Range
import dev.nomadicprogrammer.spendly.smsparser.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.model.SmsRegex
import dev.nomadicprogrammer.spendly.smsparser.model.TransactionalSms
import dev.nomadicprogrammer.spendly.smsparser.parsers.Parser
import dev.nomadicprogrammer.spendly.smsparser.usecases.base.SmsUseCase
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
        val currencyAmount = CurrencyAmount.parse(sms.msgBody, amountParser)
        val bankName = bankNameParser.parse(sms.msgBody)
        val transactionDate = dateParser.parse(sms.msgBody)

        val transactionType = when {
            isDebitTransaction(sms) -> DEBIT
            isCreditTransaction(sms) -> CREDIT
            else -> "none"
        }

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

    private fun createDebitTransaction(
        sms: Sms,
        currencyAmount: CurrencyAmount,
        bank: String?,
        transactionDate: String?
    ): TransactionalSms {
        return TransactionalSms.Debit(
            transactionDate = transactionDate, transferredTo = "", bankName = bank,
            currencyAmount = currencyAmount, originalSms = sms,
        )
    }

    private fun createCreditTransaction(
        sms: Sms,
        currencyAmount: CurrencyAmount,
        bank: String?,
        transactionDate: String?
    ): TransactionalSms {
        return TransactionalSms.Credit(
            transactionDate = transactionDate, receivedFrom = "", bankName = bank,
            currencyAmount = currencyAmount, originalSms = sms
        )
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