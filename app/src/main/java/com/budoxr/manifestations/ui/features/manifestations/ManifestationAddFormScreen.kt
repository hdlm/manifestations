package com.budoxr.manifestations.ui.features.manifestations

import com.budoxr.manifestations.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.budoxr.manifestations.commons.fromFechaTimeDb
import com.budoxr.manifestations.commons.onDismissType
import com.budoxr.manifestations.commons.onLongType
import com.budoxr.manifestations.commons.onStringType
import com.budoxr.manifestations.presentation.presenters.ManifestationAddFormUiState
import com.budoxr.manifestations.presentation.presenters.ManifestationAddFormViewModel
import com.budoxr.manifestations.presentation.presenters.ManifestationFormState
import com.budoxr.manifestations.ui.components.DatePickerFieldToModal
import com.budoxr.manifestations.ui.components.FieldFormCombo
import com.budoxr.manifestations.ui.components.FieldFormText
import com.budoxr.manifestations.ui.theme.ManifestationsTheme
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@Composable
fun ManifestationAddFormScreen(
    viewModel: ManifestationAddFormViewModel = koinViewModel()
) {
    Timber.tag(TAG).i("compose / composable")

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ManifestationAddForm(
            formState = formState,
            uiState = uiState,
            onOverviewChange = viewModel::onOverviewChange,
            onDescriptionChange = viewModel::onDescriptionChange,
            onStartDateChange = viewModel::onStartDateChange,
            onDueDateChange = viewModel::onDueDateChange,
            onCategoryChange = viewModel::onCategoryChange,
            onSaveClick = viewModel::onSaveClick
        )

    }

}


@Composable
private fun ManifestationAddForm(
    formState: ManifestationFormState,
    uiState: ManifestationAddFormUiState,
    onOverviewChange: onStringType,
    onDescriptionChange: onStringType,
    onStartDateChange: onLongType,
    onDueDateChange: onLongType,
    onCategoryChange: onStringType,
    onSaveClick: onDismissType,
) {
    val lineSpacing2x = dimensionResource(R.dimen.line_spacing_2)

    val categoriesArray: Array<String> = stringArrayResource(id = R.array.categories_array)


    FieldFormText(
        label = stringResource(R.string.label_overview),
        field = formState.overview,
        onValueChange = onOverviewChange
    )

    Spacer(modifier = Modifier.padding(vertical = lineSpacing2x))

    FieldFormText(
        label = stringResource(R.string.label_description),
        field = formState.description,
        onValueChange = onOverviewChange
    )

    Spacer(modifier = Modifier.padding(vertical = lineSpacing2x))

    FieldFormText(
        label = stringResource(R.string.label_overview),
        field = formState.overview,
        onValueChange = onOverviewChange
    )

    Spacer(modifier = Modifier.padding(vertical = lineSpacing2x))

    DatePickerFieldToModal(
        label = stringResource(R.string.label_start_date),
        date = formState.startDate.fromFechaTimeDb().time,
        onDateChange = onStartDateChange,
    )

    Spacer(modifier = Modifier.padding(vertical = lineSpacing2x))

    DatePickerFieldToModal(
        label = stringResource(R.string.label_due_date),
        date = formState.dueDate.fromFechaTimeDb().time,
        onDateChange = onDueDateChange,
    )

    Spacer(modifier = Modifier.padding(vertical = lineSpacing2x))

    FieldFormCombo(
        items = categoriesArray,
        label = stringResource(R.string.label_category),
        field = formState.category,
        enabled = true,
    )



}


@Composable
@Preview(showBackground = true)
private fun ManifestationAddFormScreenPreview() {
    val formState = ManifestationFormState(
        overview = "viajar a japon",
        description = "viajar a japon antes del 1ro de abril del 2026",
        startDate = "2026-04-01T07:08:09",
        dueDate = "2026-05-01T07:08:09",
        category = "passion",
        overviewError = null,
        descriptionError = null,
        dueDateError = null,
        categoryError = null,
        isValid = true
    )
    val uiState = ManifestationAddFormUiState.Form(null)

    ManifestationsTheme {

        Surface(modifier = Modifier.fillMaxSize()
            .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                ManifestationAddForm(
                    formState = formState,
                    uiState = uiState,
                    onOverviewChange = { _ -> },
                    onDescriptionChange = { _ -> },
                    onStartDateChange = { _ -> },
                    onDueDateChange = { _ -> },
                    onCategoryChange = { _ -> },
                    onSaveClick = { },
                )
            }
        }
    }
}


private const val TAG = "che.ManifestationAddFormScreen"