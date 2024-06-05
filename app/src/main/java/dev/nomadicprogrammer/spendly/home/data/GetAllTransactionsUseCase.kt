package dev.nomadicprogrammer.spendly.home.data

import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Transaction
import kotlinx.coroutines.flow.Flow

class GetAllTransactionsUseCase(
    private val transactionEntityRepository: TransactionRepository
) {
    suspend operator fun invoke(): Flow<List<Transaction>?> {
        return transactionEntityRepository.getAllTransactions()
    }
}