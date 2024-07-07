package dev.nomadicprogrammer.spendly.transaction.view.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.nomadicprogrammer.spendly.base.DUMMY_TRANSACTIONS
import dev.nomadicprogrammer.spendly.base.DateUtils
import dev.nomadicprogrammer.spendly.base.TransactionStateHolder
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionType
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
import dev.nomadicprogrammer.spendly.ui.components.ScreenHeader
import dev.nomadicprogrammer.spendly.ui.components.ScreenHeaderDefault
import dev.nomadicprogrammer.spendly.ui.components.ScreenHeaderState
import dev.nomadicprogrammer.spendly.ui.components.StatusBarColor

@Composable
fun TransactionDetails(navController: NavController, transactionStateHolder: TransactionStateHolder, onBackPressed : () -> Unit){
    val viewModel = hiltViewModel<TransactionDetailsViewModel>()
    val state = viewModel.state.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.onEvent(TransactionDetailsPageLoad(transactionStateHolder.transactionalSms.smsId))
    }

    if (state.value.transactionDeleted){
        onBackPressed()
    }

    val toastMessage = viewModel.toastMessage.collectAsState(null)
    if (toastMessage.value != null){
        Toast.makeText(navController.context, toastMessage.value, Toast.LENGTH_SHORT).show()
        viewModel.onEvent(ClearToast)
    }

    TransactionDetailsContent(
        state,
        transactionStateHolder.transactionalSms,
        onBackClick = { onBackPressed() },
        onDeleteClick = {
            viewModel.onEvent(OnDeleteClicked(transactionStateHolder.transactionalSms))
        },
        onEditClick = {}
    )
}

@Composable
private fun TransactionDetailsContent(
    state: State<TransactionDetailsState>,
    transactionalSms: TransactionalSms,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val headerYOffset = 50.dp
    Scaffold(
        topBar = { TransactionDetailsHeader(transactionalSms, onBackClick, onDeleteClick, headerYOffset) }
    ) {
        val allSidePadding = 16.dp
        val pd = remember {
            PaddingValues(
                start = it.calculateStartPadding(LayoutDirection.Ltr) + allSidePadding,
                end = it.calculateEndPadding(LayoutDirection.Ltr) + allSidePadding,
                top = it.calculateTopPadding() + headerYOffset + allSidePadding + 16.dp,
                bottom = it.calculateBottomPadding() + allSidePadding + 16.dp
            )
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(paddingValues = pd),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            val originalSms = state.value.originalSms
            if (originalSms != null){
                OriginalSmsContent(originalSms)
            }

            Button(
                onClick = { onEditClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Edit",
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium),
                )
            }
        }
    }
}

@Composable
private fun OriginalSmsContent(originalSms: Sms) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "From: ${originalSms.senderId}",
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = DateUtils.Local.formattedDateFromTimestamp(
                    originalSms.date
                ), style = MaterialTheme.typography.titleSmall
            )
        }

        Spacer(modifier = Modifier.padding(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.secondaryContainer,
                    MaterialTheme.shapes.medium
                )
                .padding(16.dp)
        ) {
            Text(
                text = originalSms.msgBody,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun TransactionDetailsHeader(
    transactionalSms: TransactionalSms,
    onBackClick: () -> Unit,
    onDeleteClick: () -> Unit,
    offset: Dp
) {
    val headerSurfaceColor =
        if (transactionalSms.type == TransactionType.DEBIT) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val contentColor =
        if (transactionalSms.type == TransactionType.DEBIT) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary

    StatusBarColor(surfaceBgColor = headerSurfaceColor)

    Column(
        modifier = Modifier
            .background(
                headerSurfaceColor,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val screenHeaderState = ScreenHeaderState(
            title = {
                Text(
                    text = "Transaction Details",
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor
                )
            },
            leftHeaderButton = { ScreenHeaderDefault.BackHeaderButton(tint = contentColor, onClick = { onBackClick()}) },
            rightHeaderButton = {
                IconButton(onClick = { onDeleteClick() }) {
                    Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete", tint = contentColor)
                }
            }
        )

        ScreenHeader(
            modifier = Modifier.fillMaxWidth(),
            screenHeaderState = screenHeaderState
        )

        Spacer(modifier = Modifier.padding(24.dp))

        Text(
            text = transactionalSms.currencyAmount.toString(),
            style = MaterialTheme.typography.displaySmall,
            color = contentColor,
            fontWeight = FontWeight.ExtraBold,
        )

        Spacer(modifier = Modifier.padding(8.dp))

        Text(
            text = transactionalSms.transactionDate.toString(),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
        )
        ElevatedCard(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .offset(y = offset),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        MaterialTheme.shapes.medium
                    )
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val typeName =
                    if (transactionalSms.type == TransactionType.DEBIT) "Expense" else "Income"
                DetailItem("Type", typeName)

                DetailItem("Category", transactionalSms.category.name)

                DetailItem("Mode", "Cash")
            }
        }
    }
}

@Composable
private fun DetailItem(title : String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewTransactionDetails(){
    val transaction = DUMMY_TRANSACTIONS.last()
}