package com.budoxr.manifestations.commons

object CommonValues {

    const val FLOW_WHILESUBSCRIBED = 5000L   // 5 seconds
    const val WAIT_DEFAULT = 500L
    enum class STATUS_PLAYER {
        stop,
        pause,
        playing,
        rewind,
        forward,
        previous,
    }
}