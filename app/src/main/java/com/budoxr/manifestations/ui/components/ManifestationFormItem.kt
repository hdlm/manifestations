package com.budoxr.manifestations.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.budoxr.manifestations.R
import com.budoxr.manifestations.commons.CATEGORIES
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
    var category by remember { mutableStateOf(TextFieldValue(item.category)) }

    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = blue,
        unfocusedContainerColor = gray,
        disabledContainerColor = alert,
    )

    Card(
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = Modifier
    ) {
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
            colors = textFieldColors,
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
            colors = textFieldColors,
            modifier = Modifier.padding(vertical = lineSpacing)
        )
        OutlinedTextField(
            value = category,
            onValueChange = { newValue ->
                val capitalizedText = newValue.text.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                }
                category = newValue.copy(text = capitalizedText)
            },
            label = { Text( text = stringResource(R.string.label_category)) },
            maxLines = 4,
            colors = textFieldColors,
            modifier = Modifier.padding(vertical = lineSpacing)
        )

    }

}


@Preview(showBackground = true)
@Composable
fun ManifestationFormItemPreview() {
    val listOfManifestations : List<ManifestationModel> = listOf(
        ManifestationModel(
            id = null,
            overview = "Ingreso de USD 6K",
            description = "Estoy muy feliz y agradecido por por haber manifestado antes del 7 de mayo del 2025, ingresos por USD 6K",
            creationDate = Date(),
            dueDate = Date(),
            category = CATEGORIES.WEALTH.key,
        ),
        ManifestationModel(
            id = null,
            overview = "Facturacion mensual de USD 250K",
            description = "estoy muy feliz y agradecido haber manifestado antes del 7 de Mayo del 2025, una facturacion mensual de ingresos por USD 250K.",
            creationDate = Date(),
            dueDate = Date(),
            category = CATEGORIES.WEALTH.key,
        ),
    )
    val viewModel = ManifestationViewModel()
    val manifestationState = ManifestationState(
        isDarkTheme = false,
        manifestations = listOfManifestations,
        categoryColor = viewModel::categoryColor
    )

    ManifestationsTheme {

        Surface( modifier = Modifier
            .fillMaxSize()
        ) {
            ManifestationScreenBody(
                manifestationState = manifestationState,
            )
        }
    }
}