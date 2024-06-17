package dev.nomadicprogrammer.spendly.navigation

sealed class Screen(open val route : String) {
    data object Home : Screen("home")
    data object SeeAllTransaction: Screen("see_all_transaction")

    data object NewTransaction: Screen("new_transaction")
}