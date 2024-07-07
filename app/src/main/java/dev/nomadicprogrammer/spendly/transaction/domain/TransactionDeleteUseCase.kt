package dev.nomadicprogrammer.spendly.transaction.domain

import dev.nomadicprogrammer.spendly.home.data.TransactionRepository
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
import javax.inject.Inject

class TransactionDeleteUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(transactionalSms: TransactionalSms) : Int{
        return transactionRepository.deleteTransaction(transactionalSms)
    }
}