package dev.nomadicprogrammer.spendly.smsparser

import android.content.Context
import android.util.Log
import dev.nomadicprogrammer.spendly.smsparser.Util.smsReadPermissionAvailable
import dev.nomadicprogrammer.spendly.smsparser.data.SmsInbox
import dev.nomadicprogrammer.spendly.smsparser.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.model.TransactionalSms
import dev.nomadicprogrammer.spendly.smsparser.parsers.AmountParser
import dev.nomadicprogrammer.spendly.smsparser.usecases.LocalRegexProvider
import dev.nomadicprogrammer.spendly.smsparser.usecases.TransactionalSmsClassifier
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
    val context: Context
){
    private val TAG = SpendAnalyserController::class.simpleName

    suspend fun launch() {
        if (!smsReadPermissionAvailable(context)) {
            return
        }

        val transactionalSmsClassifier = TransactionalSmsClassifier(LocalRegexProvider(), AmountParser())

        val filteredSms : MutableList<TransactionalSms> = mutableListOf()

        withContext(Dispatchers.Default){
            SmsInbox(context)
                .readSms(transactionalSmsClassifier.readSmsRange(), transactionalSmsClassifier.inboxReadSortOrder())
                .onStart { transactionalSmsClassifier.personalSmsExclusionRegex }
                .onEach {
                    val progress = (it.first.toFloat() / it.second.toFloat()) * 100
                    transactionalSmsClassifier.onProgress(progress.toInt())
                }
                .map { it.third }
                .filter {sms ->
                    Log.d(TAG, "Filtering sms: $sms")
                    val isNonPersonalSms = transactionalSmsClassifier.personalSmsExclusionRegex.isPositiveSender(sms.senderId) &&
                            !transactionalSmsClassifier.personalSmsExclusionRegex.isNegativeSender(sms.senderId)

                    isNonPersonalSms
                }
                .mapNotNull {sms -> transactionalSmsClassifier.filterMap(sms) as TransactionalSms? }
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
}