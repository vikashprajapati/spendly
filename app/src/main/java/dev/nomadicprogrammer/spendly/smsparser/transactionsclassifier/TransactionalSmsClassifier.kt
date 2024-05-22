package dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier

import android.content.Context
import android.provider.Telephony
import android.util.Log
import androidx.datastore.preferences.core.edit
import dev.nomadicprogrammer.spendly.base.DateUtils
import dev.nomadicprogrammer.spendly.base.LAST_PROCESSED_SMS
import dev.nomadicprogrammer.spendly.base.appSettings
import dev.nomadicprogrammer.spendly.home.data.StoreTransactionUseCase
import dev.nomadicprogrammer.spendly.smsparser.common.base.SmsUseCase
import dev.nomadicprogrammer.spendly.smsparser.common.exceptions.RegexFetchException
import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.common.model.Range
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.common.model.SmsRegex
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.LocalRegexProvider
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.RegexProvider
import dev.nomadicprogrammer.spendly.smsparser.parsers.Parser
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.CREDIT
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.DEBIT
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Calendar

enum class SmsReadPeriod(val days : Int) {
    DAILY(1), WEEKLY(7), MONTHLY(31), Quarter(90), MidYear(180), Yearly(365)
}
class TransactionalSmsClassifier(
    private val context: Context,
    private val regexProvider: RegexProvider = LocalRegexProvider(),
    private val amountParser: Parser,
    private val bankNameParser: Parser,
    private val dateParser: Parser,
    private val receiverDetailsParser : Parser,
    private val senderDetailsParser : Parser,
    private val storeTransactionUseCase: StoreTransactionUseCase,
    private val scope : CoroutineScope
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

    override fun readSmsRange(smsReadPeriod: SmsReadPeriod): Range {
        val from = runBlocking {
            scope.async {
                context.appSettings.data
                    .first()[LAST_PROCESSED_SMS]
                    ?: run {
                        Log.d(TAG, "Last processed sms not found, reading sms for last ${smsReadPeriod.days} days")
                        Calendar.getInstance().run {
                            add(Calendar.DAY_OF_YEAR, -smsReadPeriod.days)
                            time.time
                        }
                    }
            }.await()
        }

        return Range(from, System.currentTimeMillis())
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

        val currencyAmount = CurrencyAmount.parse(sms.msgBody, amountParser)?:run {
            Log.d(TAG, "Currency amount not found in sms: ${sms.msgBody}, sms doesn't seems valid")
            return null
        }
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
            transactionDate = transactionDate,
            receivedFrom = if (transactionType == CREDIT) receiverDetailsParser.parse(sms.msgBody) else null,
            transferredTo = if (transactionType == DEBIT) senderDetailsParser.parse(sms.msgBody) else null
        )
    }

    private fun isDebitTransaction(sms: Sms): Boolean {
        return debitTransactionIdentifierRegex.isPositiveMsgBody(sms.msgBody)
    }

    private fun isCreditTransaction(sms: Sms): Boolean {
        return creditTransactionIdentifierRegex.isPositiveMsgBody(sms.msgBody)
    }

    override fun onComplete(filteredSms: List<TransactionalSms>) {
        scope.launch {
            context.appSettings.edit { settings ->
                settings[LAST_PROCESSED_SMS] = filteredSms.last().originalSms.date
            }
            Log.d(TAG, "Filtered Sms: $filteredSms")
            this@TransactionalSmsClassifier.filteredSms = filteredSms
            storeTransactionUseCase.saveTransactions(filteredSms.mapNotNull { it.mapToTransaction() })
        }
    }

    override fun getFilteredResult(): List<TransactionalSms> {
        return filteredSms
    }

    override fun onError(throwable: Throwable) {
        Log.e(TAG, "Error: ${throwable.message}", throwable)
    }
}