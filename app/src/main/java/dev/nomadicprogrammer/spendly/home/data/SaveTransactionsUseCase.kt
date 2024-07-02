package dev.nomadicprogrammer.spendly.home.data

import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
import javax.inject.Inject

class SaveTransactionsUseCase @Inject constructor(
    private val transactionEntityRepository: TransactionRepository
) {
    suspend operator fun invoke(transactionalSms: TransactionalSms) : Long {
        return transactionEntityRepository.saveTransaction(transactionalSms)
    }

    suspend operator fun invoke(transactionalSms: List<TransactionalSms>) {
        return transactionEntityRepository.saveTransactions(transactionalSms)
    }
}