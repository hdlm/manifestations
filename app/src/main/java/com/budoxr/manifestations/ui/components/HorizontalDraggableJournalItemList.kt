package com.budoxr.manifestations.ui.components

import android.content.Context
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.budoxr.manifestations.R
import com.budoxr.manifestations.commons.CATEGORIES
import com.budoxr.manifestations.commons.onIntType
import com.budoxr.manifestations.commons.toFechaTimeDb
import com.budoxr.manifestations.data.database.entities.JournalEntity
import com.budoxr.manifestations.data.database.entities.LessonEntity
import com.budoxr.manifestations.data.database.entities.ManifestationEntity
import com.budoxr.manifestations.data.database.entities.relations.LessonWithJournals
import com.budoxr.manifestations.presentation.domain.LessonModel
import com.budoxr.manifestations.ui.theme.ManifestationsTheme
import com.budoxr.manifestations.ui.theme.alert
import com.budoxr.manifestations.ui.theme.bright
import com.budoxr.manifestations.ui.theme.gray
import com.budoxr.manifestations.ui.theme.grayLight
import com.budoxr.manifestations.ui.theme.orange
import com.budoxr.manifestations.ui.theme.passion
import java.util.Date
import kotlin.math.roundToInt


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalDraggableJournalItemList(
    manifestation: ManifestationEntity,
    item: JournalEntity,
    lessons: List<LessonModel>,
    day: Long,
    categoryColor: (String, Context) -> Color,
    onItemDeleteClick: (JournalEntity) -> Unit,
    /** use the hashCode of the item object */
    onLongPress: onIntType,
    modifier: Modifier = Modifier,
) {
    val iconSize = dimensionResource(id = R.dimen.icon_medium_size)
    val lineSpacing = dimensionResource(id = R.dimen.line_spacing_1)
    val marginHorizontal = dimensionResource(id = R.dimen.margin_horizontal)
    val separator = 2.dp
    val questions  by remember { mutableStateOf(listOf<String>()) }

    val context = LocalContext.current

    val density = LocalDensity.current
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()

    val state = remember {
        AnchoredDraggableState(
            initialValue = DragAnchors.Start,
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            snapAnimationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            ),
            decayAnimationSpec = decayAnimationSpec
        )
    }

    var contentSize by remember { mutableStateOf( IntSize(0, 0)) }
    var contentWidthSizePx = 0
    val factor = 0.95f
    val itemHeightDp  = 150.dp

    Box(
        modifier = Modifier
            .wrapContentSize()
            .onSizeChanged { layoutSize ->
                contentSize = layoutSize
                contentWidthSizePx =
                    with(density) { (contentSize.width.toDp() * factor).roundToPx() }
                val dragEndPoint = layoutSize.width - contentWidthSizePx
                state.updateAnchors(
                    DraggableAnchors {
                        DragAnchors
                            .entries
                            .forEach { anchor ->
                                anchor at dragEndPoint * anchor.fraction
                            }
                    }
                )
            }
    ) {
        Box (modifier = Modifier
            .fillMaxWidth(),
            contentAlignment =  Alignment.TopEnd
        ) {
            Box (modifier = Modifier
                .size(
                    width = with(density) { contentSize.width.toDp()/2 },
                    height = itemHeightDp
//                    height = with(density) { contentSize.height.toDp() }
                )
                .padding(end = 12.dp)
                .background(alert),
                contentAlignment = Alignment.CenterEnd
            ) {
                Box (modifier = Modifier
                ) {
                    Row  {
                        Column ( modifier = Modifier.padding(end = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            IconButton(
                                onClick = {  onItemDeleteClick.invoke( item)  }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    tint = bright,
                                    modifier = Modifier
                                        .size(iconSize),
                                    contentDescription = stringResource(id = R.string.content_description_icon),
                                )

                            }
                            Text(
                                text = stringResource(id = R.string.label_icon_delete),
                                style = MaterialTheme.typography.bodySmall,
                                color = bright
                            )
                        }
                    }
                }
            }

        }

        // Draggable Item
        Box(
            modifier = Modifier
                .size(
                    width = with(density) { contentSize.width.toDp() },
                    height = with(density) { contentSize.height.toDp() })
                .offset {
                    IntOffset(
                        x = state
                            .requireOffset()
                            .roundToInt(),
                        y = 0,
                    )
                }
                .anchoredDraggable(state, Orientation.Horizontal),
        ) {
            Card(
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, gray),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                modifier = modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                onLongPress.invoke(item.hashCode())
                            }
                        )
                    }
                    .size(
                        width = with(density) { contentSize.width.toDp() * factor },
                        with(density) { contentSize.height.toDp() })

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
                                        .background( if(day >= 15) grayLight else if(day < 15 && day >= 5) orange else if(day < 5 && day >= 1) Color.Magenta else passion )
                                ) {
                                    Text( modifier = Modifier.padding(horizontal = lineSpacing, vertical = 3.dp),
                                        text = day.toString(), style = MaterialTheme.typography.bodyMedium,
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
                                    .background(categoryColor(manifestation.category, context)),
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
                    val journalQuestions = lessons.find { it.day == day.toInt() }!!.journal
                    repeat(journalQuestions.size) { idx ->
                        HorizontalDraggableJournalQuestionList(
                            number = (idx + 1).toString(),
                            question = journalQuestions[idx],
                            answer = item.answer
                        )
                    }

                }

            }

        }

    }

}


@Composable
fun HorizontalDraggableJournalQuestionList(
    number: String,
    question: String,
    answer: String?,
) {
    val separator = 2.dp

    Text(
        text = "$number - $question",
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier
    )
    Text(
        text = answer ?: "",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier
    )
}


@Preview(showBackground = true)
@Composable
fun DraggableJournalItemPreview() {
    val manifestationEntity = ManifestationEntity(
        id = 2,
        overview = "Facturacion mensual de USD 250K",
        description = "Estoy muy feliz y agradecido haber manifestado antes del 7 de Mayo del 2025, una facturacion mensual de ingresos por USD 250K.",
        creationDate = Date().toFechaTimeDb(),
        dueDate = Date().toFechaTimeDb(),
        category = CATEGORIES.WEALTH.key,
    )
    val lessonEntity = LessonEntity(
        id = null,
        day = 1,
        subject = "Gain money",
        manifestationId = 2
    )
    val journal = JournalEntity (
        id = null,
        lessonId = 5,
        questionIdx = 2,
        questionSlug = "como te va?",
        answer = "La respuesta es simple",
        responseDate = Date().toFechaTimeDb()
    )
    val item = LessonWithJournals()
        .apply {
            lesson = lessonEntity
            _journals = listOf(journal)
        }
    val lessons : List<LessonModel> = listOf(
        LessonModel(
            day = 1,
            subject = "asunto",
            summary = listOf("resumen"),
            journal = listOf("como te va?"),
            meditation = null
        )
    )

    ManifestationsTheme {
        Surface (modifier = Modifier.fillMaxWidth()) {
            HorizontalDraggableJournalItemList(
                manifestation = manifestationEntity,
                item = journal,
                lessons = lessons,
                day = 4,
                categoryColor = { category, context -> passion },
                onItemDeleteClick = { _ -> },
                onLongPress = { _ -> },
                modifier = Modifier
            )

        }
    }
}
