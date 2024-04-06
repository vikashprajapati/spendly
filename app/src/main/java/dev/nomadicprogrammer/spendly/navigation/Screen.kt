package dev.nomadicprogrammer.spendly.navigation

sealed class Screen(open val route : String) {
    data object Home : Screen("home")
    data object TransactionDetail: Screen("transaction_detail")
    data object SeeAllTransaction: Screen("see_all_transaction")
}