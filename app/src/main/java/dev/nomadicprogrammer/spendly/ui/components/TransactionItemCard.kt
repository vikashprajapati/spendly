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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.nomadicprogrammer.spendly.home.data.TransactionSmsUiModel
import dev.nomadicprogrammer.spendly.home.data.TransactionType
import dev.nomadicprogrammer.spendly.ui.theme.randomPastelColor
import kotlin.random.Random

@Composable
fun TransactionItemCard(
    transaction: TransactionSmsUiModel,
    onTransactionClick : (TransactionSmsUiModel) -> Unit = {}
){
    Log.d("TransactionItem", "Transaction: $transaction")
    val iconList = listOf(
        Icons.Outlined.ShoppingCart,
        Icons.Outlined.Place,
        Icons.Outlined.List,
        Icons.Outlined.AccountBox,
        Icons.Outlined.Call
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onTransactionClick(transaction) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = iconList[Random.nextInt(iconList.size)],
            modifier = Modifier
                .size(60.dp)
                .background(randomPastelColor(), MaterialTheme.shapes.large)
                .padding(20.dp),
            contentDescription = "Shopping",
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = transaction.bankName ?: transaction.originalSms.senderId,
                    style = MaterialTheme.typography.bodyMedium
                )
                val transactionSymbol = if (transaction.type == TransactionType.DEBIT) "-" else "+"
                Text(
                    text = "$transactionSymbol ${transaction.currencyAmount}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (transaction.type == TransactionType.DEBIT) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
//                val transactionParticipant = when(transaction){
//                    is TransactionalSms.Debit -> transaction.transferredTo
//                    is TransactionalSms.Credit -> transaction.receivedFrom
//
//                }
                Text(
                    text = transaction.originalSms.senderId,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                    text = transaction.transactionDate ?: transaction.originalSms.date.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
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
