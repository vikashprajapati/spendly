package dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier

import android.util.Log
import dev.nomadicprogrammer.spendly.base.DateUtils
import dev.nomadicprogrammer.spendly.smsparser.common.exceptions.RegexFetchException
import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.LocalRegexProvider
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.RegexProvider
import dev.nomadicprogrammer.spendly.smsparser.di.AmountParserQualifier
import dev.nomadicprogrammer.spendly.smsparser.di.BankNameParserQualifier
import dev.nomadicprogrammer.spendly.smsparser.di.ReceiverDetailsParserQualifier
import dev.nomadicprogrammer.spendly.smsparser.di.SenderDetailsParserQualifier
import dev.nomadicprogrammer.spendly.smsparser.di.TransactionDateParserQualifier
import dev.nomadicprogrammer.spendly.smsparser.parsers.Parser
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionType
import javax.inject.Inject
import kotlin.random.Random

class TransactionSmsClassifier @Inject constructor(
    private val regexProvider: RegexProvider = LocalRegexProvider(),
    @AmountParserQualifier private val amountParser: Parser,
    @BankNameParserQualifier private val bankNameParser: Parser,
    @TransactionDateParserQualifier private val dateParser: Parser,
    @ReceiverDetailsParserQualifier private val receiverDetailsParser : Parser,
    @SenderDetailsParserQualifier private val senderDetailsParser : Parser,
){
    private val TAG = TransactionSmsClassifier::class.simpleName
    private val debitTransactionIdentifierRegex by lazy { regexProvider.getDebitTransactionIdentifierRegex() ?: throw RegexFetchException("Regex not found") }
    private val creditTransactionIdentifierRegex by lazy { regexProvider.getCreditTransactionIdentifierRegex() ?: throw RegexFetchException("Regex not found") }


    fun classify(sms: Sms): TransactionalSms? {
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

        return TransactionalSms.create(
            id = Random.nextInt().toString(),
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
}