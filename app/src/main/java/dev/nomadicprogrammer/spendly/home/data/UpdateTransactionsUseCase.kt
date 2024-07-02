package dev.nomadicprogrammer.spendly.home.data

import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
import javax.inject.Inject

class UpdateTransactionsUseCase @Inject constructor(
    private val transactionEntityRepository: TransactionRepository
) {
    suspend operator fun invoke(transactionalSms : TransactionalSms):Int {
        return transactionEntityRepository.updateTransaction(transactionalSms)
    }
}