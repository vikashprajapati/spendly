package dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.nomadicprogrammer.spendly.smsparser.common.Util.smsReadPermissionAvailable
import dev.nomadicprogrammer.spendly.smsparser.common.base.SmsUseCase
import dev.nomadicprogrammer.spendly.smsparser.common.data.SmsInbox
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
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
import javax.inject.Inject

class SpendAnalyserUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val transactionClassifier: SmsUseCase<Transaction>,
) {
    private val TAG = SpendAnalyserUseCase::class.simpleName

    suspend operator fun invoke() {
        if (!smsReadPermissionAvailable(context)) {
            return
        }

        val filteredSms: MutableList<Transaction> = mutableListOf()

        SmsInbox(context)
            .readSms(transactionClassifier)
            .onStart { transactionClassifier.getRegex() }
            .flowOn(Dispatchers.IO)
            .onEach { trackProgress(it) }
            .map { it.third }
            .filter { sms -> isNonPersonalSms(sms) }
            .mapNotNull { sms -> transactionClassifier.filterMap(sms) }
            .onCompletion { error -> onComplete(error, filteredSms) }
            .flowOn(Dispatchers.Default)
            .toList(filteredSms)
    }

    private fun trackProgress(it: Triple<Int, Int, Sms>) {
        val progress = (it.first.toFloat() / it.second.toFloat()) * 100
        transactionClassifier.onProgress(progress.toInt())
    }

    private fun onComplete(
        error: Throwable?,
        filteredSms: MutableList<Transaction>
    ) {
        if (error == null) {
            transactionClassifier.onComplete(filteredSms)
        } else {
            transactionClassifier.onError(error)
        }
    }

    private fun isNonPersonalSms(sms: Sms): Boolean {
        val isNonPersonalSms = transactionClassifier.getRegex().isPositiveSender(sms.senderId) &&
                    !transactionClassifier.getRegex().isNegativeSender(sms.senderId)

        return isNonPersonalSms
    }
}