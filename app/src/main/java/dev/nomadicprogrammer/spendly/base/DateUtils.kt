package dev.nomadicprogrammer.spendly.base

import android.util.Log
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


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
        "dd-MM-yyyy",
        "dd/MM/yy"
    )

    object Local{
        private val TAG = Local::class.java.simpleName
        const val APP_DATE_FORMAT = "dd/MMM/yyyy"
        const val APP_DATE_FORMAT_WITH_TIME = "dd/MMM/yyyy HH:mm:ss"
        private val appDateFormatter = DateTimeFormatter.ofPattern(APP_DATE_FORMAT)
        private val appDateFormatterWithTime = DateTimeFormatter.ofPattern(APP_DATE_FORMAT_WITH_TIME)

        fun formattedDateFromTimestamp(timestamp: Long): String {
            val date = Instant.ofEpochMilli(timestamp)
            val localDate = LocalDateTime.ofInstant(date, ZoneId.systemDefault())
            return appDateFormatter.format(localDate)
        }

        fun formattedDateWithTimeFromTimestamp(timestamp: Long): String {
            val date = Instant.ofEpochMilli(timestamp)
            val localDate = LocalDateTime.ofInstant(date, ZoneId.systemDefault())
            return appDateFormatterWithTime.format(localDate)
        }

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
            return date.format(appDateFormatter)
        }

        /**
         * Considers the date string to be in the format of [APP_DATE_FORMAT]
         */
        fun getLocalDate(dateString : String) : LocalDate{
            return LocalDate.parse(dateString, appDateFormatter)
        }

        fun getPreviousDate(day: Int): LocalDate? {
            val currentDate = LocalDate.now()
            return currentDate.minusDays(day.toLong())
        }
    }

}