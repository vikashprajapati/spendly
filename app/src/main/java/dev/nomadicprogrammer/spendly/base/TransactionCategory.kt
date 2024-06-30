package dev.nomadicprogrammer.spendly.base

import android.util.Log
import androidx.compose.ui.graphics.Color
import dev.nomadicprogrammer.spendly.R
import dev.nomadicprogrammer.spendly.ui.theme.brighten
import dev.nomadicprogrammer.spendly.ui.theme.randomPastelColor

sealed interface TransactionCategory {
    val value: String
    val color: Color
    val iconTint: Color
    val iconId: Int

    companion object{
        val allCategories : List<TransactionCategory> = Expenses.entries + CashInflowCategory.entries

        fun fromValue(value: String): TransactionCategory {
            Log.d("TransactionCategory", "Value: $value")
            return Expenses.entries.firstOrNull { it.value == value }
                ?: CashInflowCategory.entries.firstOrNull { it.value == value }
                ?: Other
        }
    }

    data object Other: TransactionCategory{
        override val value: String
            get() = "Other"
        override val color: Color
            get() = randomPastelColor()
        override val iconTint: Color
            get() = color.brighten()
        override val iconId: Int
            get() = R.drawable.baseline_attach_money_24

    }

    enum class Expenses(
        override val value: String,
        override val color : Color = randomPastelColor(),
        override val iconTint : Color = color.brighten(),
        override val iconId: Int = 0
    ) : TransactionCategory {
        FOOD(value = "Food", iconId = R.drawable.baseline_fastfood_24),
        GROCERY("Grocery", iconId = R.drawable.baseline_local_grocery_store_24),
        SHOPPING("Shopping", iconId = R.drawable.baseline_shopping_bag_24),
        ENTERTAINMENT("Entertainment", iconId = R.drawable.baseline_movie_24),
        TRAVEL("Travel", iconId = R.drawable.baseline_travel_explore_24),
        HEALTH("Health", iconId = R.drawable.baseline_health_and_safety_24),
        BILLS("Bills", iconId = R.drawable.baseline_description_24),
        TRANSFER("Transfer", iconId = R.drawable.baseline_send_to_mobile_24),
        RENT("Rent", iconId = R.drawable.baseline_home_work_24),
        FUEL("Fuel", iconId = R.drawable.baseline_water_drop_24),
        ELECTRICITY("Electricity", iconId = R.drawable.baseline_electric_meter_24),
        WATER("Water", iconId = R.drawable.baseline_water_drop_24),
        PHONE("Phone", iconId = R.drawable.baseline_phone_android_24),
        INTERNET("Internet", iconId = R.drawable.baseline_wifi_24),
        CREDIT_CARD("Credit Card", iconId = R.drawable.baseline_credit_card_24),
        CAR("Car", iconId = R.drawable.baseline_directions_car_filled_24),
        BUS("Bus", iconId = R.drawable.baseline_directions_bus_filled_24),
        MOVIE("Movie", iconId = R.drawable.baseline_movie_24),
        RESTAURANT("Restaurant", iconId = R.drawable.baseline_restaurant_24);
    }

    enum class CashInflowCategory(
        override val value: String,
        override val color : Color = randomPastelColor(),
        override val iconTint : Color = color.brighten(),
        override val iconId: Int = 0
    ) : TransactionCategory{
        INCOME("Income", iconId = R.drawable.baseline_attach_money_24),
        SALARY("Salary", iconId = R.drawable.baseline_attach_money_24),
        REWARDS("Rewards", iconId = R.drawable.baseline_card_giftcard_24),
        REFUND("Refund", iconId = R.drawable.baseline_autorenew_24),
        GIFTS("Gifts", iconId = R.drawable.baseline_card_giftcard_24),
    }
}