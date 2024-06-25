package dev.nomadicprogrammer.spendly.ui.components

import android.util.Log
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.IconCompat
import dev.nomadicprogrammer.spendly.base.TransactionCategory
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Credit
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Debit
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Transaction
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionType
import dev.nomadicprogrammer.spendly.ui.theme.randomPastelColor
import java.util.Locale
import kotlin.random.Random

@Composable
fun TransactionItemCard(
    transaction: Transaction,
    onTransactionClick : (Transaction) -> Unit = {}
){
    Log.d("TransactionItem", "Transaction: $transaction")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onTransactionClick(transaction) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Log.d("TransactionItem", "Transaction category: ${transaction.category}")
        Icon(
            painter = painterResource(id = transaction.category.iconId),
            modifier = Modifier
                .size(48.dp)
                .background(transaction.category.color, MaterialTheme.shapes.medium)
                .padding(12.dp),
            contentDescription = "Shopping",
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val secondParty = when(transaction){
                    is Debit -> transaction.transferredTo
                    is Credit -> transaction.receivedFrom
                }

                Column {
                    Text(
                        text = transaction.category.value,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = secondParty ?: transaction.bankName ?: transaction.originalSms?.senderId?: "Unknown",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val transactionSymbol = if (transaction.type == TransactionType.DEBIT) "-" else "+"
                Text(
                    text = "$transactionSymbol ${transaction.currencyAmount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (transaction.type == TransactionType.DEBIT) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/*
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    showSystemUi = false
)
@Composable fun TransactionItemCardPreview() {
    TransactionItemCard(
        transaction = TransactionalSms.Debit(
            originalSms = Sms(
                id = UUID.randomUUID().toString(),
                senderId = "Amazon",
                date = System.currentTimeMillis(),
                msgBody = "You've spent 1000 INR on Amazon"
            ),
            currencyAmount = CurrencyAmount(
                amount = 1000.0,
                currency = "INR"
            ),
            bankName = "ICICI Bank",
            transactionDate = "2021-09-01",
            transferredTo = ""
        )
    )

}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL,
    showSystemUi = false
)
@Composable fun CreditTransactionItemCardPreview() {
    TransactionItemCard(
        transaction = TransactionalSms.Credit(
            originalSms = Sms(
                id = UUID.randomUUID().toString(),
                senderId = "Amazon",
                date = System.currentTimeMillis(),
                msgBody = "You've spent 1000 INR on Amazon"
            ),
            currencyAmount = CurrencyAmount(
                amount = 1000.0,
                currency = "INR"
            ),
            bankName = "ICICI Bank",
            transactionDate = "2021-09-01",
            receivedFrom = ""
        )
    )

}*/
