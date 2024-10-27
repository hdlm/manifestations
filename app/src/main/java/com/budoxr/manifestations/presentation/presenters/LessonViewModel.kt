package com.budoxr.manifestations.presentation.presenters

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budoxr.manifestations.commons.CommonValues.WAIT_DEFAULT
import com.budoxr.manifestations.commons.CommonValues.FLOW_WHILESUBSCRIBED
import com.budoxr.manifestations.commons.onDismissType
import com.budoxr.manifestations.data.mapper.emptyLessonModel
import com.budoxr.manifestations.data.repositories.LocalStorage
import com.budoxr.manifestations.presentation.domain.LessonModel
import com.budoxr.manifestations.presentation.domain.LessonsWrapper
import com.budoxr.manifestations.presentation.domain.SessionModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LessonViewModel(private val context: Context) : ViewModel(), KoinComponent {

    private val localStorage : LocalStorage by inject()

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


    fun refresh(force: Boolean = true ) {
        viewModelScope.launch {
            runCatching {
                refreshing.value = true
                delay(20)
                refreshing.value = false
            }
        }
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
