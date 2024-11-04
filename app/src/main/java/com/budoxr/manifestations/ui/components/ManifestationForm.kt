package com.budoxr.manifestations.ui.components

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.budoxr.manifestations.R
import com.budoxr.manifestations.commons.CATEGORIES
import com.budoxr.manifestations.commons.CommonValues.oneDayMillis
import com.budoxr.manifestations.commons.fromFechaTimeDb
import com.budoxr.manifestations.commons.toFechaTimeDb
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import com.budoxr.manifestations.ui.theme.ManifestationsTheme
import java.util.Date

@Composable
fun ManifestationForm(
    item: ManifestationModel,
    isDarkTheme: Boolean,
    saveManifestation: (ManifestationModel, Context) -> Unit,
    modifier: Modifier
) {

    Log.i(TAG, "compose / re-compose")

    val focusManager: FocusManager = LocalFocusManager.current
    val context = LocalContext.current
    val lineSpacing = dimensionResource(R.dimen.line_spacing_1)

    var overview by remember { mutableStateOf(TextFieldValue(item.overview)) }
    var description by remember { mutableStateOf(TextFieldValue(item.description)) }
    var creationDate by remember { mutableStateOf(item.creationDate.fromFechaTimeDb()) }
    var dueDate by remember { mutableStateOf(item.dueDate.fromFechaTimeDb()) }
    var category = remember { mutableStateOf(TextFieldValue(item.category)) }

    val onCreationDateSelected: (Long?) -> Unit = { millis ->
        Log.d(TAG, "onCreationDateSelected() -> invoked, millis: $millis")
        if (millis != null) {
            creationDate = Date( millis + oneDayMillis )
        }
    }
    val onDueDateSelected: (Long?) -> Unit = { millis ->
        Log.d(TAG, "onDueDateSelected() -> invoked, millis: $millis")
        if (millis != null) {
            dueDate = Date( millis + oneDayMillis )
        }
    }

    Column (modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = overview,
            onValueChange = { newValue ->
                val capitalizedText = newValue.text.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                }
                overview = newValue.copy(text = capitalizedText)
            },
            label = { Text( text = stringResource(R.string.label_overview)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
//        colors = textFieldColors,
            modifier = Modifier
                .padding(vertical = lineSpacing)
                .fillMaxWidth()
        )
        OutlinedTextField(
            value = description,
            onValueChange = { newValue ->
                val capitalizedText = newValue.text.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                }
                description = newValue.copy(text = capitalizedText)
            },
            label = { Text( text = stringResource(R.string.label_description)) },
            maxLines = 9,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            ),
//        colors = textFieldColors,
            modifier = Modifier
                .padding(vertical = lineSpacing)
                .fillMaxWidth()
        )

//        DatePickerDocked()
        DatePickerFieldToModal(
            label = stringResource(R.string.label_creation_date),
            date = creationDate.time - oneDayMillis,
            onDateSelected = onCreationDateSelected,
            modifier = Modifier
        )

        DatePickerFieldToModal(
            label = stringResource(R.string.label_due_date),
            date = dueDate.time - oneDayMillis,
            onDateSelected = onDueDateSelected,
            modifier = Modifier.padding(vertical = lineSpacing)
        )

        val categoriesArray: Array<String> = stringArrayResource(id = R.array.categories_array)
        ComboBox(
            items = categoriesArray,
            label = stringResource(R.string.label_category),
            field = category,
            omitLabel = false,
            modifier = Modifier
        )

    }

    // save every time the compose / re-compose is called
    saveManifestation.invoke(
        ManifestationModel(
            id = item.id,
            overview = overview.text,
            description = description.text,
            creationDate = creationDate.toFechaTimeDb(),
            dueDate = dueDate.toFechaTimeDb(),
            category = category.value.text,
        ),
        context,
    )

}


@Preview(showBackground = true)
@Composable
fun ManifestationFormItemPreview() {

    val marginHorizontal = dimensionResource(R.dimen.margin_horizontal)
    val item = ManifestationModel(
        id = null,
        overview = "Ingreso de USD 6K",
        description = "Estoy muy feliz y agradecido por por haber manifestado antes del 7 de mayo del 2025, ingresos por USD 6K",
        creationDate = Date().toFechaTimeDb(),
        dueDate = Date().toFechaTimeDb(),
        category = CATEGORIES.WEALTH.key,
    )

    ManifestationsTheme {

        Surface( modifier = Modifier
            .fillMaxSize()
        ) {
            ManifestationForm(
                item = item,
                isDarkTheme = false,
                saveManifestation = { _, _ -> },
                modifier = Modifier.padding(horizontal = marginHorizontal)
            )
        }

    }
}

private const val TAG = "che.ManifestationForm"