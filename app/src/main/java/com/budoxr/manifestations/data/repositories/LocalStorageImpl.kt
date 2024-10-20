package com.budoxr.manifestations.data.repositories

import android.content.Context
import java.io.File
import java.io.IOException

class LocalStorageImpl : LocalStorage{

    @Throws(IOException::class)
    override fun loadFileFromAssets(
        context: Context,
        filename: String
    ): File = File(context.cacheDir, filename)
        .also {
            if (!it.exists()) {
                it.outputStream().use { cache ->
                    context.assets.open(filename).use { inputStream ->
                        inputStream.copyTo(cache)
                    }
                }
            }
        }


}