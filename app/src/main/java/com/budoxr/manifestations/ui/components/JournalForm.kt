package com.budoxr.manifestations.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.budoxr.manifestations.R
import com.budoxr.manifestations.commons.onIntType
import com.budoxr.manifestations.data.database.entities.JournalEntity
import com.budoxr.manifestations.data.database.entities.LessonEntity
import com.budoxr.manifestations.presentation.domain.LessonModel
import com.budoxr.manifestations.presentation.domain.LessonsWrapper
import com.budoxr.manifestations.presentation.domain.ManifestationModel


@Composable
fun JournalForm(
    manifestationSlug: String,
    manifestationMenuItems: Array<String>,
    lessonDayItems: Array<String>,
    manifestations: List<ManifestationModel>,
    lessons: LessonsWrapper,
    onSaveLesson: (LessonEntity, onIntType) -> Unit,
    onSaveButtonClick: (JournalEntity, Int) -> Unit,
    modifier: Modifier
) {
    Log.i(TAG, "compose / re-compose")

    val focusManager: FocusManager = LocalFocusManager.current
    val lineSpacing = dimensionResource(R.dimen.line_spacing_1)
    val lineSpacing2x = dimensionResource(R.dimen.line_spacing_2)
    val horizontalMargin = dimensionResource(R.dimen.margin_horizontal)

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
            val dayIndexSelected = lessonDay.value.text.toInt() - 1
            JournalFormSubject(
                lessonDay = dayIndexSelected,
                lessons = lessons.lessons,
                subject = subject,
                focusManager = focusManager
            )
            repeat(lessons.lessons[dayIndexSelected].journal.size-1) { idx ->
                OutlinedTextField(
                    value = answers.value[idx],
                    onValueChange = { newValue ->
                        val capitalizedText = newValue.text.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase() else it.toString()
                        }
                        answers.value = answers.value.toMutableList()
                            .apply { this[idx] = newValue.copy(text = capitalizedText) }
                    },
                    label = { Text(text = lessons.lessons[dayIndexSelected].journal[idx]) },
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

            }

            Spacer(modifier = Modifier.padding(vertical = lineSpacing2x))
            Button( onClick = {
                if (lessonDay.value.text.isNotEmpty() && lessonDay.value.text.toInt() > 0) {

                    val manifestation = manifestations.find { it.overview == manifestation.value.text }

                    val lessonEntity = LessonEntity(
                        id = null,
                        day = lessonDay.value.text.toInt(),
                        subject = lessons.lessons[dayIndexSelected].subject,
                        manifestationId = manifestation!!.id!!
                    )

                    val onDone: onIntType = { lessonId ->
                        repeat(lessons.lessons[dayIndexSelected].journal.size-1) { idx ->
                            val journalEntity = JournalEntity(
                                id = null,
                                lessonId = 0,
                                questionIdx = idx + 1,
                                questionSlug = lessons.lessons[lessonDay.value.text.toInt()-1].journal[idx],
                                answer = answers.value[idx].text,
                                responseDate = System.currentTimeMillis()
                            )
                            onSaveButtonClick.invoke( journalEntity, lessonId )
                        }
                    }
                    onSaveLesson.invoke(lessonEntity, onDone)

                }

            },
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = horizontalMargin)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = stringResource(R.string.button_save),
                        modifier = Modifier.padding(horizontal = horizontalMargin)
                    )
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