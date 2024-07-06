package dev.nomadicprogrammer.spendly.base

import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.TransactionCategory
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionType
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms

val DUMMY_TRANSACTIONS = listOf(
    TransactionalSms.create(
        id = "dfd",
        TransactionType.DEBIT,
        Sms("id", "", "", System.currentTimeMillis()),
        CurrencyAmount("INR", 100.0),
        "Amazon",
        "12-12-2021",
        "Amit",
        "Sbi",
        TransactionCategory.CashOutflow("Grocery")
    ),

    TransactionalSms.create(
        "idfd",
        TransactionType.CREDIT,
        Sms("id", "", "", System.currentTimeMillis()),
        CurrencyAmount("INR", 200.0),
        "Amazon",
        "12-12-2021",
        "Amit",
        "Sbi",
        TransactionCategory.Other
    )
)