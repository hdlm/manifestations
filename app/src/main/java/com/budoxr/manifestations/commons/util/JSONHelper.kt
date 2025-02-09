package com.budoxr.manifestations.commons.util

import com.budoxr.Exercises.ui.JournalState
import com.budoxr.manifestations.presentation.domain.LessonsWrapper
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object JSONHelper {
    fun getManifestationFromJSON(json: String): List<ManifestationModel>? =
        try {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val type = Types.newParameterizedType(List::class.java, ManifestationModel::class.java)
            val adapter: JsonAdapter<List<ManifestationModel>> = moshi.adapter(type)
            adapter.fromJson(json)

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    fun getLessonWrapperFromJSON(json: String) : LessonsWrapper? = try {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val adapter = moshi.adapter(LessonsWrapper::class.java)
        adapter.fromJson(json)

    } catch (e: Exception) {
        e.printStackTrace() // Log the error or handle it appropriately
        null // Return null to indicate failure
    }

    /**
     * The purpose of this method is to capture the generated JSON
     * to assign it to the variables in the _Preview_
     */
    fun journalStateToJSON(journalState: JournalState): String? =
        try {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val listManifestation = Types.newParameterizedType(List::class.java, ManifestationModel::class.java)
            val adapterManifestation: JsonAdapter<List<ManifestationModel>> = moshi.adapter(listManifestation)
            val manifestations = adapterManifestation.toJson(journalState.manifestations)

            val adapterLesson = moshi.adapter(LessonsWrapper::class.java)
            val lessons =  adapterLesson.toJson(journalState.lessons)
            manifestations

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }


}