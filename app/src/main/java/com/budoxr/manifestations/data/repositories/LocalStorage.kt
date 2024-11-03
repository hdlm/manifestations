package com.budoxr.manifestations.data.repositories

import android.content.Context
import com.budoxr.manifestations.presentation.domain.LessonsWrapper
import kotlinx.coroutines.flow.Flow
import java.io.File

interface LocalStorage {

    fun loadFileFromAssets(context: Context, filename: String): File
    fun loadLessons(context: Context): String
    fun getLessons(context: Context): Flow<LessonsWrapper>

}