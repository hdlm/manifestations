package com.budoxr.manifestations.presentation.presenters

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budoxr.manifestations.commons.CategoryHelper
import com.budoxr.manifestations.commons.CommonValues
import com.budoxr.manifestations.commons.CommonValues.WAIT_DEFAULT
import com.budoxr.manifestations.commons.onDismissType
import com.budoxr.manifestations.commons.util.Utily
import com.budoxr.manifestations.data.mapper.toEntity
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import com.budoxr.manifestations.presentation.domain.SessionModel
import com.budoxr.manifestations.presentation.usecase.ManifestationDeleteUseCase
import com.budoxr.manifestations.presentation.usecase.ManifestationInfoUseCase
import com.budoxr.manifestations.presentation.usecase.ManifestationInsertUseCase
import com.budoxr.manifestations.presentation.usecase.ManifestationInsertWorkerUseCase
import com.budoxr.manifestations.presentation.usecase.ManifestationLastIdUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ManifestationViewModel : ViewModel(), KoinComponent {
    private val manifestationLastIdUseCase : ManifestationLastIdUseCase by inject()
    private val manifestationInfoUseCase: ManifestationInfoUseCase by inject()
    private val manifestationInsertUseCase: ManifestationInsertUseCase by inject()
    private val manifestationDeleteUseCase: ManifestationDeleteUseCase by inject()
    private val manifestationInsertWorkerUseCase: ManifestationInsertWorkerUseCase by inject()
    val util: Utily by inject()
    private val categoryHelper: CategoryHelper by inject()

    val flowOfManifestations = manifestationInfoUseCase.invoke().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(CommonValues.FLOW_WHILESUBSCRIBED),
        initialValue = emptyList()
    )
    private val refreshing = MutableStateFlow(false)

    private val _uiState = MutableStateFlow<ManifestationScreenUiState>(ManifestationScreenUiState.Loading)
    val uiState: StateFlow<ManifestationScreenUiState>
        get() = _uiState

    private val _sessionModel by inject<SessionModel>()
    val session: SessionModel
        get() = _sessionModel

    /** this value avoid to show the same error twice */
    var errorShowed: Boolean = false

    init {
        viewModelScope.launch {

            com.budoxr.manifestations.commons.util.combine(
                flowOfManifestations,
                refreshing
            ) { _,
                refreshing ->

                if (refreshing) {
                    Log.d(TAG, "refreshing: $refreshing")
                    return@combine ManifestationScreenUiState.Loading
                }

                delay(50)
                ManifestationScreenUiState.Ready

            }.catch { throwable ->
                throwable.printStackTrace()
                _uiState.value = ManifestationScreenUiState.Error(throwable.message)
                Log.e(TAG, "error: ${throwable.message}")
            }.collect {
                _uiState.value = it
            }
        }
        refresh(force = false)
    }

    fun loading( onDone: onDismissType ) {
        viewModelScope.launch {
            _uiState.value = ManifestationScreenUiState.Loading
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
            _uiState.value = ManifestationScreenUiState.Error(errorMessage)
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


    fun dateDifference(startDate: String, endDate: String): Long =
        util.dateDifference(startDate, endDate)


    fun categoryColor(categoryKey: String, context: Context): Color =
        categoryHelper.getCategoryColor(categoryKey, context)


    suspend fun lastId(): Int  =
        manifestationLastIdUseCase.invoke()


    fun saveManifestation(manifestation: ManifestationModel, context: Context) {
        Log.d(TAG, "saveManifestation() -> called, id: ${manifestation.id ?: "null"}")
        viewModelScope.launch(Dispatchers.IO) {
//            manifestationInsertWorkerUseCase.saveManifestationWorker(manifestation, context)
            manifestationInsertUseCase.invoke(manifestation.toEntity())
        }

    }


    fun deleteManifestation(manifestation: ManifestationModel) {
        Log.d(TAG, "deleteManifestation() -> called, id: ${manifestation.id}")
        viewModelScope.launch(Dispatchers.IO) {
            manifestationDeleteUseCase.invoke(manifestation.toEntity())
        }
    }

}


sealed interface ManifestationScreenUiState {
    data object Loading : ManifestationScreenUiState

    data class Error(
        val errorMessage: String? = null
    ) : ManifestationScreenUiState

    object Ready : ManifestationScreenUiState

}

private const val TAG = "che.ManifestationViewModel"