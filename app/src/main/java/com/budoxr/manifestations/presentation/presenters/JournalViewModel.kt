package com.budoxr.manifestations.presentation.presenters

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budoxr.manifestations.commons.CategoryHelper
import com.budoxr.manifestations.commons.CommonValues.FLOW_WHILESUBSCRIBED
import com.budoxr.manifestations.commons.CommonValues.WAIT_DEFAULT
import com.budoxr.manifestations.commons.CommonValues.WAIT_DEFERRED
import com.budoxr.manifestations.commons.onDismissType
import com.budoxr.manifestations.commons.util.Utily
import com.budoxr.manifestations.data.database.entities.JournalEntity
import com.budoxr.manifestations.data.mapper.emptyLessonModel
import com.budoxr.manifestations.data.repositories.LocalStorage
import com.budoxr.manifestations.presentation.domain.LessonModel
import com.budoxr.manifestations.presentation.domain.LessonsWrapper
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import com.budoxr.manifestations.presentation.domain.SessionModel
import com.budoxr.manifestations.presentation.usecase.JournalAnswerExistUseCase
import com.budoxr.manifestations.presentation.usecase.JournalDeleteUseCase
import com.budoxr.manifestations.presentation.usecase.JournalInfoUseCase
import com.budoxr.manifestations.presentation.usecase.JournalInsertUseCase
import com.budoxr.manifestations.presentation.usecase.JournalLastIdUseCase
import com.budoxr.manifestations.presentation.usecase.ManifestationInfoUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.getValue

class JournalViewModel(private val context: Context) : ViewModel(), KoinComponent {
    private val localStorage: LocalStorage by inject()
    private val manifestationInfoUseCase : ManifestationInfoUseCase by inject()
    private val journalInfoUseCase : JournalInfoUseCase by inject()
    private val journalInsertUseCase : JournalInsertUseCase by inject()
    private val journalDeleteUseCase : JournalDeleteUseCase by inject()
    private val journalLastIdUseCase : JournalLastIdUseCase by inject()
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
    val flowOfJournals = journalInfoUseCase.invoke().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(FLOW_WHILESUBSCRIBED),
        initialValue = emptyList()
    )
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
                flowOfJournals,
                refreshing
            ) { lessons,
                manifestations,
                _,
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

    fun saveJournal(journal: JournalEntity) {
        Log.d(TAG, "saveJournal() -> called, id: ${journal.id ?: "null}"}")
        viewModelScope.launch(Dispatchers.IO) {
            val matchFound = util.performAsyncOperation(scope = this, timeout = WAIT_DEFERRED, timeUnit = java.util.concurrent.TimeUnit.SECONDS, dispatcher = Dispatchers.IO) {
                journalAnswerExistUseCase.invoke(journal.lessonDay, journal.manifestationId, journal.question)
            }.await()

            matchFound?.let {
                journalInsertUseCase.invoke(journal.copy(id = it))
            } ?: run {
                journalInsertUseCase.invoke(journal)
            }

        }

    }

    fun deleteJournal(journalId: Int) {
        Log.d(TAG, "deleteJournal() -> called, journal id: $journalId")
        viewModelScope.launch {
            journalDeleteUseCase.invoke(journalId)
        }

    }

    suspend fun lastId(): Int =
        journalLastIdUseCase.invoke()


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