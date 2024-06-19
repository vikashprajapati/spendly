package dev.nomadicprogrammer.spendly.home.data

import dev.nomadicprogrammer.spendly.smsparser.common.data.SmsInbox
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import javax.inject.Inject

class OriginalSmsFetchUseCase @Inject constructor(
    private val smsInbox: SmsInbox
) {
    operator fun invoke(smsId : String): Sms? {
        return smsInbox.getSmsById(smsId.toInt())
    }

    operator fun invoke(smsId: List<String>): List<Sms?> {
        return smsInbox.getSmsByIds(smsId.map { it.toInt() })
    }
}