package dev.nomadicprogrammer.spendly.smsparser.common.data

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.Telephony
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.nomadicprogrammer.spendly.smsparser.common.base.SmsUseCase
import dev.nomadicprogrammer.spendly.smsparser.common.model.Range
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.SmsReadPeriod
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class SmsInbox @Inject constructor(@ApplicationContext val context: Context) : SmsDataSource<TransactionalSms> {
    private val TAG = SmsInbox::class.simpleName

    private fun getRangeFilter(range: Range): String {
        return "${Telephony.Sms.Inbox.DATE} > ${range.start} AND ${Telephony.Sms.Inbox.DATE} <= ${range.end}"
    }

    @SuppressLint("Range")
    override fun <T> readSms(transactionalSmsClassifier: SmsUseCase<T>): Flow<Triple<Int, Int, Sms>> = flow{
        val range = transactionalSmsClassifier.readSmsRange(SmsReadPeriod.Yearly)
        Log.d(TAG, "Reading sms range: $range")
        val sortOrder = transactionalSmsClassifier.inboxReadSortOrder()
        val inboxUri = Uri.parse("content://sms/inbox")
        val projection = arrayOf(Telephony.Sms.Inbox.ADDRESS, Telephony.Sms.Inbox.BODY, Telephony.Sms.Inbox.DATE, Telephony.Sms.Inbox._ID)
        val cursor = context.contentResolver.query(inboxUri, projection, getRangeFilter(range), null, sortOrder)
        Log.d(TAG, "cursor count: ${cursor?.count}")

        var currentSms = 0

        while (cursor != null && cursor.moveToNext()){
            Log.d(TAG, "Reading sms: $currentSms")
            val _id = cursor.getString(cursor.getColumnIndex(Telephony.Sms.Inbox._ID))
            val address = cursor.getString(cursor.getColumnIndex(Telephony.Sms.Inbox.ADDRESS))
            val smsBody = cursor.getString(cursor.getColumnIndex(Telephony.Sms.Inbox.BODY))
            val date = cursor.getString(cursor.getColumnIndex(Telephony.Sms.Inbox.DATE))
            val sms = Sms(id = _id, senderId = address, msgBody = smsBody, date = date.toLong())
            currentSms++
            emit(Triple(currentSms, cursor.count, sms))
        }

        cursor?.close()
    }

    override fun getSmsById(id: Int): Sms? {
        val projection = arrayOf(Telephony.Sms.Inbox.ADDRESS, Telephony.Sms.Inbox.BODY, Telephony.Sms.Inbox.DATE, Telephony.Sms.Inbox._ID)

        val cursor = context.contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            projection,
            "${Telephony.Sms._ID} = ?",
            arrayOf(id.toString()),
            null
        )

        cursor?.use {
            if (it.moveToNext()) {
                val smsInboxId = it.getString(it.getColumnIndex(Telephony.Sms._ID))
                val senderId = it.getString(it.getColumnIndex(Telephony.Sms.ADDRESS))
                val body = it.getString(it.getColumnIndex(Telephony.Sms.BODY))
                val date = it.getLong(it.getColumnIndex(Telephony.Sms.DATE))
                return Sms(smsInboxId, senderId, body, date)
            }
        }

        return null
    }

    override fun getSmsByIds(ids: List<Int>): List<Sms?> {
        return ids.map { getSmsById(it) }
    }
}