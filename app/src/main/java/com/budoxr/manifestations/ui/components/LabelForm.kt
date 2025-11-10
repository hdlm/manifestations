package com.budoxr.manifestations.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.budoxr.manifestations.ui.theme.ManifestationsTheme
import com.budoxr.manifestations.ui.theme.bright
import com.budoxr.manifestations.ui.theme.grayDark


@Composable
fun LabelForm(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = grayDark,
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
        )

        Box( // Or use a Card here for a background
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = grayDark, // Color of the border
                    shape = MaterialTheme.shapes.extraSmall // Use a shape for the border
                )
                .padding(16.dp) // Padding inside the bordered area
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge // Style the value
            )
        }
    }
}


@Composable
@Preview(showBackground = true)
fun LabelFormPreview() {
    ManifestationsTheme {
        Column(modifier = Modifier.fillMaxWidth()
            .padding(16.dp)
            .background(bright)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            LabelForm(label = "Nam", value = "John Doe")

        }
    }

}

private const val TAG = "che.Label"