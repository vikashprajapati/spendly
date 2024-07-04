package dev.nomadicprogrammer.spendly.navigation

import dev.nomadicprogrammer.spendly.navigation.NewTransaction.Args.TRANSACTION_TYPE
import dev.nomadicprogrammer.spendly.navigation.TransactionDetail.Args.TRANSACTION_ID

sealed class Screen(open val route : String) {
    fun withArgs(vararg args: Pair<String, String>): String {
        var updatedRoute = route
        args.forEach {
            updatedRoute = updatedRoute.replace("{${it.first}}", it.second)
        }
        return updatedRoute
    }
}

data object Home : Screen("home")

data object TransactionDetail : Screen("transaction_detail/{$TRANSACTION_ID}"){
    object Args {
        const val TRANSACTION_ID = "transactionId"
    }
}

data object SeeAllTransaction: Screen("see_all_transaction")

data object NewTransaction: Screen("new_transaction/{$TRANSACTION_TYPE}"){
    object Args {
        const val TRANSACTION_TYPE = "transactionType"
    }
}