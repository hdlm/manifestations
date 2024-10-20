package com.budoxr.manifestations.data.repositories

import android.content.Context
import java.io.File

interface LocalStorage {

    fun loadFileFromAssets(context: Context, filename: String): File
}