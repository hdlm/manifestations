package com.budoxr.manifestations.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.budoxr.manifestations.R
import com.budoxr.manifestations.commons.toFechaTimeDb
import com.budoxr.manifestations.data.database.entities.JournalEntity
import com.budoxr.manifestations.presentation.domain.LessonModel
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import com.budoxr.manifestations.ui.theme.ManifestationsTheme
import com.budoxr.manifestations.ui.theme.gray
import com.budoxr.manifestations.ui.theme.grayLight
import com.budoxr.manifestations.ui.theme.orange
import com.budoxr.manifestations.ui.theme.passion
import java.util.Date

@Composable
fun JournalCardItem(
    manifestation: ManifestationModel,
    item: JournalEntity,
    lessons: List<LessonModel>,
    days: Long,
    categoryColor: Color,
    modifier: Modifier
) {
    val marginHorizontal = dimensionResource(id = R.dimen.margin_horizontal)
    val lineSpacing = dimensionResource(id = R.dimen.line_spacing_1)
    val separator = 2.dp
    val questions  by remember { mutableStateOf(listOf<String>()) }


    Card(
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, gray),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = modifier
    ) {
        Column (modifier = modifier
            .fillMaxWidth()
            .padding(top = marginHorizontal, start = marginHorizontal, end = marginHorizontal)
        ) {
            Text(
                text = manifestation.overview,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.padding(vertical = lineSpacing))
            Text(
                text = manifestation.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.padding(vertical = separator))
            Row (modifier = Modifier.fillMaxWidth() ) {
                Column (verticalArrangement = Arrangement.Bottom) {
                    Row (verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.label_days) + ":",
                            style = MaterialTheme.typography.labelMedium,
                        )
                        Spacer(modifier = Modifier.padding(horizontal = separator))
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
//                                        .size(40.dp)
//                                        .clip(CircleShape)
                                .clip(MaterialTheme.shapes.small)
                                .background( if(days >= 15) grayLight else if(days < 15 && days >= 5) orange else if(days < 5 && days >= 1) Color.Magenta else passion )
                        ) {
                            Text( modifier = Modifier.padding(horizontal = lineSpacing, vertical = 3.dp),
                                text = days.toString(), style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
                Column (modifier = Modifier
                    .weight(1f),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End
                ) {
                    Box (
                        modifier = modifier
                            .clip(MaterialTheme.shapes.small)
                            .background(categoryColor),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Text(
                            text = manifestation.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .padding(horizontal = lineSpacing, vertical = 3.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(vertical = lineSpacing))
            //TODO definir como sera esta parte de la tarjeta, pienso que podria ser un _more_ para abrir la tarjeta y mostrar las preguntas y respuesta, o abrir un Dialog o BootomSheet con el contenido de las preguntas y respuestas
//            val journalQuestions = lessons.find { it.day == days.toInt() }!!.journal
//            repeat(journalQuestions.size) { idx ->
//                HorizontalDraggableJournalQuestionList(
//                    number = (idx + 1).toString(),
//                    question = journalQuestions[idx],
//                    answer = item.answer
//                )
//            }

        }

    }
}


@Composable
@Preview(showBackground = true)
fun JournalCardItemPreview() {
    var contentSize by remember { mutableStateOf( IntSize(0, 0)) }
    val density = LocalDensity.current
    val factor = 0.95f
    val itemHeightDp  = 150.dp

    val manifestation = ManifestationModel(
        id = 1,
        overview = "Yo solo quiero cantar en la radio",
        description = "Para ganar mi primer millon",
        startDate = Date(1741268232000L).toFechaTimeDb(),
        dueDate = Date(1746057600000L).toFechaTimeDb(),
        category = "Passion"
    )
    val lesson = LessonModel(
        manifestationId = 1,
        day = 1,
        subject = "The 80/20 trap",
        summary = listOf("summary"),
        journal = listOf("journal"),
        meditation = "meditation"
    )
    val journalEntity1 =  JournalEntity(
        id = 1,
        lessonId = 1,
        questionIdx = 1,
        questionSlug = "What are you committed to during this Quest?",
        answer = "Abc",
        responseDate = 1741268296748L
    )
    val journalEntity2 =  JournalEntity(
        id = 2,
        lessonId = 1,
        questionIdx = 2,
        questionSlug = "What is going to be your focus point?",
        answer = "Def",
        responseDate =1741268296751L
    )


    ManifestationsTheme {

        Surface( modifier = Modifier
            .fillMaxWidth()
        ) {
            JournalCardItem(
                manifestation = manifestation,
                item = journalEntity1,
                lessons = listOf(lesson),
                days = 20,
                categoryColor = passion,
                modifier = Modifier
//                    .size(
//                        width = with(density) { contentSize.width.toDp() * factor },
//                        with(density) { contentSize.height.toDp() }
//                    )
            )

        }

    }

}