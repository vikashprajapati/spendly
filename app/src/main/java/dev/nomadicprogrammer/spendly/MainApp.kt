package dev.nomadicprogrammer.spendly

import android.app.Application
import android.content.Context
import dev.nomadicprogrammer.spendly.database.AppDatabase
import dev.nomadicprogrammer.spendly.home.data.LocalTransactionRepository
import dev.nomadicprogrammer.spendly.home.data.TransactionUseCase
import dev.nomadicprogrammer.spendly.home.data.mappers.TransactionMapper
import dev.nomadicprogrammer.spendly.smsparser.common.data.SmsInbox
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.LocalRegexProvider
import dev.nomadicprogrammer.spendly.smsparser.parsers.AmountParser
import dev.nomadicprogrammer.spendly.smsparser.parsers.BankNameParser
import dev.nomadicprogrammer.spendly.smsparser.parsers.DateParser
import dev.nomadicprogrammer.spendly.smsparser.parsers.ReceiverDetailsParser
import dev.nomadicprogrammer.spendly.smsparser.parsers.SenderDetailsParser
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.TransactionalSmsClassifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MainApp : Application() {
    companion object{
        lateinit var transactionalSmsClassifier: TransactionalSmsClassifier
    }

    val context : Context = this

    override fun onCreate() {
        super.onCreate()

        val regexProvider = LocalRegexProvider()
        val amountParser = AmountParser()
        val bankNameParser = BankNameParser()
        val dateParser = DateParser()
        val receiverDetailsParser = ReceiverDetailsParser()
        val senderDetailsParser = SenderDetailsParser()
        val smsInbox = SmsInbox(context)
        val transactionUseCase = TransactionUseCase(
            transactionEntityRepository = LocalTransactionRepository(
                AppDatabase.getInstance(context).transactionDao(),
                TransactionMapper(smsInbox),
                smsInbox
            )
        )
        val scope = CoroutineScope(Dispatchers.IO)

        transactionalSmsClassifier = TransactionalSmsClassifier(
            this,
            regexProvider,
            amountParser,
            bankNameParser,
            dateParser,
            receiverDetailsParser,
            senderDetailsParser,
            transactionUseCase,
            scope
        )
    }
}