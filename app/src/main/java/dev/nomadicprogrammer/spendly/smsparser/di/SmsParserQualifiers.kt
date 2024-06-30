package dev.nomadicprogrammer.spendly.smsparser.di

import javax.inject.Qualifier

@Qualifier
annotation class AmountParserQualifier

@Qualifier
annotation class BankNameParserQualifier

@Qualifier
annotation class TransactionDateParserQualifier

@Qualifier
annotation class ReceiverDetailsParserQualifier

@Qualifier
annotation class SenderDetailsParserQualifier