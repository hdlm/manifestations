package com.budoxr.manifestations.presentation.presenters

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewModelScope
import com.budoxr.manifestations.commons.CategoryHelper
import com.budoxr.manifestations.commons.CommonValues
import com.budoxr.manifestations.commons.util.Utily
import com.budoxr.manifestations.data.mapper.toEntity
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import com.budoxr.manifestations.presentation.domain.SessionModel
import com.budoxr.manifestations.presentation.usecase.LessonInfoUseCase
import com.budoxr.manifestations.presentation.usecase.ManifestationDeleteUseCase
import com.budoxr.manifestations.presentation.usecase.ManifestationInfoUseCase
import com.budoxr.manifestations.presentation.usecase.ManifestationInsertUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import timber.log.Timber

class ManifestationViewModel : KoinViewModel() {
    private val manifestationInfoUseCase: ManifestationInfoUseCase by inject()
    private val manifestationInsertUseCase: ManifestationInsertUseCase by inject()
    private val manifestationDeleteUseCase: ManifestationDeleteUseCase by inject()
    private val lessonInfoUseCase: LessonInfoUseCase by inject()
    val util: Utily by inject()
    private val categoryHelper: CategoryHelper by inject()

    val manifestations = manifestationInfoUseCase.invoke().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(CommonValues.FLOW_WHILESUBSCRIBED),
        initialValue = emptyList()
    )
    val flowOfLessons = lessonInfoUseCase.invoke().stateIn(
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

    init {
        viewModelScope.launch {

            com.budoxr.manifestations.commons.util.combine(
                manifestations,
                refreshing
            ) { manifestations,
                refreshing ->

                if (refreshing) {
                    Timber.tag(TAG).d("refreshing: $refreshing")
                    return@combine ManifestationScreenUiState.Loading
                }

                ManifestationScreenUiState.ListManifestation(manifestations = manifestations)

            }.catch { throwable ->
                throwable.printStackTrace()
                _uiState.value = ManifestationScreenUiState.Error(throwable.message)
                Timber.tag(TAG).e("error: ${throwable.message}")
            }.collect {
                _uiState.value = it
            }
        }
        refresh(force = false)
    }


    fun refresh(force: Boolean = true ) {
        viewModelScope.launch {
            runCatching {
                if (force) {
                    refreshing.value = true
                    delay(20)
                    refreshing.value = false
                }
            }
        }
    }


    fun dateDifference(startDate: String, endDate: String): Long =
        util.dateDifference(startDate, endDate)


    fun categoryColor(categoryKey: String): Color =
        categoryHelper.getCategoryColor(categoryKey)


    /**
     * The method return the id of the last manifestation added to the database.
     * If the database is empty, the method return 0.
     */
    suspend fun lastManifestationId(): Int =
        manifestationInfoUseCase.lastId() ?: 0


    fun addManifestation(manifestation: ManifestationModel) {
        Timber.tag(TAG).d("addManifestation() -> called, id: ${manifestation.id ?: "null"}")
        viewModelScope.launch(Dispatchers.IO) {
            manifestationInsertUseCase.invoke(manifestation.toEntity())
        }

    }

    fun deleteManifestation(manifestation: ManifestationModel) {
        Timber.tag(TAG).d("deleteManifestation() -> called, id: ${manifestation.id}")
        viewModelScope.launch(Dispatchers.IO) {
            manifestationDeleteUseCase.invoke(manifestation.toEntity())
        }
    }

    fun editManifestation(manifestation: ManifestationModel) {
        Timber.tag(TAG).d("editManifestation() -> called, id: ${manifestation.id}")
        TODO ("not implemented")
    }


    fun changeToAddManifestationState() {
        Timber.tag(TAG).d("changeToAddManifestationState() -> called.")
        _uiState.update {
            ManifestationScreenUiState.AddManifestation
        }

    }

    fun changeToEditManifestationState(manifestation: ManifestationModel) {
        Timber.tag(TAG).d("changeToEditManifestationState() -> called, id: ${manifestation.id}")
        _uiState.update {
            ManifestationScreenUiState.EditManifestation(manifestation)
        }

    }

    fun changeToDeleteManifestationState(manifestation: ManifestationModel) {
        Timber.tag(TAG).d("changeToDeleteManifestationState() -> called, id: ${manifestation.id}")
        _uiState.update {
            ManifestationScreenUiState.DeleteManifestation(manifestation)
        }

    }


    fun changeToManifestationDetailsState(manifestation: ManifestationModel) {
        Timber.tag(TAG).d("changeToManifestationDetailsState() -> called, id: ${manifestation.id}")
        TODO ("not implemented")
    }

}


sealed interface ManifestationScreenUiState {
    data object Loading : ManifestationScreenUiState

    data class Error(
        val errorMessage: String? = null
    ) : ManifestationScreenUiState

    data class ListManifestation(
        val manifestations: List<ManifestationModel> = emptyList(),
    ) : ManifestationScreenUiState

    data object AddManifestation : ManifestationScreenUiState

    data class EditManifestation(
        val manifestation: ManifestationModel? = null
    ) : ManifestationScreenUiState

    data class DeleteManifestation(
        val manifestation: ManifestationModel? = null
    ) : ManifestationScreenUiState

    data class ManifestationDetails(
        val manifestation: ManifestationModel? = null
    ) : ManifestationScreenUiState


}

private const val TAG = "che.ManifestationViewModel"