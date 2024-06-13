package dev.nomadicprogrammer.spendly.smsparser.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.nomadicprogrammer.spendly.home.data.SaveTransactionsUseCase
import dev.nomadicprogrammer.spendly.smsparser.common.base.SmsUseCase
import dev.nomadicprogrammer.spendly.smsparser.common.data.SmsDataSource
import dev.nomadicprogrammer.spendly.smsparser.common.data.SmsInbox
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.LocalRegexProvider
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.RegexProvider
import dev.nomadicprogrammer.spendly.smsparser.parsers.AmountParser
import dev.nomadicprogrammer.spendly.smsparser.parsers.BankNameParser
import dev.nomadicprogrammer.spendly.smsparser.parsers.DateParser
import dev.nomadicprogrammer.spendly.smsparser.parsers.Parser
import dev.nomadicprogrammer.spendly.smsparser.parsers.ReceiverDetailsParser
import dev.nomadicprogrammer.spendly.smsparser.parsers.SenderDetailsParser
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.SpendAnalyserUseCase
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.TransactionalSmsClassifier
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
 object SmsParserModule {

    @Singleton
    @Provides
    fun provides(
        @ApplicationContext context: Context,
        @AmountParserQualifier amountParser: Parser,
        @BankNameParserQualifier bankNameParser: Parser,
        @TransactionDateParserQualifier dateParser: Parser,
        @ReceiverDetailsParserQualifier receiverDetailsParser: Parser,
        @SenderDetailsParserQualifier senderDetailsParser: Parser,
        regexProvider: RegexProvider,
        saveTransactionsUseCase: SaveTransactionsUseCase
    ) : SmsUseCase<Transaction>{
        return TransactionalSmsClassifier(
            context = context,
            amountParser = amountParser,
            bankNameParser = bankNameParser,
            dateParser = dateParser,
            receiverDetailsParser = receiverDetailsParser,
            senderDetailsParser = senderDetailsParser,
            regexProvider = regexProvider,
            saveTransactionUseCase = saveTransactionsUseCase,
            scope = CoroutineScope(Dispatchers.IO)
        )
    }

    @Singleton
    @Provides
    fun providesSpendAnalyserUseCase(
        @ApplicationContext context: Context,
        transactionClassifier: SmsUseCase<Transaction>
    ) : SpendAnalyserUseCase {
        return SpendAnalyserUseCase(context, transactionClassifier)
    }
}