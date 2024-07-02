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
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.SpendAnalyserUseCase
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.TransactionSmsClassifier
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.TransactionalSmsUseCase
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
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
    ) : SmsUseCase<TransactionalSms>{
        return TransactionalSmsUseCase(
            context = context,
            transactionSmsClassifier = transactionSmsClassifier,
            regexProvider = regexProvider,
            saveTransactionUseCase = saveTransactionsUseCase,
            scope = CoroutineScope(Dispatchers.IO)
        )
    }

    @Provides
    fun providesSpendAnalyserUseCase(
        @ApplicationContext context: Context,
        transactionalSmsClassifier: SmsUseCase<TransactionalSms>
    ) : SpendAnalyserUseCase {
        return SpendAnalyserUseCase(context, transactionalSmsClassifier)
    }
}