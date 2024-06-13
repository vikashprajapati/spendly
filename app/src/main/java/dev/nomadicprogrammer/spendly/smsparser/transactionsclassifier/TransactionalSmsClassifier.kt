package dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier

import android.content.Context
import android.provider.Telephony
import android.util.Log
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.nomadicprogrammer.spendly.base.DateUtils
import dev.nomadicprogrammer.spendly.base.LAST_PROCESSED_SMS
import dev.nomadicprogrammer.spendly.base.appSettings
import dev.nomadicprogrammer.spendly.smsparser.di.AmountParserQualifier
import dev.nomadicprogrammer.spendly.smsparser.di.BankNameParserQualifier
import dev.nomadicprogrammer.spendly.smsparser.di.ReceiverDetailsParserQualifier
import dev.nomadicprogrammer.spendly.smsparser.di.SenderDetailsParserQualifier
import dev.nomadicprogrammer.spendly.smsparser.di.TransactionDateParserQualifier
import dev.nomadicprogrammer.spendly.home.data.SaveTransactionsUseCase
import dev.nomadicprogrammer.spendly.smsparser.common.base.SmsUseCase
import dev.nomadicprogrammer.spendly.smsparser.common.exceptions.RegexFetchException
import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.common.model.Range
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.common.model.SmsRegex
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.LocalRegexProvider
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.RegexProvider
import dev.nomadicprogrammer.spendly.smsparser.parsers.Parser
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Transaction
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import javax.inject.Inject

enum class SmsReadPeriod(val days : Int) {
    DAILY(1), WEEKLY(7), MONTHLY(31), Quarter(90), MidYear(180), Yearly(365)
}
class TransactionalSmsClassifier @Inject constructor(
    @ApplicationContext private val context: Context,
    private val regexProvider: RegexProvider = LocalRegexProvider(),
    @AmountParserQualifier private val amountParser: Parser,
    @BankNameParserQualifier private val bankNameParser: Parser,
    @TransactionDateParserQualifier private val dateParser: Parser,
    @ReceiverDetailsParserQualifier private val receiverDetailsParser : Parser,
    @SenderDetailsParserQualifier private val senderDetailsParser : Parser,
    private val saveTransactionUseCase : SaveTransactionsUseCase,
    private val scope : CoroutineScope
) : SmsUseCase<Transaction> {
    private val TAG = TransactionalSmsClassifier::class.simpleName

    private val personalSmsExclusionRegex by lazy { regexProvider.getRegex() ?: throw RegexFetchException("Regex not found") }
    private val debitTransactionIdentifierRegex by lazy { regexProvider.getDebitTransactionIdentifierRegex() ?: throw RegexFetchException("Regex not found") }
    private val creditTransactionIdentifierRegex by lazy { regexProvider.getCreditTransactionIdentifierRegex() ?: throw RegexFetchException("Regex not found") }
    private var filteredSms: List<Transaction> = emptyList()

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

    override fun filterMap(sms: Sms): Transaction? {
        val transactionType = when {
            isDebitTransaction(sms) -> TransactionType.DEBIT
            isCreditTransaction(sms) -> TransactionType.CREDIT
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

        return Transaction.create(
            type = transactionType,
            sms = sms,
            currencyAmount = currencyAmount,
            bank = bankName,
            transactionDate = transactionDate,
            receivedFrom = if (transactionType == TransactionType.CREDIT) receiverDetailsParser.parse(sms.msgBody) else null,
            transferredTo = if (transactionType == TransactionType.DEBIT) senderDetailsParser.parse(sms.msgBody) else null
        )
    }

    private fun isDebitTransaction(sms: Sms): Boolean {
        return debitTransactionIdentifierRegex.isPositiveMsgBody(sms.msgBody)
    }

    private fun isCreditTransaction(sms: Sms): Boolean {
        return creditTransactionIdentifierRegex.isPositiveMsgBody(sms.msgBody)
    }

    override fun onComplete(filteredSms: List<Transaction>) {
        scope.launch {
            context.appSettings.edit { settings ->
                settings[LAST_PROCESSED_SMS] = try{
                    filteredSms.last().originalSms.date
                }catch (e: NoSuchElementException){
                    Log.e(TAG, "No sms found to save last processed sms date", e)
                    System.currentTimeMillis()
                }
            }
            Log.d(TAG, "Filtered Sms: $filteredSms")
            this@TransactionalSmsClassifier.filteredSms = filteredSms
            saveTransactionUseCase(filteredSms)
        }
    }

    override fun getFilteredResult(): List<Transaction> {
        return filteredSms
    }

    override fun onError(throwable: Throwable) {
        Log.e(TAG, "Error: ${throwable.message}", throwable)
    }
}