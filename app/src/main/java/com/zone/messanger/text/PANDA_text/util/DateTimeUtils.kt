package com.zone.messanger.text.PANDA_text.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** Formats message / conversation timestamps the way the reference UI shows them. */
object DateTimeUtils {

    private fun calendarFor(millis: Long): Calendar =
        Calendar.getInstance().apply { timeInMillis = millis }

    private fun isSameDay(a: Calendar, b: Calendar): Boolean =
        a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
            a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)

    /** "2:59 PM" */
    fun formatTime(millis: Long): String =
        SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(millis))

    /**
     * Conversation-list timestamp: time for today, weekday name within the
     * last week, otherwise a short date — matching common SMS app behaviour.
     */
    fun formatListTimestamp(millis: Long): String {
        val now = Calendar.getInstance()
        val then = calendarFor(millis)
        return when {
            isSameDay(now, then) -> formatTime(millis)
            now.timeInMillis - millis < 6L * 24 * 60 * 60 * 1000 ->
                SimpleDateFormat("EEE", Locale.getDefault()).format(Date(millis))
            else -> SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date(millis))
        }
    }

    /** "Today" / "Yesterday" / "25 Sep 2026" — used for the date separator chips. */
    fun formatDateHeader(millis: Long): String {
        val now = Calendar.getInstance()
        val then = calendarFor(millis)
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        return when {
            isSameDay(now, then) -> "Today"
            isSameDay(yesterday, then) -> "Yesterday"
            else -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(millis))
        }
    }

    fun isSameDayMillis(a: Long, b: Long): Boolean = isSameDay(calendarFor(a), calendarFor(b))
}
