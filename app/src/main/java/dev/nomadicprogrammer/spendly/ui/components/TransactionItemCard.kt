package dev.nomadicprogrammer.spendly.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.nomadicprogrammer.spendly.base.TransactionCategoryResourceProvider
import dev.nomadicprogrammer.spendly.base.TransactionStateHolder
import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.common.usecases.TransactionCategory
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Credit
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Debit
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionType
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionItemCard(
    transactionStateHolder: TransactionStateHolder,
    onTransactionClick : (TransactionStateHolder) -> Unit = {}
){
    val transactionalSms = transactionStateHolder.transactionalSms
    val transactionCategoryResource = transactionStateHolder.transactionCategoryResource
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable { onTransactionClick(transactionStateHolder) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = transactionCategoryResource.icon),
            modifier = Modifier
                .size(48.dp)
                .background(transactionCategoryResource.color, MaterialTheme.shapes.medium)
                .padding(12.dp),
            contentDescription = "Shopping",
            tint = transactionCategoryResource.iconTint
        )
        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val secondParty = when(transactionalSms){
                    is Debit -> transactionalSms.transferredTo
                    is Credit -> transactionalSms.receivedFrom
                }

                Column {
                    Text(
                        text = transactionStateHolder.transactionalSms.category.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = secondParty ?: transactionalSms.bankName ?: transactionalSms.originalSms?.senderId?: "Unknown",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val transactionSymbol = if (transactionalSms.type == TransactionType.DEBIT) "-" else "+"
                Text(
                    text = "$transactionSymbol ${transactionalSms.currencyAmount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (transactionalSms.type == TransactionType.DEBIT) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    showSystemUi = false
)
@Composable fun TransactionItemCardPreview() {
    val provider = TransactionCategoryResourceProvider(LocalContext.current)
    val transactionStateHolder = TransactionStateHolder(
        TransactionalSms.create(
            id = "",
            type = TransactionType.CREDIT,
            currencyAmount = CurrencyAmount(amount = 40.9),
            category = TransactionCategory.CashInflow("Salary"),
            bank = "Icici",
            transactionDate = "2021-09-01"
        ),
        transactionCategoryResource = provider.getResource(TransactionCategory.CashInflow("Salary"))
    )
    TransactionItemCard(
        transactionStateHolder = transactionStateHolder
    )
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    showSystemUi = false
)
@Composable fun CreditTransactionItemCardPreview() {
    val provider = TransactionCategoryResourceProvider(LocalContext.current)
    val transactionStateHolder = TransactionStateHolder(
        TransactionalSms.create(
            id = "",
            type = TransactionType.DEBIT,
            currencyAmount = CurrencyAmount(amount = 40.9),
            category = TransactionCategory.CashOutflow("Grocery"),
            bank = "Icici",
            transactionDate = "2021-09-01"
        ),
        transactionCategoryResource = provider.getResource(TransactionCategory.CashOutflow("Grocery"))
    )
    TransactionItemCard(
        transactionStateHolder = transactionStateHolder
    )
}
