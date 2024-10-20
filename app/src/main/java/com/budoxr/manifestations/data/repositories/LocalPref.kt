package com.budoxr.manifestations.data.repositories

import android.content.SharedPreferences
import android.util.Log
import com.budoxr.manifestations.commons.util.moshi.SessionModelAdapter
import com.budoxr.manifestations.presentation.domain.SessionModel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import java.lang.reflect.Type

object LocalPref {

    lateinit var pref: SharedPreferences

    /*
    fun saveSession(session: SessionModel) {
        Log.d(TAG, "saveSession() -> called")
        val moshi = Moshi.Builder().add(SessionModelAdapter()).build()
        val jsonAdapter = moshi.adapter(SessionModel::class.java)
        val sessionJson = jsonAdapter.toJson(session)
        val editor: SharedPreferences.Editor = pref.edit()
        editor.putString(PREF_SESSION, sessionJson)
        editor.apply()
    }
     */

    fun saveSession(session: SessionModel) {
        Log.d(TAG, "saveSession() -> called")
        val moshi = Moshi.Builder().add(SessionModelAdapter()).build()
        val jsonAdapter: JsonAdapter<SessionModel> = moshi.adapter(SessionModel::class.java)
        val sessionJson = jsonAdapter.toJson(session)
        val editor: SharedPreferences.Editor = pref.edit()
        editor.putString(PREF_SESSION, sessionJson)
        editor.apply()
    }

    /*
    fun getSession(): SessionModel? {
        Log.d(TAG, "getSession() -> called")
        val moshi = Moshi.Builder().add(SessionModelAdapter()).build()
        val jsonAdapter = moshi.adapter(SessionModel::class.java)
        val sessionJson = pref.getString(PREF_SESSION, "")
        sessionJson?.let {
            return jsonAdapter.fromJson(sessionJson)
        } ?: run {
            return null
        }
    }
     */


    fun getSession(): SessionModel? {
        Log.d(TAG, "getSession() -> called")
        val moshi = Moshi.Builder().add(SessionModelAdapter()).build()
        val jsonAdapter: JsonAdapter<SessionModel> = moshi.adapter(SessionModel::class.java)
        val sessionJson = pref.getString(PREF_SESSION, "")
        return if (!sessionJson.isNullOrEmpty()) {
            jsonAdapter.fromJson(sessionJson)
        } else {
            null
        }

    }

    fun resetData() {
        val editor: SharedPreferences.Editor = pref.edit()
        editor.clear()
        editor.apply()
    }

    const val LOCAL_PREF = "manifestationspref"
    const val PREF_SESSION = "session"
    private const val TAG = "che.LocalPref"
}