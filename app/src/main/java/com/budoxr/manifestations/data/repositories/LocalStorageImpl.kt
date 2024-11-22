package com.budoxr.manifestations.data.repositories

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.util.Log
import com.budoxr.manifestations.data.mapper.emptyLessonModel
import com.budoxr.manifestations.presentation.domain.LessonsWrapper
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import com.budoxr.manifestations.presentation.domain.ManifestationsWrapper
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class LocalStorageImpl : LocalStorage{

    @Throws(IOException::class)
    override fun loadFileStreamFromAssets(
        context: Context,
        filename: String
    ): File = File(context.cacheDir, filename)
        .also {
            if (!it.exists()) {
                it.outputStream().use { cache ->
                    context
                        .assets
                        .open(filename)
                        .use { inputStream ->
                            inputStream.copyTo(cache)
                        }
                }
            }
        }

    @Throws(IOException::class)
    override fun loadFileReaderFromAssets(
        context: Context,
        filename: String
    ): String {
        val reader: FileReader

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            reader = FileReader(File(context.cacheDir, filename)
                .also {
                    if (!it.exists()) {
                        it.outputStream().use { cache ->
                            context
                                .assets
                                .open(filename)
                                .use { inputStream ->
                                    inputStream.copyTo(cache)
                                }
                        }
                    }
                },
                Charset.forName("UTF-8")
            )

        } else {
            reader = FileReader( File(context.cacheDir, filename)
                .also {
                    if (!it.exists()) {
                        it.outputStream().use { cache ->
                            context
                                .assets
                                .open(filename)
                                .use { inputStream ->
                                    inputStream.copyTo(cache)
                                }
                        }
                    }
                }
            )
        }
        return BufferedReader( reader ).readText()

    }

    /**
     * The function write a `String` to the specified file.
     * @param content
     * @param context
     * @param filename
     */
    @Throws(IOException::class)
    private fun writeContentToFile(content: String, context: Context, filename: String): Unit = File(context.filesDir, filename)
        .outputStream()
        .bufferedWriter(Charset.forName("UTF-8"))
        .write(content)


    @Throws(IOException::class)
    private fun writeContentToFile(content: String, context: Context, uri: Uri ): Unit {
        context
            .contentResolver
            .openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream, StandardCharsets.UTF_8).use { writer ->
                    writer.write(content)
                }
            }
        Log.d(TAG, "writeFileContent() -> file: ${uri.path}, saved.")

    }

    /**
     * The function read from a text file
     * @param context
     * @param filename
     * @return a `String` with the content of the file
     */
    @Throws(FileNotFoundException::class)
    private fun readerFromFile(context: Context, filename: String): String = File(context.cacheDir, filename)
        .also {
            if (!it.exists()) {
                it.outputStream().bufferedWriter(Charset.forName("UTF-8")).use { cache ->
                    context.openFileInput(filename)
                        .bufferedReader(StandardCharsets.UTF_8).use { inputStream ->
                            inputStream.copyTo(cache, DEFAULT_BUFFER_SIZE)
                        }
                }
            }
        }
        .inputStream()
        .bufferedReader(Charset.forName("UTF-8"))
        .use(BufferedReader::readText)





    @Throws(IOException::class)
    override suspend fun backupDatabase(
        context: Context,
        manifestations: List<ManifestationModel>
    ) {
        Log.d(TAG, "backing up the database, count of registers: ${manifestations.size}")

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter = moshi.adapter<List<ManifestationModel>>(Types.newParameterizedType(List::class.java, ManifestationModel::class.java))
        val jsonString = jsonAdapter.toJson(manifestations)

        writeContentToFile(jsonString, context, BACKUP_FILE)
    }

    @Throws(FileNotFoundException::class)
    override suspend fun restoreDatabase(context: Context): List<ManifestationModel> {
        Log.d(TAG, "restoring the database")

        val jsonString = readerFromFile(context, BACKUP_FILE)
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter = moshi.adapter(ManifestationsWrapper::class.java)
        val manifestations: List<ManifestationModel> = jsonAdapter.fromJson(jsonString)!!.manifestations
        return manifestations
    }


    @Throws(IOException::class)
    override suspend fun exportDatabase(context: Context, manifestations: List<ManifestationModel>, selectedFolderUri: Uri) : Unit {
        Log.d(TAG, "backing up the database, count of registers: ${manifestations.size}")

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter = moshi.adapter<List<ManifestationModel>>(Types.newParameterizedType(List::class.java, ManifestationModel::class.java))
        val jsonString = jsonAdapter.toJson(manifestations)

//        val filename = getFilenameFromUri(selectedFolderUri, context)
//        writeContentToFile(jsonString, context, filename!!)
        writeContentToFile(jsonString, context, selectedFolderUri)
    }


    @Throws(FileNotFoundException::class)
    override suspend fun importDatabase(
        context: Context,
        selectedFolderUri: Uri
    ): List<ManifestationModel> {
        Log.d(TAG, "restoring the database")

        val filename = getFilenameFromUri(selectedFolderUri, context)
        val jsonString = readerFromFile(context, filename!!)
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter = moshi.adapter(ManifestationsWrapper::class.java)
        val manifestations: List<ManifestationModel> = jsonAdapter.fromJson(jsonString)!!.manifestations
        return manifestations

    }


    @Throws(IOException::class)
    override fun getLessons(context: Context): Flow<LessonsWrapper> = flow {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val jsonAdapter = moshi.adapter(LessonsWrapper::class.java)
        val lessonsJson = loadFileReaderFromAssets(context, filenameLessons)
        val listOfLesson = jsonAdapter.fromJson(lessonsJson)
        listOfLesson?.let {
            emit(it)
        } ?: run {
            val lessons = LessonsWrapper(
                listOf(emptyLessonModel())
            )
            emit( lessons )
        }

    }


    /**
     * get the file name from the URI
     * @param fileUri
     * @param context
     * @return the file name
     */
    private fun getFilenameFromUri(fileUri: Uri, context: Context): String? {
        //
        val contentResolver = context.contentResolver
        var fileName: String? = null
        val cursor: Cursor? = contentResolver.query(fileUri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }


    companion object {
        private const val filenameLessons = "lessons.json"
        const val filenameManifestations = "manifestations.json"
        private val BACKUP_FILE = "backup.json"
        private const val TAG = "che.LocalStorageImpl"
    }


}