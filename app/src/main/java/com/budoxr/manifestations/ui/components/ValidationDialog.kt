package com.budoxr.manifestations.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.budoxr.manifestations.commons.onBooleanType
import com.budoxr.manifestations.ui.theme.ManifestationsTheme
import com.budoxr.manifestations.ui.theme.blue
import kotlin.let

@Composable
fun ValidationDialog(
    modifier: Modifier,
    title: String?,
    msg: String,
    buttonLabels: Pair<String,String>,
    onDone: onBooleanType,
) {

    title?.let {
        AlertDialog(
            onDismissRequest = { onDone.invoke(false) },
            title = { Text(text = title, style = MaterialTheme.typography.labelLarge) },
            text = {
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Companion.Normal,
                )
            },
            confirmButton = {
                TextButton(onClick = { onDone.invoke(true) }) {
                    Text(buttonLabels.first)
                }
            },
            dismissButton = {
                TextButton(onClick = { onDone.invoke(false) }) {
                    Text(buttonLabels.second)
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        )
    } ?: run {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = { onDone.invoke(false) },
            text = {
                Text(
                    modifier = modifier,
                    textAlign = TextAlign.Companion.Center,
                    text = msg,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Companion.Normal,
                )
            },
            confirmButton = {
                TextButton(onClick = { onDone.invoke(true) }) {
                    Text(
                        buttonLabels.first,
                        color = blue
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { onDone.invoke(false) }) {
                    Text(
                        text = buttonLabels.second,
                        color = blue
                    )
                }
            },
        )
    }

}


@Composable
@Preview(showBackground = true)
fun ValidationDialogPreview() {
    ManifestationsTheme {
        ValidationDialog(
            modifier = Modifier,
            title = "titulo",
            msg = "mensaje",
            buttonLabels = Pair("aceptar", "cancelar"),
            onDone = {}
        )
    }

}