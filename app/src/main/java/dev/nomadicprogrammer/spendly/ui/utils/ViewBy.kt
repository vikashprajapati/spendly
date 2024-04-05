package dev.nomadicprogrammer.spendly.ui.utils

import dev.nomadicprogrammer.spendly.R

enum class ViewBy(val days : Int, val resId: Int) {
    DAILY(1, R.string.daily),
    WEEKLY(7, R.string.weekly),
    MONTHLY(31, R.string.monthly),
    Quarter(90, R.string.quarter),
    MidYear(180, R.string.mid_year),
    Yearly(365, R.string.yearly)
}