package com.budoxr.manifestations.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.budoxr.manifestations.R
import com.budoxr.manifestations.commons.CATEGORIES
import com.budoxr.manifestations.commons.fromFechaTimeDb
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import com.budoxr.manifestations.presentation.presenters.ManifestationViewModel
import com.budoxr.manifestations.ui.ManifestationScreenBody
import com.budoxr.manifestations.ui.ManifestationState
import com.budoxr.manifestations.ui.theme.ManifestationsTheme
import com.budoxr.manifestations.ui.theme.alert
import com.budoxr.manifestations.ui.theme.blue
import com.budoxr.manifestations.ui.theme.gray
import java.util.Date

@Composable
fun ManifestationFormItem(
    item: ManifestationModel,
    isDarkTheme: Boolean,
) {
    val lineSpacing = dimensionResource(R.dimen.line_spacing_1)

    var overview by remember { mutableStateOf(TextFieldValue(item.overview)) }
    var description by remember { mutableStateOf(TextFieldValue(item.description)) }
    var creationDate by remember { mutableStateOf(item.creationDate) }
    var dueDate by remember { mutableStateOf(item.dueDate) }
    var category = remember { mutableStateOf(TextFieldValue(item.category)) }

    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = blue,
        unfocusedContainerColor = gray,
        disabledContainerColor = alert,
    )

//    val onCreationDateSelected: (Long?) -> Unit = { millis ->
//        Log.d(TAG, "onCreationDateSelected() -> invoked, millis: $millis")
//        if (millis != null) {
//            creationDate = convertMillisToDate(millis)
//        }
//    }
//    val onDueDateSelected: (Long?) -> Unit = { millis ->
//        Log.d(TAG, "onDueDateSelected() -> invoked, millis: $millis")
//        if (millis != null) {
//            dueDate = convertMillisToDate(millis)
//        }
//    }
    val onDismiss: () -> Unit = {
        Log.d(TAG, "onDismiss() -> invoked")
    }

    Column {
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
//        colors = textFieldColors,
            modifier = Modifier.padding(vertical = lineSpacing)
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
            singleLine = true,
//        colors = textFieldColors,
            modifier = Modifier.padding(vertical = lineSpacing)
        )

//        DatePickerDocked()
        DatePickerFieldToModal(
            label = stringResource(R.string.label_creation_date),
            modifier = Modifier
        )

        DatePickerFieldToModal(
            label = stringResource(R.string.label_due_date),
            modifier = Modifier
        )

        val categoriesArray: Array<String> = stringArrayResource(id = R.array.categories_array)
        ComboBox(
            items = categoriesArray,
            label = stringResource(R.string.label_category),
            field = category,
            maxlength = 20,
            omitLabel = false,
            modifier = Modifier.padding(vertical = lineSpacing)
        )

    }

}


@Preview(showBackground = true)
@Composable
fun ManifestationFormItemPreview() {

    val item = ManifestationModel(
        id = null,
        overview = "Ingreso de USD 6K",
        description = "Estoy muy feliz y agradecido por por haber manifestado antes del 7 de mayo del 2025, ingresos por USD 6K",
        creationDate = Date(),
        dueDate = Date(),
        category = CATEGORIES.WEALTH.key,
    )

    ManifestationsTheme {

        Surface( modifier = Modifier
            .fillMaxSize()
        ) {
            ManifestationFormItem(
                item = item,
                isDarkTheme = false
            )
        }

    }
}

private const val TAG = "che.ManifestationFormItem"