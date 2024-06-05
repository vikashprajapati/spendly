package dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier

import android.content.Context
import android.util.Log
import dev.nomadicprogrammer.spendly.MainApp
import dev.nomadicprogrammer.spendly.smsparser.common.Util.smsReadPermissionAvailable
import dev.nomadicprogrammer.spendly.smsparser.common.base.SmsUseCase
import dev.nomadicprogrammer.spendly.smsparser.common.data.SmsInbox
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.toList

class SpendAnalyserUseCase(
    private val context: Context,
    private val transactionClassifier: SmsUseCase<Transaction> = MainApp.transactionalSmsClassifier,
) {
    private val TAG = SpendAnalyserUseCase::class.simpleName

    suspend operator fun invoke() {
        if (!smsReadPermissionAvailable(context)) {
            return
        }

        val filteredSms: MutableList<Transaction> = mutableListOf()

        SmsInbox(context)
            .readSms(
                transactionClassifier.readSmsRange(SmsReadPeriod.Yearly),
                transactionClassifier.inboxReadSortOrder()
            )
            .flowOn(Dispatchers.IO)
            .onStart { transactionClassifier.getRegex() }
            .onEach {
                val progress = (it.first.toFloat() / it.second.toFloat()) * 100
                transactionClassifier.onProgress(progress.toInt())
            }
            .map { it.third }
            .filter { sms ->
                Log.d(TAG, "Filtering sms: $sms")
                val isNonPersonalSms =
                    transactionClassifier.getRegex().isPositiveSender(sms.senderId) &&
                            !transactionClassifier.getRegex().isNegativeSender(sms.senderId)

                isNonPersonalSms
            }
            .mapNotNull { sms ->
                Log.d(TAG, "Mapping sms to Transactional sms: $sms")
                transactionClassifier.filterMap(sms)
            }
            .onCompletion { error ->
                if (error == null) {
                    transactionClassifier.onComplete(filteredSms)
                } else {
                    transactionClassifier.onError(error)
                }
            }
            .flowOn(Dispatchers.Default)
            .toList(filteredSms)
    }
}