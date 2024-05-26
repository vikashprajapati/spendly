package dev.nomadicprogrammer.spendly.home.data

import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
import kotlinx.coroutines.flow.Flow

class TransactionUseCase(
    private val transactionEntityRepository: TransactionRepository
) {
    suspend fun saveTransaction(transactionSmsUiModel : TransactionSmsUiModel) {
        transactionEntityRepository.saveTransaction(transactionSmsUiModel)
    }

    suspend fun saveTransactions(transactionSmsUiModels: List<TransactionSmsUiModel>) {
        transactionEntityRepository.saveTransactions(transactionSmsUiModels)
    }

    suspend fun getTransactions(): Flow<List<TransactionSmsUiModel>?> {
        return transactionEntityRepository.getTransactions()
    }
}