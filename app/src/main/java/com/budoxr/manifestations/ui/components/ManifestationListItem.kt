package com.budoxr.manifestations.ui.components

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.budoxr.manifestations.R
import com.budoxr.manifestations.commons.CATEGORIES
import com.budoxr.manifestations.commons.onIntType
import com.budoxr.manifestations.commons.toFechaTimeDb
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import com.budoxr.manifestations.ui.theme.ManifestationsTheme
import com.budoxr.manifestations.ui.theme.alert
import com.budoxr.manifestations.ui.theme.bright
import com.budoxr.manifestations.ui.theme.gray
import com.budoxr.manifestations.ui.theme.grayLight
import com.budoxr.manifestations.ui.theme.orange
import com.budoxr.manifestations.ui.theme.passion
import java.util.Date

@Composable
fun ManifestationListItem(
    item: ManifestationModel,
    days: Long,
    isDarkTheme: Boolean,
    categoryColor: (String) -> Color,
    onLongPress: onIntType,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lineSpacing = dimensionResource(R.dimen.line_spacing_1)
    val marginHorizontal = dimensionResource(R.dimen.margin_horizontal)
    val separator = 2.dp

    Card(
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, gray),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = Modifier
            .pointerInput(Unit) {
            detectTapGestures(
                onLongPress = {
                    onLongPress.invoke(item.id!!)
                }
            )
    }) {
        Column (modifier = modifier
            .fillMaxWidth()
            .padding(top = marginHorizontal, bottom = lineSpacing, start = marginHorizontal, end = marginHorizontal)
        ) {
            Text(
                text = item.overview,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.padding(vertical = lineSpacing))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.padding(vertical = separator))
            Row (modifier = Modifier
                .fillMaxWidth()
            ) {
                Column {
                    Row (verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.label_days) + ":",
                            style = MaterialTheme.typography.labelMedium,
                        )
                        Spacer(modifier = Modifier.padding(horizontal = separator))
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background( if(days >= 15) grayLight else if(days < 15 && days >= 5) orange else if(days < 5 && days >= 1) Color.Magenta else alert )
                        ) {
                            Text(
                                text = days.toString(), style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
                Column (modifier = Modifier
                    .weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Box (
                        modifier = modifier
                            .clip(MaterialTheme.shapes.small)
                            .background(categoryColor(item.category)),
                    ) {
                        Text(
                            text = item.category,
                            style = MaterialTheme.typography.labelMedium,
                            color = bright,
                            modifier = Modifier
                                .padding(lineSpacing)
                        )
                    }
                }
            }

        }

    }

}


@Composable
@Preview(showBackground = true)
fun ManifestationListItemPreview() {
    val marginHorizontal = dimensionResource(R.dimen.margin_horizontal)
    val lineSpacing = dimensionResource(R.dimen.line_spacing_1)

    val item = ManifestationModel(
            id = 2,
            overview = "Facturacion mensual de USD 250K",
            description = "Estoy muy feliz y agradecido haber manifestado antes del 7 de Mayo del 2025, una facturacion mensual de ingresos por USD 250K.",
            creationDate = Date().toFechaTimeDb(),
            dueDate = Date().toFechaTimeDb(),
            category = CATEGORIES.WEALTH.key,
        )

    ManifestationsTheme {
        Surface(modifier = Modifier.fillMaxSize()
            .padding(vertical = lineSpacing, horizontal = marginHorizontal)
        ) {
            Column {
                ManifestationListItem(
                    item = item,
                    days = 4,
                    isDarkTheme = false,
                    categoryColor = { category-> passion },
                    onLongPress = { _ -> },
                    modifier = Modifier
                )
            }

        }
    }

}