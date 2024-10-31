package com.budoxr.manifestations.presentation.presenters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budoxr.manifestations.commons.CommonValues.FLOW_WHILESUBSCRIBED
import com.budoxr.manifestations.commons.CommonValues.WAIT_DEFAULT
import com.budoxr.manifestations.commons.TextToSpeechHelper
import com.budoxr.manifestations.commons.onDismissType
import com.budoxr.manifestations.commons.util.Utily
import com.budoxr.manifestations.data.mapper.emptyLessonModel
import com.budoxr.manifestations.data.repositories.LocalStorage
import com.budoxr.manifestations.presentation.domain.LessonsWrapper
import com.budoxr.manifestations.presentation.domain.SessionModel
import com.budoxr.manifestations.presentation.domain.TextContent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.Reader

class LessonViewModel(private val context: Context) : ViewModel(), KoinComponent {

    private val localStorage : LocalStorage by inject()
    private val utily: Utily by inject()

    private val lessons = localStorage.getLessons(context).shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(FLOW_WHILESUBSCRIBED)
    )
    private val refreshing = MutableStateFlow(false)

    private val _uiState = MutableStateFlow<LessonScreenUiState>(LessonScreenUiState.Loading)
    val uiState: StateFlow<LessonScreenUiState>
        get() = _uiState

    private val _sessionModel: SessionModel by inject()
    val session: SessionModel
        get() = _sessionModel

    private var _meditationContent: TextContent = TextContent(paragraphs = listOf(), text = "", paragraphCount = 0)
    val meditationContent: TextContent
        get() = _meditationContent

    private var _textToSpeech  = TextToSpeechHelper(context)
    val textToSpeech: TextToSpeechHelper
        get() = _textToSpeech

    /** this value avoid to show the same error twice */
    var errorShowed: Boolean = false

    init {
        viewModelScope.launch {

            com.budoxr.manifestations.commons.util.combine(
                lessons,
                refreshing,
            ) { lessons,
                refreshing ->

                if (refreshing) {
                    Log.d(TAG, "refreshing: $refreshing")
                    return@combine LessonScreenUiState.Loading
                }

                delay(1000)
                LessonScreenUiState.Ready(
                    lessons = lessons
                )
            }.catch { throwable ->
                throwable.printStackTrace()
                _uiState.value = LessonScreenUiState.Error(throwable.message)
                Log.e(TAG, "error: ${throwable.message}")
            }.collect {
                _uiState.value = it
            }
        }
        refresh(force = false)
    }

    fun loading( onDone: onDismissType ) {
        viewModelScope.launch {
            _uiState.value = LessonScreenUiState.Loading
            delay(WAIT_DEFAULT)
            launch {
                onDone.invoke()
                refresh(true)
            }
        }
    }


    fun error(errorMessage: String) {
        if (!errorShowed) {
            errorShowed = true
            _uiState.value = LessonScreenUiState.Error(errorMessage)
        }
    }

    fun refresh(force: Boolean = true ) {
        viewModelScope.launch {
            runCatching {
                refreshing.value = true
                delay(20)
                refreshing.value = false
            }
        }
    }


    fun loadMeditation(fileName: String) {
        val file = localStorage.loadFileFromAssets(context, fileName)
        val reader = file.reader()
        val paragraphs = getParagraphs(reader)
        Log.d(TAG, "loadMeditation() -> filename: $fileName, loaded.")
        _meditationContent = TextContent(paragraphs = paragraphs, text = "", paragraphCount = 0)
    }

    private fun getParagraphs(reader: Reader): List<String> {
        val paragraphs = mutableListOf<String>()
        val currentParagraph = StringBuilder()
        reader.forEachLine { line ->
            if (line.isBlank()) {
                if (currentParagraph.isNotEmpty()) {
                    paragraphs.add(currentParagraph.toString().trim())
                    currentParagraph.clear()
                }
            } else {
                currentParagraph.append(line).append("\n")
            }
        }
        if (currentParagraph.isNotEmpty()) {
            paragraphs.add(currentParagraph.toString().trim())
        }
        return paragraphs
    }

    fun speak(paragraphIndex: Int) {
        viewModelScope.launch(Dispatchers.IO) {

            val paragraph =_meditationContent.paragraphs[paragraphIndex]
            if (utily.isPause(paragraph)) {
                // make a pause of 'n' seconds
                val pause = utily.extractNumber(paragraph)
                pause?.let {
                    Log.i(TAG, "make a pause of '$it seconds'")
                    delay(it.toLong() * 1000L)
                    val nextParagraph = _meditationContent.paragraphCount++
                    textToSpeech.speak(meditationContent.paragraphs[nextParagraph])
                }
            } else {
                textToSpeech.speak(meditationContent.paragraphs[paragraphIndex])
            }
        }

    }

    fun stopSpeak() {
        Log.i(TAG, "speak stopped.")
        _meditationContent.paragraphCount = 0
    }

    fun restartSpeak() {
        _textToSpeech.restart(context)
    }

}


sealed interface LessonScreenUiState {
    data object Loading : LessonScreenUiState

    data class Error(
        val errorMessage: String? = null
    ) : LessonScreenUiState

    data class Ready(
        val lessons: LessonsWrapper = LessonsWrapper( listOf(emptyLessonModel()) )
    ) : LessonScreenUiState

}


private const val  TAG = "che.LessonViewModel"
