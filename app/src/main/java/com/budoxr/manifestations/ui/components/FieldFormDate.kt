package com.budoxr.manifestations.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.budoxr.manifestations.R
import com.budoxr.manifestations.commons.CommonValues.oneDayMillis
import com.budoxr.manifestations.commons.fromFechaTimeDb
import com.budoxr.manifestations.ui.theme.ManifestationsTheme
import timber.log.Timber
import java.util.Date


@Composable
fun FieldFormDate(
    label: String,
    dateTimeStamp: String,
) {
    var date by remember { mutableStateOf(dateTimeStamp.fromFechaTimeDb()) }

    val lineSpacing = dimensionResource(R.dimen.line_spacing_1)

    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall
    )
    Spacer(modifier = Modifier.height(lineSpacing))
    val onDateSelected: (Long?) -> Unit = { millis ->
        Timber.tag(TAG).d("onDateSelected() -> invoked, millis: $millis")

        if (millis != null) {
            date = Date( millis + oneDayMillis )
        }
    }

    DatePickerFieldToModal(
        label = label,
        date = date.time - oneDayMillis,
        onDateChange = onDateSelected,
        modifier = Modifier
    )

}


@Composable
@Preview(showBackground = true)
private fun FieldFormDatePreview() {
    val label = ""
    val dateTimeStamp = "2025-11-01T12:14:16"

    ManifestationsTheme {
        Surface(modifier = Modifier.padding(4.dp)) {
            FieldFormDate(
                label = label,
                dateTimeStamp = dateTimeStamp,
            )
        }

    }
}

private const val TAG = "che.FieldFormDate"