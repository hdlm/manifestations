package com.budoxr.manifestations.commons

object CommonValues {

    const val FLOW_WHILESUBSCRIBED = 5000L   // 5 seconds
    const val WAIT_DEFAULT = 500L
    const val WAIT_DEFERRED = 60L
    const val SPEAK_DELAY = 750L
    const val oneDayMillis = 86_400_000L
    const val MANIFESTATION_TAG = "manif"
    const val LANGUAGE = "es"
    const val COUNTRY = "MX"


    enum class STATUS_PLAYER {
        stop,
        pause,
        playing,
        rewind,
        forward,
        previous,
    }

    enum class TYPE_PERMISSION {
        read_external_storage,
        write_external_storage,
    }

    enum class TYPE_ITEM {
        manifestation,
        excercise,
    }

}
