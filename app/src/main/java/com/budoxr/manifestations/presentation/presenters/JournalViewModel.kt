package com.budoxr.manifestations.presentation.presenters

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budoxr.manifestations.commons.CategoryHelper
import com.budoxr.manifestations.commons.CommonValues.FLOW_WHILESUBSCRIBED
import com.budoxr.manifestations.commons.CommonValues.WAIT_DEFAULT
import com.budoxr.manifestations.commons.onDismissType
import com.budoxr.manifestations.commons.util.Utily
import com.budoxr.manifestations.data.database.entities.JournalEntity
import com.budoxr.manifestations.data.database.entities.LessonEntity
import com.budoxr.manifestations.data.database.entities.relations.LessonWithJournals
import com.budoxr.manifestations.data.database.entities.relations.ManifestationWithLessonsAndJournals
import com.budoxr.manifestations.data.mapper.emptyLessonModel
import com.budoxr.manifestations.data.repositories.LocalStorage
import com.budoxr.manifestations.presentation.domain.LessonsWrapper
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import com.budoxr.manifestations.presentation.domain.SessionModel
import com.budoxr.manifestations.presentation.usecase.JournalAnswerExistUseCase
import com.budoxr.manifestations.presentation.usecase.JournalDeleteUseCase
import com.budoxr.manifestations.presentation.usecase.JournalInfoUseCase
import com.budoxr.manifestations.presentation.usecase.JournalInsertUseCase
import com.budoxr.manifestations.presentation.usecase.LessonInfoUseCase
import com.budoxr.manifestations.presentation.usecase.LessonInsertUseCase
import com.budoxr.manifestations.presentation.usecase.ManifestationInfoUseCase
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
import kotlin.getValue

class JournalViewModel(private val context: Context) : ViewModel(), KoinComponent {
    private val localStorage: LocalStorage by inject()
    private val manifestationInfoUseCase : ManifestationInfoUseCase by inject()
    private val lessonInfoUseCase : LessonInfoUseCase by inject()
    private val lessonInsertUseCase : LessonInsertUseCase by inject()
    private val lessonDeleteUseCase : LessonInsertUseCase by inject()
    private val journalInfoUseCase : JournalInfoUseCase by inject()
    private val journalInsertUseCase : JournalInsertUseCase by inject()
    private val journalDeleteUseCase : JournalDeleteUseCase by inject()
    private val journalAnswerExistUseCase : JournalAnswerExistUseCase by inject()
    private val categoryHelper: CategoryHelper by inject()
    val util: Utily by inject()

    private val lessons = localStorage.getLessons(context).shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(FLOW_WHILESUBSCRIBED)
    )
    val manifestations = manifestationInfoUseCase.invoke().shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(FLOW_WHILESUBSCRIBED),
    )
    private val _manifestationWithlessons = MutableStateFlow<List<ManifestationWithLessonsAndJournals>>(emptyList())
    val manifestationWithLessons: StateFlow<List<ManifestationWithLessonsAndJournals>>
        get() = _manifestationWithlessons

    private val _journals = MutableStateFlow<List<LessonWithJournals>>(emptyList<LessonWithJournals>())
    val journals: StateFlow<List<LessonWithJournals>>
        get() = _journals

    private val refreshing = MutableStateFlow(false)

    private val _uiState = MutableStateFlow<JournalScreenUiState>(JournalScreenUiState.Loading)
    val uiState: StateFlow<JournalScreenUiState>
        get() = _uiState

    private val _sessionModel by inject<SessionModel>()
    val session: SessionModel
        get() = _sessionModel


    /** this value avoid to show the same error twice */
    var errorShowed: Boolean = false

    init {

        viewModelScope.launch {
            com.budoxr.manifestations.commons.util.combine(
                lessons,
                manifestations,
                refreshing
            ) { lessons,
                manifestations,
                refreshing ->

                if (refreshing) {
                    Log.d(TAG, "refreshing: $refreshing")
                    return@combine JournalScreenUiState.Loading
                }

                delay(50)
                JournalScreenUiState.Ready(
                    lessons = lessons,
                    manifestations = manifestations
                )

            }.catch { throwable ->
                throwable.printStackTrace()
                _uiState.value = JournalScreenUiState.Error(throwable.message)
                Log.e(TAG, "error: ${throwable.message}")
            }.collect {
                _uiState.value = it
            }
        }
        refresh(force = false)
    }

    fun error(errorMessage: String) {
        if (!errorShowed) {
            errorShowed = true
            _uiState.value = JournalScreenUiState.Error(errorMessage)
        }
    }

    fun loading( onDone: onDismissType ) {
        viewModelScope.launch {
            _uiState.value = JournalScreenUiState.Loading
            delay(WAIT_DEFAULT)
            launch {
                onDone.invoke()
                refresh(true)
            }
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

    fun collectManifestationWithLessons(manifestationId: Int): Unit {
        viewModelScope.launch(Dispatchers.IO) {
            lessonInfoUseCase.invoke(manifestationId)
                .collect {
                    _manifestationWithlessons.value = it
                }
        }
    }

    fun collectJournals(lessonId: Int): Unit {
        viewModelScope.launch(Dispatchers.IO) {
            journalInfoUseCase.invoke(lessonId)
                .collect {
                    _journals.value = it
                }
        }
    }

    fun saveLesson(lesson: LessonEntity) {
        Log.d(TAG, "saveLesson() -> called, lesson: ${lesson.day} of manifestation: ${lesson.manifestationId}")
        viewModelScope.launch(Dispatchers.IO) {
            lessonInsertUseCase.invoke(lesson)
        }

    }
    fun deleteLesson(lesson: LessonEntity) {
        Log.d(TAG, "deleteJournal() -> called")
        viewModelScope.launch {
            lessonDeleteUseCase.invoke(lesson)
        }

    }

    fun saveJournal(lesson: LessonEntity, journal: JournalEntity) {
        Log.d(TAG, "saveJournal() -> called, answer: ${journal.answer}")
        viewModelScope.launch(Dispatchers.IO) {
            journalInsertUseCase.invoke(journal)
        }

    }

    fun deleteJournal(journal: JournalEntity) {
        Log.d(TAG, "deleteJournal() -> called")
        viewModelScope.launch {
            journalDeleteUseCase.invoke(journal)
        }

    }


    fun categoryColor(categoryKey: String, context: Context): Color =
        categoryHelper.getCategoryColor(categoryKey, context)

    fun dateDifference(startDate: String, endDate: String): Long =
        util.dateDifference(startDate, endDate)

}


sealed interface JournalScreenUiState {
    data object Loading : JournalScreenUiState

    data class Error(
        val errorMessage: String? = null
    ) : JournalScreenUiState

    data class Ready(
        val lessons: LessonsWrapper = LessonsWrapper( listOf(emptyLessonModel()) ),
        val manifestations: List<ManifestationModel> = emptyList()
    ) : JournalScreenUiState
}

private const val TAG = "che.JournalViewModel"