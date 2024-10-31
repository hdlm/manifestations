package com.budoxr.manifestations.commons

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class AppScope : CoroutineScope {
    private var parentJob = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + parentJob

    fun onStart() {
        parentJob = Job()
    }

    fun onStop() {
        parentJob.cancel()
    }
}





