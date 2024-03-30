package dev.nomadicprogrammer.spendly.smsparser

import android.content.Context
import dev.nomadicprogrammer.spendly.smsparser.Util.smsReadPermissionAvailable
import dev.nomadicprogrammer.spendly.smsparser.data.SmsInbox
import dev.nomadicprogrammer.spendly.smsparser.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.usecases.LocalRegexProvider
import dev.nomadicprogrammer.spendly.smsparser.usecases.SpendAnalyser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
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

        val spendAnalyser = SpendAnalyser(LocalRegexProvider())

        val filteredSms : MutableList<Triple<Int, Int, Sms>> = mutableListOf()

        withContext(Dispatchers.Default){
            SmsInbox(context)
                .readSms(spendAnalyser.readSmsRange(), spendAnalyser.inboxReadSortOrder())
                .onStart { spendAnalyser.regex }
                .onEach {
                    val progress = (it.first.toFloat() / it.second.toFloat()) * 100
                    spendAnalyser.onProgress(progress.toInt())
                }
                .filter {
                    val sms = it.third
                    spendAnalyser.regex.isPositiveSender(sms.senderId) &&
                            !spendAnalyser.regex.isNegativeSender(sms.senderId) &&
                            spendAnalyser.regex.isPositiveMsgBody(sms.msgBody) &&
                            !spendAnalyser.regex.isNegativeMsgBody(sms.msgBody)
                }
                .onCompletion { error ->
                    if (error == null) {
                        spendAnalyser.onComplete(filteredSms.map { it.third })
                    } else {
                        spendAnalyser.onError(error)
                    }
                }
                .flowOn(Dispatchers.IO)
                .toList(filteredSms)
        }
    }
}