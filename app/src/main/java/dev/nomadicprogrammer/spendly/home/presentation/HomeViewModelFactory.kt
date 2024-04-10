package dev.nomadicprogrammer.spendly.home.presentation

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.nomadicprogrammer.spendly.database.AppDatabase
import dev.nomadicprogrammer.spendly.home.data.LocalTransactionRepository
import dev.nomadicprogrammer.spendly.home.data.StoreTransactionUseCase
import dev.nomadicprogrammer.spendly.home.data.mappers.TransactionMapper
import dev.nomadicprogrammer.spendly.smsparser.common.data.SmsInbox
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.SpendAnalyserController

class HomeViewModelFactory(
    private val spendAnalyserController: SpendAnalyserController,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                spendAnalyserController,
                StoreTransactionUseCase(
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