package com.budoxr.manifestations.commons

import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.budoxr.manifestations.data.mapper.defaultConfigModel
import com.budoxr.manifestations.presentation.domain.ConfigModel
import com.budoxr.manifestations.presentation.usecase.ConfigInfoUseCase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import java.util.Locale
import java.util.UUID
import kotlin.getValue

@OptIn(DelicateCoroutinesApi::class)
class TextToSpeechHelper(val context: Context) : KoinComponent, TextToSpeech.OnInitListener {
    private val configInfoUseCase: ConfigInfoUseCase by inject()
    private var _tts: TextToSpeech? = null
    private val _config = MutableStateFlow(defaultConfigModel())
    val config: MutableStateFlow<ConfigModel>
        get() = _config

    var onDone : onDismissType = {}
    var onError : onStringType = {}

    init {
        GlobalScope.launch {
            _config.value = configInfoUseCase.invoke()
            initializeTTS(context, _config.value.speechRate)
        }

    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            _tts?.let { tts -> // Renamed `it` to `tts` for clarity
                val result = tts.isLanguageAvailable(Locale(CommonValues.LANGUAGE, CommonValues.COUNTRY))
                Log.d(TAG, "isLanguageAvailable for ${CommonValues.LANGUAGE}-${CommonValues.COUNTRY} returned: $result")

                when (result) {
                    TextToSpeech.LANG_AVAILABLE, TextToSpeech.LANG_COUNTRY_AVAILABLE -> { // Consider LANG_COUNTRY_AVAILABLE as well
                        tts.language = Locale(CommonValues.LANGUAGE, CommonValues.COUNTRY)
                        Log.i(TAG, "Language set to: ${tts.voice.locale}")
                        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                            // ... your onStart, onDone, onError implementations ...
                            override fun onStart(utteranceId: String?) {
                                Log.i(TAG, "Speech started. UtteranceId: $utteranceId")
                            }

                            override fun onDone(utteranceId: String?) {
                                Log.i(TAG, "Speak finished. UtteranceId: $utteranceId")
                                val scope: AppScope = get()
                                scope.launch {
                                    delay(CommonValues.SPEAK_DELAY)
                                    onDone.invoke()
                                }
                            }

                            override fun onError(utteranceId: String?) {
                                Log.e(TAG, "Speak error. UtteranceId: $utteranceId")
                                onError("There was a problem with the Speech To Speech (TTS).")
                            }
                        })
                        Log.d(TAG, "UtteranceProgressListener set successfully.")
                    }
                    TextToSpeech.LANG_MISSING_DATA -> {
                        Log.w(TAG, "Language data is missing for ${CommonValues.LANGUAGE}-${CommonValues.COUNTRY}. Prompting user to install.")
                        // Option 1: Prompt user to install language data
                        val installIntent = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
                        installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Needed if calling from non-Activity context
                        context.startActivity(installIntent)
                        onError("Required speech language data is missing. Please install it.")
                        // Option 2: Fallback to default language
                        // tts.language = Locale.getDefault() // Or some other known available language
                    }
                    TextToSpeech.LANG_NOT_SUPPORTED -> {
                        Log.e(TAG, "Language ${CommonValues.LANGUAGE}-${CommonValues.COUNTRY} is not supported by this TTS engine.")
                        onError("The chosen language is not supported by your device's speech engine.")
                        // Option: Fallback to default language
                        // tts.language = Locale.getDefault()
                    }
                    else -> {
                        Log.e(TAG, "Unknown language availability status: $result for ${CommonValues.LANGUAGE}-${CommonValues.COUNTRY}.")
                        onError("An unknown error occurred with language setup for speech.")
                    }
                }
            }
        } else {
            Log.e(TAG, "TextToSpeech initialization failed with status: $status")
            onError("Text-to-Speech initialization failed. Status: $status")
        }
    }

    fun speak(text: String) {
        val phrases: CharSequence = text
        val utteranceId: String = UUID.randomUUID().toString()
        _tts?.setSpeechRate(config.value.speechRate)
        _tts?.speak(phrases, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun shutdown() {
        _tts?.shutdown()
    }

    fun restart() {
        shutdown()
        initializeTTS(context, _config.value.speechRate)
        Log.i(TAG, "TextToSpeech restarted")
    }

    private fun initializeTTS(context: Context, speechRate: Float) {
        _tts = TextToSpeech(context, this)
        _tts?.setSpeechRate(speechRate)
        Log.d(TAG, "initializeTTS() -> speechRate: $speechRate")
    }


    companion object {
        private const val TAG = "che.TextToSpeechHelper"
    }
}
