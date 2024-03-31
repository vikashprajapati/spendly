package dev.nomadicprogrammer.spendly.base

import android.util.Log
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale


object DateUtils {
    private val potentialInputPatterns = listOf(
        "ddMMMyy",
        "ddMMMyyyy",
        "dd/MM/yyyy",
        "MM/dd/yyyy",
        "dd MMMM yyyy",
        "yyyy-MM-dd",
        "yyyy/MM/dd",
        "yyyy.MM.dd",
        "yyyy MM dd",
        "dd-MM-yyyy",
        "dd/MMM/yyyy",
        "dd/MM/yyyy",
        "MM/dd/yyyy",
        "dd MMMM yyyy",
        "yyyy-MM-dd",
        "yyyy/MM/dd",
        "yyyy.MM.dd",
        "yyyy MM dd",
        "dd-MM-yyyy"
    )

    object Local{
        private val TAG = Local::class.java.simpleName
        const val DATE_FORMAT = "dd/MMM/yyyy"
        private val outputFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT)

        fun getFormattedDate(dateString: String): String? {
            for (pattern in potentialInputPatterns) {
                try {
                    val date = convertDate(dateString, pattern)
                    if (date != null) {
                        return date
                    }
                } catch (e: DateTimeParseException) {
                    Log.e(TAG, "Error parsing date: $dateString with pattern: $pattern")
                }
            }
            return null
        }

        fun convertDate(dateString: String, inputPattern : String): String? {
            val inputFormatter = DateTimeFormatter.ofPattern(inputPattern)
            val date = LocalDate.parse(dateString, inputFormatter)
            return date.format(outputFormatter)
        }
    }

}