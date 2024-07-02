package dev.nomadicprogrammer.spendly.smsparser.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.nomadicprogrammer.spendly.smsparser.common.data.SmsDataSource
import dev.nomadicprogrammer.spendly.smsparser.common.data.SmsInbox
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.LocalRegexProvider
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.RegexProvider
import dev.nomadicprogrammer.spendly.smsparser.parsers.AmountParser
import dev.nomadicprogrammer.spendly.smsparser.parsers.BankNameParser
import dev.nomadicprogrammer.spendly.smsparser.parsers.DateParser
import dev.nomadicprogrammer.spendly.smsparser.parsers.Parser
import dev.nomadicprogrammer.spendly.smsparser.parsers.ReceiverDetailsParser
import dev.nomadicprogrammer.spendly.smsparser.parsers.SenderDetailsParser
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms

@Module
@InstallIn(SingletonComponent::class)
abstract class ParserModule {
    @AmountParserQualifier
    @Binds
    abstract fun amountParser(amountParser: AmountParser) : Parser

    @BankNameParserQualifier
    @Binds
    abstract fun bankNameParser(amountParser: BankNameParser) : Parser

    @TransactionDateParserQualifier
    @Binds
    abstract fun dateParser(dateParser: DateParser) : Parser

    @ReceiverDetailsParserQualifier
    @Binds
    abstract fun receiverDetailsParser(receiverDetailsParser: ReceiverDetailsParser) : Parser

    @SenderDetailsParserQualifier
    @Binds
    abstract fun senderDetailsParser(senderDetailsParser: SenderDetailsParser) : Parser

    @Binds
    abstract fun smsInbox(smsInbox: SmsInbox): SmsDataSource<TransactionalSms>

    @Binds
    abstract fun localRegexProvider(localRegexProvider: LocalRegexProvider) : RegexProvider
}