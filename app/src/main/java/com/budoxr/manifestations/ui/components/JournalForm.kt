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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.budoxr.manifestations.commons.CommonValues.oneDayMillis
import com.budoxr.manifestations.commons.fromFechaTimeDb
import com.budoxr.manifestations.commons.toFechaTimeDb
import com.budoxr.manifestations.data.database.entities.JournalEntity
import com.budoxr.manifestations.data.database.entities.relations.ManifestationWithJournals
import com.budoxr.manifestations.presentation.domain.LessonModel
import com.budoxr.manifestations.presentation.domain.LessonsWrapper
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import java.util.Date


@Composable
fun JournalForm(
    manifestationMenuItems: Array<String>,
    lessonDays: Array<String>,
    manifestations: List<ManifestationModel>,
    lessons: LessonsWrapper,
    item: ManifestationWithJournals,
    saveJournal: (JournalEntity) -> Unit,
    modifier: Modifier
) {
    Log.i(TAG, "compose / re-compose")

    val focusManager: FocusManager = LocalFocusManager.current
    val context = LocalContext.current
    val lineSpacing = dimensionResource(R.dimen.line_spacing_1)

    var manifestation = remember { mutableStateOf(TextFieldValue(manifestationMenuItems.first())) }
    var lessonDay = remember { mutableStateOf(TextFieldValue("")) }
    var answers = remember { mutableStateOf(List(4) { TextFieldValue("") }) }

    Column (modifier = modifier.fillMaxWidth()
        .verticalScroll(rememberScrollState())
    ) {
        ComboBox(
            items = manifestationMenuItems,
            label = stringResource(R.string.label_manifestation),
            field = manifestation,
            omitLabel = false,
            modifier = Modifier
        )

        ComboBox(
            items = lessonDays,
            label = stringResource(R.string.label_lesson),
            field = lessonDay,
            omitLabel = false,
            modifier = Modifier
        )

        JournalFormSubject(
            lessonDay = if (lessonDay.value.text.isEmpty()) null else  lessonDay.value.text.toInt()-1,
            lessons = lessons.lessons,
            focusManager = focusManager
        )

        if (lessonDay.value.text.isNotEmpty()) {
            val adjustedDay = lessonDay.value.text.toInt() - 1
            repeat(lessons.lessons[adjustedDay].journal.size) { idx ->
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
                    // save every time the compose /re-compose is called
                    saveJournal.invoke(
                        JournalEntity(
                            id = null,
                            lessonDay = lessonDay.value.text.toInt(),
                            manifestationId = it.id!!,
                            question = idx,
                            answer = if (answers.value[idx].text.isEmpty()) null else answers.value[idx].text,
                            responseDate = Date().toFechaTimeDb()
                        )
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
    focusManager: FocusManager,
) {
    val lineSpacing = dimensionResource(R.dimen.line_spacing_1)

    val value: String = lessonDay?.let { lessons[it].subject } ?: run { "" }

    OutlinedTextField(
        readOnly = true,
        value = value,
        onValueChange = { },
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