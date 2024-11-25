package com.budoxr.manifestations.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.budoxr.manifestations.commons.onDismissType
import com.budoxr.manifestations.ui.theme.ManifestationsTheme

@Composable
fun InfoDialog(
    modifier: Modifier,
    title: String,
    msg: String,
    buttonLabel: String,
    onDone: onDismissType,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = { onDone.invoke() },
        title = { Text(text = title, style = MaterialTheme.typography.labelLarge) },
        text = {
            Text(
                text = msg,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Companion.Normal,
            )
        },
        confirmButton = {
            TextButton(onClick = { onDone.invoke() }) {
                Text(buttonLabel)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    )

}

@Composable
@Preview(showBackground = true)
fun InfoDialogPreview() {

    ManifestationsTheme {
        InfoDialog(
            modifier = Modifier,
            title = "titulo",
            msg = "mensaje",
            buttonLabel = "aceptar",
            onDone = {}
        )
    }

}
