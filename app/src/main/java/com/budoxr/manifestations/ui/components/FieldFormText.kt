package com.budoxr.manifestations.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.budoxr.manifestations.R
import com.budoxr.manifestations.ui.theme.ManifestationsTheme


@Composable
fun FieldFormText(
    label: String,
    field: String,
    onValueChange: (String) -> Unit,
) {
    val focusManager: FocusManager = LocalFocusManager.current

    val lineSpacing = dimensionResource(R.dimen.line_spacing_1)

    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall
    )
    Spacer(modifier = Modifier.height(lineSpacing))
    Textfield(
        value = field,
        textLabel = label,
        onValueChange = onValueChange,
        keyboardType = KeyboardType.Text,
        keyboardActions = KeyboardActions(
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        ),
        imeAction = ImeAction.Next,
    )
}


@Composable
@Preview(showBackground = true)
private fun FieldFormTextPreview() {
    ManifestationsTheme {
        Column {
            FieldFormText(
                label = "Name",
                field = "Henry",
                onValueChange = {}
            )

        }
    }

}