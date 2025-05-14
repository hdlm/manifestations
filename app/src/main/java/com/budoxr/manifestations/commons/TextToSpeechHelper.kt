package com.budoxr.manifestations.commons

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.budoxr.manifestations.data.mapper.emptyConfigModel
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
class TextToSpeechHelper(private val _context: Context) : KoinComponent, TextToSpeech.OnInitListener {
    private val configInfoUseCase: ConfigInfoUseCase by inject()
    private var _tts: TextToSpeech? = null
    private val _config = MutableStateFlow(emptyConfigModel())
    val config: MutableStateFlow<ConfigModel>
        get() = _config

    val context: Context
        get() = _context

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
            _tts?.let {
                if( it.isLanguageAvailable(Locale(CommonValues.LANGUAGE, CommonValues.COUNTRY)) == TextToSpeech.LANG_AVAILABLE) {
                    it.language = Locale(CommonValues.LANGUAGE, CommonValues.COUNTRY)
                    it.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            // Called when the utterance starts
                            Log.i(TAG, "Speech stared")
                        }

                        /**
                         * Called when the utterance is done
                         */
                        override fun onDone(utteranceId: String?) {
                            Log.i(TAG, "Speak finished.")

                            val scope: AppScope = get()
                            scope.launch {
                                delay(CommonValues.SPEAK_DELAY)
                                onDone.invoke()
                            }

                        }

                        override fun onError(utteranceId: String?) {
                            Log.e(TAG, "Speak error.")
                            onError("There was a problem with the Speech To Speech (TTS).")
                        }
                    })
                }
            }

        }
    }

    fun speak(text: String) {
        val phrases: CharSequence = text
        val utteranceId = UUID.randomUUID().toString()
        val params = HashMap<String, String>()
        params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "utteranceId"
//        tts?.speak(phrases, TextToSpeech.QUEUE_FLUSH, params, ) // deprecated
        _tts?.setSpeechRate(config.value.speechRate)
        _tts?.speak(phrases, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun shutdown() {
        _tts?.shutdown()
    }

    fun restart(context: Context) {
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
        private const val TAG = "TextToSpeechHelper"
    }
}
