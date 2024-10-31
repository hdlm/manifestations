package com.budoxr.manifestations.commons

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.util.Locale
import java.util.UUID

class TextToSpeechHelper(context: Context) : KoinComponent, TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    var onDone : onDismissType = {}
    var onError : onStringType = {}

    init {
        initializeTTS(context)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("es", "MX") // Set language to Mexican Spanish
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
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

    fun speak(text: String) {
        val phrases: CharSequence = text
        val utteranceId = UUID.randomUUID().toString()
        val params = HashMap<String, String>()
        params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "utteranceId"
//        tts?.speak(phrases, TextToSpeech.QUEUE_FLUSH, params, ) // deprecated
        tts?.speak(phrases, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun shutdown() {
        tts?.shutdown()
    }

    fun restart(context: Context) {
        shutdown()
        initializeTTS(context)
        Log.i(TAG, "TextToSpeech restarted")
    }

    private fun initializeTTS(context: Context) {
        tts = TextToSpeech(context, this)
    }


    companion object {
        private const val TAG = "TextToSpeechHelper"
    }
}
