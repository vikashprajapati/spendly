package dev.nomadicprogrammer.spendly.home.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.nomadicprogrammer.spendly.database.AppDatabase
import dev.nomadicprogrammer.spendly.home.data.LocalTransactionRepository
import dev.nomadicprogrammer.spendly.home.data.GetAllTransactionsUseCase
import dev.nomadicprogrammer.spendly.home.data.mappers.TransactionMapper
import dev.nomadicprogrammer.spendly.smsparser.common.data.SmsInbox
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.SpendAnalyserUseCase

class HomeViewModelFactory(
    private val spendAnalyserUseCase: SpendAnalyserUseCase,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                spendAnalyserUseCase,
                GetAllTransactionsUseCase(
                    LocalTransactionRepository(
                    AppDatabase.getInstance(context.applicationContext).transactionDao(),
                    TransactionMapper(SmsInbox(context.applicationContext)),
                    SmsInbox(context.applicationContext)
                )
                )
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}