package com.budoxr.manifestations.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.budoxr.manifestations.ui.theme.bright
import com.budoxr.manifestations.ui.theme.grayLight


@Composable
fun Textfield(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    textLabel: String,
    keyboardType: KeyboardType,
    keyboardActions: KeyboardActions,
    imeAction: ImeAction,
    trailingIcon: @Composable() (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 4.dp,
    ) {
        TextField(
            modifier = modifier.fillMaxWidth(),
            value = value,
            textStyle = MaterialTheme.typography.bodyMedium,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = textLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = grayLight
                )
            },
            trailingIcon = trailingIcon,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            colors =
                TextFieldDefaults.colors(
                    focusedContainerColor = bright,
                    unfocusedContainerColor = bright,
                    focusedIndicatorColor = bright,
                    unfocusedIndicatorColor = bright,
                    disabledIndicatorColor = bright,
                    errorIndicatorColor = bright,
                )

        )
    }
}