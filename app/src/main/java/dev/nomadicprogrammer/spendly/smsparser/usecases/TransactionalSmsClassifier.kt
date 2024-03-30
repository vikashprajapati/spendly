package dev.nomadicprogrammer.spendly.smsparser.usecases

import android.provider.Telephony
import android.util.Log
import dev.nomadicprogrammer.spendly.smsparser.exceptions.RegexFetchException
import dev.nomadicprogrammer.spendly.smsparser.model.DEFAULT_CURRENCY
import dev.nomadicprogrammer.spendly.smsparser.model.Range
import dev.nomadicprogrammer.spendly.smsparser.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.model.TransactionalSms
import dev.nomadicprogrammer.spendly.smsparser.parsers.Parser
import dev.nomadicprogrammer.spendly.smsparser.usecases.base.SmsUseCase
import java.util.Calendar

class TransactionalSmsClassifier(
    private val regexProvider: RegexProvider,
    private val amountParser: Parser
) : SmsUseCase {
    private val TAG = TransactionalSmsClassifier::class.simpleName

    val personalSmsExclusionRegex by lazy { regexProvider.getRegex() ?: throw RegexFetchException("Regex not found") }
    private val debitTransactionIdentifierRegex by lazy { regexProvider.getDebitTransactionIdentifierRegex() ?: throw RegexFetchException("Regex not found") }
    private val creditTransactionIdentifierRegex by lazy { regexProvider.getCreditTransactionIdentifierRegex() ?: throw RegexFetchException("Regex not found") }

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

    override fun <TransactionalSms> filterMap(sms: Sms): TransactionalSms? {
        val currencyAmount = parseCurrencyAmount(sms.msgBody)

        return when {
            isDebitTransaction(sms) -> createDebitTransaction(sms = sms, currencyAmount = currencyAmount) as TransactionalSms
            isCreditTransaction(sms) -> createCreditTransaction(sms, currencyAmount = currencyAmount) as TransactionalSms
            else -> null
        }
    }

    private fun isDebitTransaction(sms: Sms): Boolean {
        return debitTransactionIdentifierRegex.isPositiveMsgBody(sms.msgBody)
    }

    private fun isCreditTransaction(sms: Sms): Boolean {
        return creditTransactionIdentifierRegex.isPositiveMsgBody(sms.msgBody)
    }

    private fun createDebitTransaction(sms: Sms,  currencyAmount: CurrencyAmount): TransactionalSms {
        return TransactionalSms.Credit(
            transactionDate = "", receivedFrom = "", bankName = "",
            currencyAmount = currencyAmount, originalSms = sms
        )
    }

    private fun createCreditTransaction(sms: Sms, currencyAmount: CurrencyAmount): TransactionalSms {
        return TransactionalSms.Credit(
            transactionDate = "", receivedFrom = "", bankName = "",
            currencyAmount = currencyAmount, originalSms = sms
        )
    }

    override fun <TransactionalSms> onComplete(filteredSms: List<TransactionalSms>) {
        Log.d(TAG, "Filtered Sms: $filteredSms")
    }

    override fun onError(throwable: Throwable) {
        Log.e(TAG, "Error: ${throwable.message}", throwable)
    }

    data class CurrencyAmount(val currency: String = DEFAULT_CURRENCY, val amount: Double?)

    fun parseCurrencyAmount(messageBody: String): CurrencyAmount {
        val parts = amountParser.parse(messageBody)?.split("\\s")
        return if (parts != null && parts.size == 2) {
            CurrencyAmount(currency = parts[0], amount = parts[1].toDoubleOrNull())
        } else {
            CurrencyAmount(amount = parts?.get(0)?.toDoubleOrNull())
        }
    }
}