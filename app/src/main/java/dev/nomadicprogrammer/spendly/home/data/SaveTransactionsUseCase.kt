package dev.nomadicprogrammer.spendly.home.data

import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Transaction
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveTransactionsUseCase @Inject constructor(
    private val transactionEntityRepository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        return transactionEntityRepository.saveTransaction(transaction)
    }

    suspend operator fun invoke(transactions: List<Transaction>) {
        return transactionEntityRepository.saveTransactions(transactions)
    }
}