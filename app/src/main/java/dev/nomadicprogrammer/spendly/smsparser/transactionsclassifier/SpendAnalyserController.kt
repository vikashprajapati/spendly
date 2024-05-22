package dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier

import android.content.Context
import android.util.Log
import dev.nomadicprogrammer.spendly.MainApp
import dev.nomadicprogrammer.spendly.database.TransactionEntity
import dev.nomadicprogrammer.spendly.home.data.LocalTransactionRepository
import dev.nomadicprogrammer.spendly.home.data.StoreTransactionUseCase
import dev.nomadicprogrammer.spendly.home.data.TransactionRepository
import dev.nomadicprogrammer.spendly.smsparser.common.Util.smsReadPermissionAvailable
import dev.nomadicprogrammer.spendly.smsparser.common.base.SmsUseCase
import dev.nomadicprogrammer.spendly.smsparser.common.data.SmsInbox
import dev.nomadicprogrammer.spendly.smsparser.parsers.AmountParser
import dev.nomadicprogrammer.spendly.smsparser.parsers.BankNameParser
import dev.nomadicprogrammer.spendly.smsparser.parsers.DateParser
import dev.nomadicprogrammer.spendly.smsparser.parsers.ReceiverDetailsParser
import dev.nomadicprogrammer.spendly.smsparser.parsers.SenderDetailsParser
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext

class SpendAnalyserController(
    private val context: Context,
    private val transactionalSmsClassifier: SmsUseCase<TransactionalSms> = MainApp.transactionalSmsClassifier,
    private val readPeriod: SmsReadPeriod = SmsReadPeriod.Yearly
){
    private val TAG = SpendAnalyserController::class.simpleName

    suspend fun launchTransactionalSmsClassifier() {
        if (!smsReadPermissionAvailable(context)) {
            return
        }

        val filteredSms : MutableList<TransactionalSms> = mutableListOf()

        withContext(Dispatchers.Default){
            SmsInbox(context)
                .readSms(transactionalSmsClassifier.readSmsRange(readPeriod), transactionalSmsClassifier.inboxReadSortOrder())
                .onStart { transactionalSmsClassifier.getRegex() }
                .onEach {
                    val progress = (it.first.toFloat() / it.second.toFloat()) * 100
                    transactionalSmsClassifier.onProgress(progress.toInt())
                }
                .map { it.third }
                .filter {sms ->
                    Log.d(TAG, "Filtering sms: $sms")
                    val isNonPersonalSms = transactionalSmsClassifier.getRegex().isPositiveSender(sms.senderId) &&
                            !transactionalSmsClassifier.getRegex().isNegativeSender(sms.senderId)

                    isNonPersonalSms
                }
                .mapNotNull {sms ->
                    Log.d(TAG, "Mapping sms to Transactional sms: $sms")
                    transactionalSmsClassifier.filterMap(sms) as TransactionalSms?
                }
                .onCompletion { error ->
                    if (error == null) {
                        transactionalSmsClassifier.onComplete(filteredSms)
                    } else {
                        transactionalSmsClassifier.onError(error)
                    }
                }
                .flowOn(Dispatchers.IO)
                .toList(filteredSms)
        }
    }

    fun generateReport(): List<TransactionalSms> {
        Log.d(TAG, "Generating report")
        return transactionalSmsClassifier.getFilteredResult()
    }
}