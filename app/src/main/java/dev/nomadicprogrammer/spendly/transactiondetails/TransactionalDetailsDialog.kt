package dev.nomadicprogrammer.spendly.transactiondetails

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.nomadicprogrammer.spendly.base.DateUtils
import dev.nomadicprogrammer.spendly.home.presentation.HomeEvent
import dev.nomadicprogrammer.spendly.home.presentation.HomeViewModel
import dev.nomadicprogrammer.spendly.home.presentation.HomeViewModelFactory
import dev.nomadicprogrammer.spendly.navigation.Screen
import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.common.model.Sms
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.SpendAnalyserController
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionalSms
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetails(
    transactionalSms: TransactionalSms,
    sheetState : SheetState,
    onDismiss : () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        sheetState = sheetState,
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transaction Details",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = transactionalSms.type,
                    color = if (transactionalSms is TransactionalSms.Debit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .background(
                        color = if (transactionalSms is TransactionalSms.Debit) MaterialTheme.colorScheme.error.copy(alpha = 0.1f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    ).padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.padding(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "From: ${transactionalSms.originalSms.senderId}", style = MaterialTheme.typography.titleSmall)
                Text(text = DateUtils.Local.formattedDateFromTimestamp(transactionalSms.originalSms.date), style = MaterialTheme.typography.titleSmall)
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
                    text = transactionalSms.originalSms.msgBody,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}
