package com.budoxr.manifestations.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.budoxr.manifestations.R
import com.budoxr.manifestations.ui.theme.ManifestationsTheme

@Composable
fun FieldFormCombo(
    items: Array<String>,
    label: String,
    field: String,
    enabled: Boolean = true,
) {
    val value = remember { mutableStateOf(TextFieldValue(field)) }

    val lineSpacing = dimensionResource(R.dimen.line_spacing_1)

    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall
    )
    Spacer(modifier = Modifier.height(lineSpacing))
    ComboBox(
        items = items,
        label = label,
        field = value,
        enabled = enabled,
        modifier = Modifier
    )

}


@Composable
@Preview(showBackground = true)
private fun FieldFormComboPreview() {
    val items = arrayOf("Item 1", "Item 2", "Item 3")
    val label = ""
    val field = "Item 2"

    ManifestationsTheme {

        Surface(modifier = Modifier.padding(4.dp)) {
            FieldFormCombo(
                items = items,
                label = label,
                field = field,
                enabled = true,
            )
        }
    }

}