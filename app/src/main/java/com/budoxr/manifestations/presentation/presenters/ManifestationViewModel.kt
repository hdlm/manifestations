package com.budoxr.manifestations.presentation.presenters

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budoxr.manifestations.commons.CategoryHelper
import com.budoxr.manifestations.commons.CommonValues
import com.budoxr.manifestations.presentation.domain.SessionModel
import com.budoxr.manifestations.presentation.usecase.ManifestationInfoUseCase
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
    private val manifestationInfoUseCase: ManifestationInfoUseCase by inject()
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

    /** this value avoid to show the same error twince */
    var errowShowed: Boolean = false

    init {
        viewModelScope.launch {

            com.budoxr.manifestations.commons.util.combine(
                flowOfManifestations,
                refreshing
            ) { _,
                refreshing ->

                if (refreshing) {
                    Log.d(TAG, "refreshing: $refreshing")
                }

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

    fun refresh(force: Boolean = true ) {
        viewModelScope.launch {
            runCatching {
                refreshing.value = true
                delay(20)
                refreshing.value = false
            }
        }
    }

    fun categoryColor(categoryKey: String): Color =
        categoryHelper.getCategoryColor(categoryKey)

}


sealed interface ManifestationScreenUiState {
    data object Loading : ManifestationScreenUiState

    data class Error(
        val errorMessage: String? = null
    ) : ManifestationScreenUiState

    object Ready : ManifestationScreenUiState

}

private const val TAG = "che.ManifestationViewModel"