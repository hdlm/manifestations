package com.budoxr.manifestations.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.budoxr.manifestations.R
import com.budoxr.manifestations.data.database.entities.JournalEntity
import com.budoxr.manifestations.data.database.entities.LessonEntity
import com.budoxr.manifestations.data.database.entities.relations.ManifestationWithLessonsAndJournals
import com.budoxr.manifestations.data.mapper.toEntity
import com.budoxr.manifestations.presentation.domain.LessonModel
import com.budoxr.manifestations.presentation.domain.LessonsWrapper
import com.budoxr.manifestations.presentation.domain.ManifestationModel


@Composable
fun JournalForm(
    manifestationId: Int,
    manifestationSlug: String,
    lessonDay: Int,
    manifestationMenuItems: Array<String>,
    lessonDayItems: Array<String>,
    manifestations: List<ManifestationModel>,
    lessons: LessonsWrapper,
    item: ManifestationWithLessonsAndJournals,
    saveJournal: (LessonEntity, JournalEntity) -> Unit,
    modifier: Modifier
) {
    Log.i(TAG, "compose / re-compose")

    val focusManager: FocusManager = LocalFocusManager.current
    val context = LocalContext.current
    val lineSpacing = dimensionResource(R.dimen.line_spacing_1)

    val manifestation = remember { mutableStateOf(TextFieldValue(manifestationMenuItems.find { it == manifestationSlug}!! )) }
    val lessonDay = remember { mutableStateOf(TextFieldValue("")) }
    val subject =  remember { mutableStateOf("") }
    val answers = remember { mutableStateOf(List(4) { TextFieldValue("") }) }

    Column (modifier = modifier.fillMaxWidth()
        .verticalScroll(rememberScrollState())
    ) {
        Label(
            label = stringResource(R.string.label_manifestation),
            value = manifestation.value.text
        )
        ComboBox(
            items = lessonDayItems,
            label = stringResource(R.string.label_lesson),
            field = lessonDay,
            omitLabel = false,
            modifier = Modifier
        )


        if (lessonDay.value.text.isNotEmpty()) {
            val adjustedDay = lessonDay.value.text.toInt() - 1
            JournalFormSubject(
                lessonDay = adjustedDay,
                lessons = lessons.lessons,
                subject = subject,
                focusManager = focusManager
            )
            repeat(lessons.lessons[adjustedDay].journal.size-1) { idx ->
                OutlinedTextField(
                    value = answers.value[idx],
                    onValueChange = { newValue ->
                        val capitalizedText = newValue.text.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase() else it.toString()
                        }
                        answers.value = answers.value.toMutableList()
                            .apply { this[idx] = newValue.copy(text = capitalizedText) }
                    },
                    label = { Text(text = lessons.lessons[adjustedDay].journal[idx]) },
                    minLines = 3,
                    maxLines = 35,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
//        colors = textFieldColors,
                    modifier = Modifier
                        .padding(vertical = lineSpacing)
                        .fillMaxWidth()
                )

                val manifestationItem =
                    manifestations.find { it.overview == manifestation.value.text }
                manifestationItem?.let {
                    if (lessonDay.value.text.isNotEmpty() && lessonDay.value.text.toInt() > 0) {

                        val journalEntity = JournalEntity(
                            id = null,
                            lessonId = lessonDay.value.text.toInt(),
                            questionIdx = idx + 1,
                            questionSlug = lessons.lessons[lessonDay.value.text.toInt()-1].journal[idx],
                            answer = answers.value[idx].text,
                            responseDate = System.currentTimeMillis()
                        )

                        lessons.lessons[lessonDay.value.text.toInt()-1].manifestationId = it.id
                       saveJournal.invoke(
                            lessons.lessons[lessonDay.value.text.toInt()-1].toEntity(),
                            journalEntity
                        )
                    }
                }

            }
        }
    }

}


@Composable
fun JournalFormSubject(
    lessonDay: Int?,
    lessons: List<LessonModel>,
    subject: MutableState<String>,
    focusManager: FocusManager,
) {
    val lineSpacing = dimensionResource(R.dimen.line_spacing_1)

    val value: String = lessonDay?.let { lessons[it].subject } ?: run { "" }

    OutlinedTextField(
        readOnly = true,
        value = value,
        onValueChange = { subject.value = value },
        label = { Text( text = stringResource(R.string.label_subject)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        ),
//        colors = textFieldColors,
        modifier = Modifier
            .padding(vertical = lineSpacing)
            .fillMaxWidth()
    )
}

private const val TAG = "che.JournalForm"