package dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.nomadicprogrammer.spendly.smsparser.common.Util.smsReadPermissionAvailable
import dev.nomadicprogrammer.spendly.smsparser.common.base.SmsUseCase
import dev.nomadicprogrammer.spendly.smsparser.common.data.SmsInbox
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
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
    private val transactionalSmsClassifier: SmsUseCase<TransactionalSms>,
) {
    private val TAG = SpendAnalyserUseCase::class.simpleName
    private val _progress : MutableSharedFlow<Float> = MutableSharedFlow()
    val progress : SharedFlow<Float> = _progress

    suspend operator fun invoke() {
        if (!smsReadPermissionAvailable(context)) {
            return
        }

        val filteredSms: MutableList<TransactionalSms> = mutableListOf()

        SmsInbox(context)
            .readSms(transactionalSmsClassifier)
            .onStart { transactionalSmsClassifier.getRegex() }
            .flowOn(Dispatchers.IO)
            .onEach { trackProgress(it) }
            .map { it.third }
            .filter { sms -> isNonPersonalSms(sms) }
            .mapNotNull { sms -> transactionalSmsClassifier.filterMap(sms) }
            .onCompletion { error -> onComplete(error, filteredSms) }
            .flowOn(Dispatchers.Default)
            .toList(filteredSms)
    }

    private suspend fun trackProgress(it: Triple<Int, Int, Sms>) {
        val progress = (it.first.toFloat() / it.second.toFloat()) * 100
        _progress.emit(progress)
        transactionalSmsClassifier.onProgress(progress.toInt())
    }

    private fun onComplete(
        error: Throwable?,
        filteredSms: MutableList<TransactionalSms>
    ) {
        if (error == null) {
            transactionalSmsClassifier.onComplete(filteredSms)
        } else {
            transactionalSmsClassifier.onError(error)
        }
    }

    private fun isNonPersonalSms(sms: Sms): Boolean {
        val isNonPersonalSms = transactionalSmsClassifier.getRegex().isPositiveSender(sms.senderId) &&
                    !transactionalSmsClassifier.getRegex().isNegativeSender(sms.senderId)

        return isNonPersonalSms
    }
}