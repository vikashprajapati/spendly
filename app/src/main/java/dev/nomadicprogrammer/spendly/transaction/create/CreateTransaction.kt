package dev.nomadicprogrammer.spendly.transaction.create

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dev.nomadicprogrammer.spendly.base.DateUtils
import dev.nomadicprogrammer.spendly.base.TransactionCategory
import dev.nomadicprogrammer.spendly.smsparser.common.model.CurrencyAmount
import dev.nomadicprogrammer.spendly.smsparser.common.model.DEFAULT_UNDEFINED_SMS
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.Transaction
import dev.nomadicprogrammer.spendly.smsparser.transactionsclassifier.model.TransactionType
import dev.nomadicprogrammer.spendly.ui.theme.brighten
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTransaction(
    navController: NavController,
    transactionType: TransactionType,
    viewModel : CreateTransactionViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val toastMessage = viewModel.toastMessage.collectAsState(initial = null, coroutineScope.coroutineContext)
    if (toastMessage.value != null){
        Toast.makeText(context, toastMessage.value, Toast.LENGTH_SHORT).show()
        viewModel.onEvent(CreateTransactionEvents.ClearToastMessage)
    }

    CreateTransactionScreen(
        transactionType = transactionType,
        state = state,
        navController = navController,
        onEvent = { viewModel.onEvent(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTransactionScreen(
    transactionType: TransactionType,
    state: State<CreateTransactionState>,
    navController: NavController,
    onEvent: (CreateTransactionEvents) -> Unit,
) {
    var amount by rememberSaveable { mutableStateOf(state.value.transactionAmount) }

    Scaffold(
        topBar = {
            ScreenHeader(navController, transactionType, amount) {
                amount = it
            }
        }
    ) { paddingValues ->
        val screenPaddingTop = paddingValues.calculateTopPadding()
        val screenPaddingBottom = paddingValues.calculateBottomPadding()
        val screenPaddingStart = paddingValues.calculateStartPadding(LayoutDirection.Rtl)
        val screenPaddingEnd = paddingValues.calculateEndPadding(LayoutDirection.Rtl)

        Column(
            modifier = Modifier
                .padding(
                    top = screenPaddingTop,
                    bottom = screenPaddingBottom,
                    start = screenPaddingStart,
                    end = screenPaddingEnd
                )
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                var secondParty by rememberSaveable { mutableStateOf("") }
                val secondPartyLabel =
                    if (transactionType == TransactionType.DEBIT) "Transferred To" else "Received From"
                OutlinedTextField(
                    value = secondParty,
                    onValueChange = { secondParty = it },
                    modifier = Modifier
                        .fillMaxWidth(),
                    label = { Text(secondPartyLabel) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                )

                Spacer(modifier = Modifier.height(16.dp))

                val selectedCategory = remember { mutableStateOf<TransactionCategory?>(null) }
                var categoryMenuExpanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            shape = MaterialTheme.shapes.medium
                        )
                ){
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedCategory.value?.value ?: "Select Category",
                            modifier = Modifier
                                .padding(16.dp)
                                .weight(1f)
                        )
                        IconButton(onClick = {
                            categoryMenuExpanded = true
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.KeyboardArrowDown,
                                contentDescription = "Expand Category Menu"
                            )
                        }
                    }
                }

                DropdownMenu(
                    expanded = categoryMenuExpanded,
                    onDismissRequest = { categoryMenuExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    TransactionCategory.entries.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(text = category.value) },
                            onClick = {
                                selectedCategory.value = category
                                categoryMenuExpanded = false
                            })
                    }
                }








                Spacer(modifier = Modifier.height(16.dp))

                var transactionMedium by remember { mutableStateOf<String>("Cash") }
                OutlinedTextField(
                    value = transactionMedium,
                    onValueChange = {
                        transactionMedium = it
                    },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icons.Outlined.KeyboardArrowDown },
                    shape = MaterialTheme.shapes.medium,
                )


                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = System.currentTimeMillis(),
                    initialDisplayMode = DisplayMode.Input
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Enter Transaction Date",
                    style = MaterialTheme.typography.bodyMedium
                )
                DatePicker(
                    modifier = Modifier
                        .padding(0.dp),
                    state = datePickerState,
                    showModeToggle = false,
                    title = null,
                    headline = null
                )

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val transaction = Transaction.create(
                            id = Random.nextInt().toString(),
                            type = transactionType,
                            sms = DEFAULT_UNDEFINED_SMS,
                            currencyAmount = CurrencyAmount(amount = amount.toDouble()),
                            bank = null,
                            transactionDate = DateUtils.Local.formattedDateFromTimestamp(
                                datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                            ),
                            transferredTo = if (transactionType == TransactionType.DEBIT) secondParty else null,
                            receivedFrom = if (transactionType == TransactionType.CREDIT) secondParty else null,
                            category = selectedCategory.value ?: TransactionCategory.OTHER
                        )
                        onEvent(CreateTransactionEvents.OnCreateTransactionClicked(transaction))
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "Create Transaction",
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Composable
private fun ScreenHeader(
    navController: NavController,
    transactionType: TransactionType,
    amount: String,
    onAmountChange : (String) -> Unit
) {
    val surfaceBgColor =  if (transactionType == TransactionType.DEBIT) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primary
    val contentColor = Color.White

    val coroutineScope = rememberCoroutineScope()
    val context =LocalContext.current
    LaunchedEffect(key1 = true){
        coroutineScope.launch {
            (context as Activity).window.statusBarColor = surfaceBgColor.toArgb()
        }
    }

    Surface(color = surfaceBgColor, contentColor = contentColor) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                IconButton(
                    modifier = Modifier.align(Alignment.CenterStart),
                    onClick = {
                        navController.popBackStack()
                    }) {
                    Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = if (transactionType == TransactionType.DEBIT) "Expense" else "Income",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleMedium,
                    color = contentColor
                )
            }

            Spacer(modifier = Modifier.height(84.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ){
                Text(
                    text = "How much?",
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    color = contentColor.brighten(0.9f),
                    modifier = Modifier.padding(start = 16.dp)
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { onAmountChange(it) },
                    textStyle = MaterialTheme.typography.displayMedium,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    prefix = { Text(
                        "₹", style = MaterialTheme.typography.displayMedium,
                        color = contentColor
                    ) }, // TODO: use local currency saved
                    shape = MaterialTheme.shapes.medium,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedLabelColor = contentColor,
                        unfocusedTextColor = contentColor,
                        focusedTextColor = contentColor,
                    ),
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(
                        MaterialTheme.shapes.large.copy(
                            bottomEnd = CornerSize(0.dp),
                            bottomStart = CornerSize(0.dp)
                        )
                    )
                    .background(MaterialTheme.colorScheme.surface)
            ) {

            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, showSystemUi = true)
@Composable
fun previewCreateTransaction() {
    val state = remember { mutableStateOf(CreateTransactionState()) }
    CreateTransactionScreen(transactionType = TransactionType.DEBIT, state = state, navController = rememberNavController() ) {

    }
}