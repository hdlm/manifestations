package com.budoxr.manifestations.presentation.presenters

import androidx.lifecycle.viewModelScope
import com.budoxr.manifestations.commons.CommonValues.oneDayMillis
import com.budoxr.manifestations.commons.toFechaTimeDb
import com.budoxr.manifestations.data.database.entities.ManifestationEntity
import com.budoxr.manifestations.presentation.usecase.ManifestationInsertUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import timber.log.Timber
import java.util.Date

class ManifestationAddFormViewModel : KoinViewModel() {

    private val manifestationInsertUseCase: ManifestationInsertUseCase by inject()

    private val _uiState = MutableStateFlow<ManifestationAddFormUiState>(ManifestationAddFormUiState.Form())
    val uiState: StateFlow<ManifestationAddFormUiState>
        get() = _uiState.asStateFlow()


    private val _formState = MutableStateFlow(ManifestationFormState())
    val formState : StateFlow<ManifestationFormState>
        get() = _formState.asStateFlow()

    fun onOverviewChange(overview: String) {
        _formState.update {
            it.copy(
                overview = overview,
                overviewError = when {
                    overview.isBlank() ->  ErrorCode.FIELD_REQUIRED.code
                    overview.length < 10 -> ErrorCode.OVERVIEW_MIN_LENGTH.code
                    else -> null
                }
            )
        }
        validateForm()
    }

    fun onDescriptionChange(description: String) {
        _formState.update {
            it.copy(
                description = description,
                descriptionError = when {
                    description.isBlank() ->  ErrorCode.FIELD_REQUIRED.code
                    else -> null
                }
            )
        }
        validateForm()
    }

    fun onStartDateChange(millis: Long?) {
        Timber.tag(TAG).d("onStartDateChange() -> called, millis: $millis")
        if (millis != null) {
            val startDate = Date( millis + oneDayMillis ).toFechaTimeDb()
            _formState.update {
                it.copy(
                    startDate = startDate,
                    startDateError = when {
                        startDate.isBlank() ->  ErrorCode.FIELD_REQUIRED.code
                        else -> null
                    }
                )
            }
        }
    }

    fun onDueDateChange(millis: Long?) {
        Timber.tag(TAG).d("onDueDateChange() -> called, millis: $millis")
        if (millis != null) {
            val dueDate = Date( millis + oneDayMillis ).toFechaTimeDb()
            _formState.update {
                it.copy(
                    startDate = dueDate,
                    dueDateError = when {
                        dueDate.isBlank() ->  ErrorCode.FIELD_REQUIRED.code
                        else -> null
                    }
                )
            }
        }
    }

    fun onCategoryChange(category: String) {
        Timber.tag(TAG).d("onCategoryChange() -> called, category: $category")
        _formState.update {
            it.copy(
                category = category,
                categoryError = when {
                    category.isBlank() -> ErrorCode.FIELD_REQUIRED.code
                    else -> null
                }
            )
        }
    }

    fun onSaveClick() {
        Timber.tag(TAG).d("onSaveClick) -> called.")

        val form = _formState.value

        viewModelScope.launch {
            val manifestationEntity = ManifestationEntity(
                overview =  form.overview,
                description = form.description,
                startDate = form.startDate,
                dueDate = form.dueDate,
                category = form.category
            )
            Timber.tag(TAG).i("Saving new manifestation in the database.")
            manifestationInsertUseCase.invoke(manifestationEntity)
        }
    }

    private fun validateForm() {
        _uiState.value = ManifestationAddFormUiState.Form(
            errorType = if (_formState.value.overview.isBlank()) {
                ErrorCode.FIELD_REQUIRED.code
            } else if (_formState.value.description.isBlank()) {
                ErrorCode.FIELD_REQUIRED.code
            } else if (_formState.value.startDate.isBlank()) {
                ErrorCode.FIELD_REQUIRED.code
            } else if (_formState.value.dueDate.isBlank()) {
                ErrorCode.FIELD_REQUIRED.code
            } else if (_formState.value.category.isBlank()) {
                ErrorCode.FIELD_REQUIRED.code
            } else {
                null
            }
           )
        _formState.update {
            it.copy(
                isValid = it.overview.isNotBlank() &&
                        it.description.isNotBlank() &&
                        it.dueDate.isNotBlank() &&
                        it.category.isNotBlank()
            )
        }
    }


    companion object {
        private const val TAG = "che.ManifestationAddFormViewModel "
    }

}

sealed interface ManifestationAddFormUiState {
    data class Form(val errorType: Int?  = null) : ManifestationAddFormUiState
}

data class ManifestationFormState(
    val overview: String = "",
    val description: String = "",
    val startDate: String = "2006-05-06T11:12:13",
    val dueDate: String = "2006-06-07T12:13:14",
    val category: String = "",
    val overviewError: Int? = null,
    val descriptionError: Int? = null,
    val startDateError: Int? = null,
    val dueDateError: Int? = null,
    val categoryError : Int? = null,
    val isValid: Boolean = false,
)

enum class ErrorCode(val code: Int) {
    FIELD_REQUIRED(100),
    OVERVIEW_MIN_LENGTH(101);

    /**
     * Optional utility function to look up an ErrorCode by its raw integer value.
     */
    companion object {
        fun fromCode(code: Int): ErrorCode? = entries.find { it.code == code }
    }
}