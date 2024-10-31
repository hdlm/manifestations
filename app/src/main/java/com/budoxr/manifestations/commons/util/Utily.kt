package com.budoxr.manifestations.commons.util

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

}