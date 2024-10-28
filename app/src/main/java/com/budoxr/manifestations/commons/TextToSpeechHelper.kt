package com.budoxr.manifestations.commons

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

class TextToSpeechHelper(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null
    var onDone : onDismissType = {}

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("es", "MX") // Set language to Mexican Spanish
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    // Called when the utterance starts
                    Log.i(TAG, "Speech stared")
                }

                override fun onDone(utteranceId: String?) {
                    // Called when the utterance is done
                    Log.i(TAG, "Speech finished")
                    onDone.invoke()
                }

                override fun onError(utteranceId: String?) {
                    // Called when an error occurs
                }
            })
        }
    }

    fun speak(text: String) {
        val params = HashMap<String, String>()
        params[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "utteranceId"
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params)
//        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun shutdown() {
        tts?.shutdown()
    }

    companion object {
        private const val TAG = "TextToSpeechHelper"
    }
}
