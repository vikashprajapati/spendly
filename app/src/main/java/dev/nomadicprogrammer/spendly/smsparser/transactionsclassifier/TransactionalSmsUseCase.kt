package dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier

import android.content.Context
import android.provider.Telephony
import android.util.Log
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.nomadicprogrammer.spendly.base.DateUtils
import dev.nomadicprogrammer.spendly.base.LAST_PROCESSED_SMS
import dev.nomadicprogrammer.spendly.base.appSettings
import dev.nomadicprogrammer.spendly.home.data.SaveTransactionsUseCase
import dev.nomadicprogrammer.spendly.smsparser.common.base.SmsUseCase
import dev.nomadicprogrammer.spendly.smsparser.common.exceptions.RegexFetchException
import dev.nomadicprogrammer.spendly.smsparser.common.model.Range
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.common.model.SmsRegex
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.LocalRegexProvider
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.RegexProvider
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import javax.inject.Inject

enum class SmsReadPeriod(val days : Int) {
    DAILY(1), WEEKLY(7), MONTHLY(31), Quarter(90), MidYear(180), Yearly(365)
}
class TransactionalSmsUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val regexProvider: RegexProvider = LocalRegexProvider(),
    private val transactionSmsClassifier: TransactionSmsClassifier,
    private val saveTransactionUseCase : SaveTransactionsUseCase,
    private val scope : CoroutineScope
) : SmsUseCase<Transaction> {
    private val TAG = TransactionalSmsUseCase::class.simpleName

    private val personalSmsExclusionRegex by lazy { regexProvider.getRegex() ?: throw RegexFetchException("Regex not found") }
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
                val lastTimeRun = context.appSettings.data.firstOrNull()?.get(LAST_PROCESSED_SMS)
                Log.d(TAG, "Last processed sms: $lastTimeRun")
                lastTimeRun ?: run {
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
        Log.d(TAG, "Filtering sms: ${DateUtils.Local.formattedDateWithTimeFromTimestamp(sms.date)}: $sms")
        return transactionSmsClassifier.classify(sms)
    }

    override fun onComplete(filteredSms: List<Transaction>) {
        Log.d(TAG, "onComplete")
        scope.launch {
            if (filteredSms.isEmpty()) {
                context.appSettings.edit { settings->
                    settings[LAST_PROCESSED_SMS] = System.currentTimeMillis()
                }
                return@launch
            }
            Log.d(TAG, "Saving transactions: $filteredSms")
            context.appSettings.edit { settings ->
                settings[LAST_PROCESSED_SMS] = filteredSms.last().originalSms?.date?: System.currentTimeMillis()
            }
            Log.d(TAG, "last processed time updated: ${context.appSettings.data.firstOrNull()?.get(LAST_PROCESSED_SMS)}")
            Log.d(TAG, "Filtered Sms: $filteredSms")
            this@TransactionalSmsUseCase.filteredSms = filteredSms
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