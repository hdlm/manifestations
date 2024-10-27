package com.budoxr.manifestations.data.repositories

import android.content.Context
import android.util.Log
import com.budoxr.manifestations.presentation.domain.LessonsWrapper
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import com.budoxr.manifestations.data.mapper.emptyLessonModel

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

    override fun loadLessons(context: Context): String =
        context
            .assets
            .open(filenameLessons)
            .bufferedReader(Charset.forName("UTF-8"))
            .use(BufferedReader::readText)

    override fun getLessons(context: Context): Flow<LessonsWrapper> = flow {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter = moshi.adapter(LessonsWrapper::class.java)
        val lessonsJson = loadLessons(context)
        val listOfLesson = jsonAdapter.fromJson(lessonsJson)
        listOfLesson?.let {
            emit(it)
        } ?: run {
            val lessons = LessonsWrapper(
                listOf(emptyLessonModel())
            )
            emit( lessons )
        }
        Log.d(TAG, "getLessons() -> called")

    }

    companion object {
        private const val filenameLessons = "lessons.json"
        private const val TAG = "che.LocalStorageImpl"
    }


}