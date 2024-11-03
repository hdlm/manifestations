package com.budoxr.manifestations.commons.util

import com.budoxr.manifestations.commons.toLocalDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

class Utily {

    /**
     * The method check if the input string is a pause.
     * An explicit break contains a line with a number in brackets.
     * Example of a 5 second pause: [5]
     */
    fun isPause(input: String): Boolean {
        val regex = "\\[\\d+]".toRegex()
        return regex.containsMatchIn(input)
    }

    /**
     * The method extracts the number in brackets from a string.
     */
    fun extractNumber(input: String): String? {
        val regex = "\\[(\\d+)]".toRegex()
        val matchResult = regex.find(input)
        return matchResult?.groups?.get(1)?.value
    }

    @Throws(Exception::class)
    suspend fun <T> performAsyncOperation(
        scope: CoroutineScope,
        operation: suspend () -> T
    ): Deferred<T> = scope.async {
        operation()
    }

    /**
     * The params must have the format: `yyyy-MM-dd kk:mm:ss`
     */
    fun dateDifference(startDateString: String, endDateString: String): Long {
        val startDate = startDateString.toLocalDate()
        val endDate = endDateString.toLocalDate()
        val daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate)
        return daysBetween
    }

}
