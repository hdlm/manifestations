package com.budoxr.manifestations.commons

object CommonValues {

    const val FLOW_WHILESUBSCRIBED = 5000L   // 5 seconds
    const val WAIT_DEFAULT = 500L
    const val SPEAK_DELAY = 750L
    const val oneDayMillis = 86_400_000L
    enum class STATUS_PLAYER {
        stop,
        pause,
        playing,
        rewind,
        forward,
        previous,
    }
    enum class TYPE_ITEM {
        manifestation,
        excercise,
    }

}
