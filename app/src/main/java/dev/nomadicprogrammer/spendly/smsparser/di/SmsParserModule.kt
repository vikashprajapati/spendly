package dev.nomadicprogrammer.spendly.smsparser.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.nomadicprogrammer.spendly.home.data.SaveTransactionsUseCase
import dev.nomadicprogrammer.spendly.smsparser.common.base.SmsUseCase
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.RegexProvider
import dev.nomadicprogrammer.spendly.smsparser.parsers.Parser
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.SpendAnalyserUseCase
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.TransactionSmsClassifier
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.TransactionalSmsUseCase
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
        transactionSmsClassifier: TransactionSmsClassifier,
        regexProvider: RegexProvider,
        saveTransactionsUseCase: SaveTransactionsUseCase
    ) : SmsUseCase<Transaction>{
        return TransactionalSmsUseCase(
            context = context,
            transactionSmsClassifier = transactionSmsClassifier,
            regexProvider = regexProvider,
            saveTransactionUseCase = saveTransactionsUseCase,
            scope = CoroutineScope(Dispatchers.IO)
        )
    }

    @Singleton
    @Provides
    fun providesTransactionSmsClassifier(
        @AmountParserQualifier amountParser: Parser,
        @BankNameParserQualifier bankNameParser: Parser,
        @TransactionDateParserQualifier dateParser: Parser,
        @ReceiverDetailsParserQualifier receiverDetailsParser: Parser,
        @SenderDetailsParserQualifier senderDetailsParser: Parser,
        regexProvider: RegexProvider
    ) : TransactionSmsClassifier{
        return TransactionSmsClassifier(
            regexProvider = regexProvider,
            amountParser = amountParser,
            bankNameParser = bankNameParser,
            dateParser = dateParser,
            receiverDetailsParser = receiverDetailsParser,
            senderDetailsParser = senderDetailsParser
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