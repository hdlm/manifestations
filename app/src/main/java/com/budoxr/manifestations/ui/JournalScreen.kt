package com.budoxr.Exercises.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NoSim
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.budoxr.manifestations.R
import com.budoxr.manifestations.commons.onBooleanType
import com.budoxr.manifestations.commons.onDismissType
import com.budoxr.manifestations.commons.onIntType
import com.budoxr.manifestations.commons.toFechaTimeDb
import com.budoxr.manifestations.data.database.entities.JournalEntity
import com.budoxr.manifestations.data.database.entities.relations.LessonWithJournals
import com.budoxr.manifestations.data.database.entities.relations.ManifestationWithLessonsAndJournals
import com.budoxr.manifestations.data.mapper.emptyJournalEntity
import com.budoxr.manifestations.data.mapper.emptyManifestationModel
import com.budoxr.manifestations.data.mapper.toEntity
import com.budoxr.manifestations.data.repositories.LocalPref
import com.budoxr.manifestations.presentation.domain.LessonsWrapper
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import com.budoxr.manifestations.presentation.presenters.JournalScreenUiState
import com.budoxr.manifestations.presentation.presenters.JournalViewModel
import com.budoxr.manifestations.ui.components.ComboBox
import com.budoxr.manifestations.ui.components.HorizontalDraggableJournalItemList
import com.budoxr.manifestations.ui.components.InfoDialog
import com.budoxr.manifestations.ui.components.JournalForm
import com.budoxr.manifestations.ui.navigation.Screens
import com.budoxr.manifestations.ui.theme.ManifestationsTheme
import org.koin.androidx.compose.koinViewModel
import java.util.Date


data class JournalState(
    val manifestationId: Int,
    val manifestationSlug: String?,
    val manifestationMenuItems: Array<String>,
    val lessonDayItems: Array<String>,
    val manifestations: List<ManifestationModel>,
    val lessons: LessonsWrapper,
    val categoryColor: (String, Context) -> Color,
    val onItemDeleteClick: (JournalEntity) -> Unit,
    val dateDifference: (String, String) -> Long,
    val onFetchManifestationWithLessons: (Int) -> Unit,
    val onFetchJournal: (Int) -> Unit,
    /** using the hash code */
    val onLongPress: onIntType,
)

@Composable
fun JournalScreen(
    navController: NavController,
    page: Int,
    id: Int,
    lessonDay: Int,
    innerPadding: PaddingValues,
    onEditMode: onIntType,
    viewModel: JournalViewModel = koinViewModel()

) {
    Log.i(TAG, "compose / recompose")

    val lessons by viewModel.manifestationWithLessons.collectAsStateWithLifecycle()
    val journals by viewModel.journals.collectAsStateWithLifecycle()

    val journalScreenUiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val uiState = journalScreenUiState) {
        is JournalScreenUiState.Loading -> {
            LocalPref.saveSession(
                viewModel.session.apply {
                    currentScreen = Screens.JournalScreen.route
                    manifestation = id
                }
            )
            JournalScreenLoading(innerPadding = innerPadding)
        }
        is JournalScreenUiState.Error -> {
            JournalScreenError(
                innerPadding = innerPadding,
                msg = uiState.errorMessage!!,
                onRetry = {
                    viewModel.errorShowed = true
                    viewModel.refresh(force = true)
                }
            )
        }
        is JournalScreenUiState.Ready -> {
            JournalScreenReady(
                page = page,
                id = id.toInt(),
                innerPadding = innerPadding,
                lessons = lessons,
                journals = journals,
                navController = navController,
                uiState = uiState,
                onEditMode = onEditMode,
                viewModel = viewModel
            )
        }
    }

}

@Composable
fun JournalScreenLoading(modifier: Modifier = Modifier,
                               innerPadding: PaddingValues
) {

    val iconSize = dimensionResource(id = R.dimen.icon_huge_size)
    val areaSize = 94.dp

    Surface(modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
        Box {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(areaSize)
                    .align(Alignment.Center),
                strokeWidth = 8.dp,
                color = MaterialTheme.colorScheme.primary
            )

            Image( modifier = Modifier
                .align(Alignment.Center)
                .clip(CircleShape)
                .size(iconSize),
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = stringResource(id = R.string.content_description_logo),
                contentScale = ContentScale.Fit,
            )
        }
    }
}


@Composable
fun JournalScreenError(innerPadding: PaddingValues, msg: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.padding(innerPadding)) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            Text(
                text = stringResource(id = R.string.msg_an_error_has_ocurred),
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text =  msg,
                modifier = Modifier.padding(16.dp)
            )
            Button(onClick = onRetry) {
                Text(text = stringResource(id = R.string.label_retry))
            }
        }
    }

}

@Composable
fun JournalScreenReady(
    page: Int,
    id: Int,
    innerPadding: PaddingValues,
    lessons: List<ManifestationWithLessonsAndJournals>,
    journals: List<LessonWithJournals>,
    navController: NavController,
    uiState: JournalScreenUiState.Ready,
    onEditMode: onIntType,
    viewModel: JournalViewModel) {

    val horizontalMargin = dimensionResource(id = R.dimen.margin_horizontal)

    val coroutineScope = rememberCoroutineScope()
    var searchPattern by remember { mutableStateOf("") }
    var selectedItem by remember { mutableStateOf(emptyJournalEntity()) }
    var page by remember { mutableStateOf(page) }
    var showDialogForDelete by remember { mutableStateOf(false) }
    var showDialogForManifestationEmpty by remember { mutableStateOf(false) }

    val onLongPress: onIntType = { hashCode ->
        Log.d(TAG, "onLongPress() -> invoked, id: $hashCode")
        onEditMode.invoke(hashCode)
    }
    val onBackButtonClick: onDismissType = {
        val firstPop = navController.popBackStack()
        Log.d(TAG, "onBackButtonClick() -> clicked\n\treturned first pop: $firstPop")
    }
    val onItemDeleteClick: (JournalEntity) -> Unit = { journal ->
        Log.d(TAG, "onItemDeleteClick() -> invoked, journal id: ${journal}")
        selectedItem = journal
        showDialogForDelete = true
    }
    val onButtonConfirmationDelete: onBooleanType = { confirm ->
        Log.d(TAG, "onButtonJournalConfirmationDelete() -> invoked: $confirm, id: ${selectedItem} ")
        if (confirm) {
            viewModel.deleteJournal(selectedItem)
        }
        showDialogForDelete = false
        selectedItem = emptyJournalEntity()
    }
    val onButtonManifestationEmpty: onDismissType = {
        Log.d(TAG, "onButtonJournalManifestationEmpty() -> invoked")
        showDialogForManifestationEmpty = false
        onBackButtonClick.invoke()
    }


    val journalState = JournalState(
        manifestationId = id,
        manifestationSlug = uiState.manifestations.find { it.id == id }?.overview,
        manifestationMenuItems =  viewModel.util.transformList(uiState.manifestations) { it.overview }.toTypedArray(),
        lessonDayItems = viewModel.util.transformList(uiState.lessons.lessons) { it.day.toString() }.toTypedArray(),
        manifestations = uiState.manifestations,
        lessons = uiState.lessons,
        categoryColor = viewModel::categoryColor,
        onItemDeleteClick = onItemDeleteClick,
        dateDifference = viewModel::dateDifference,
        onFetchManifestationWithLessons = viewModel::collectManifestationWithLessons,
        onFetchJournal = viewModel::collectJournals,
        onLongPress = onLongPress,
    )

    Surface(modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
        when (page) {
            0 -> {
                JournalScreenBody(
                    journalState = journalState,
                    journals = journals //journals.distinctBy { it.lesson.id }
                )
            }
            1 -> { // add new Journal
//                LaunchedEffect(Unit) {
//                    Log.d(TAG, "LaunchedEffect running the coroutine")
//                    coroutineScope.launch {
//                        nextId = viewModel.util.performAsyncOperation(scope = this, timeout = CommonValues.WAIT_DEFERRED, timeUnit = TimeUnit.SECONDS, dispatcher = Dispatchers.IO) {
//                            viewModel.lastId()
//                        }.await()
//                        nextId++
//                    }
//                }



                if (journalState.manifestationMenuItems.isNotEmpty()) {
                    JournalForm(
                        manifestationId = journalState.manifestationId,
                        manifestationSlug = journalState.manifestationSlug!!,
                        lessonDay = 0,
                        manifestationMenuItems = journalState.manifestationMenuItems,
                        lessonDayItems = journalState.lessonDayItems,
                        manifestations = journalState.manifestations,
                        lessons = journalState.lessons,
                        item = ManifestationWithLessonsAndJournals(),
                        saveLesson = viewModel::saveLesson,
                        saveJournal = viewModel::saveJournal,
                        modifier = Modifier
                    )
//                    JournalForm(
//                        manifestationMenuItems = journalState.manifestationMenuItems,
//                        lessonDayItems = journalState.lessonDayItems,
//                        manifestations = journalState.manifestations,
//                        lessons = journalState.lessons,
//                        item = ManifestationWithJournals().apply {
//                            _journals = listOf( emptyJournalEntity() )
//                            manifestation = emptyManifestationModel().toEntity()
//                        },
//                        saveLesson = viewModel::saveLesson,
//                        saveJournal = viewModel::saveJournal,
//                        modifier = Modifier.padding(horizontal = horizontalMargin)
//                    )
                } else {
                    showDialogForManifestationEmpty = true
                }

            }
        }

        if ( showDialogForManifestationEmpty ) {
            InfoDialog(
                modifier = Modifier,
                title = stringResource(id = R.string.title_manifestation_empty),
                msg = stringResource(id = R.string.msg_manifestation_empty),
                buttonLabel = stringResource(id = R.string.button_validation_confirm ),
                onDone = onButtonManifestationEmpty
            )
        }
    }
    
    
}

@Composable
fun JournalScreenBody(
    journalState: JournalState,
    journals: List<LessonWithJournals>
) {
    val iconSize = dimensionResource(id = R.dimen.icon_big_size)
    val marginHorizontal = dimensionResource(id = R.dimen.margin_horizontal)
    val lineSpacing = dimensionResource(id = R.dimen.line_spacing_1)

    val manifestation = remember { mutableStateOf(TextFieldValue(journalState.manifestationMenuItems.find { it == journalState.manifestationSlug }!! )) }
    val day = remember { mutableStateOf(TextFieldValue(journalState.lessonDayItems.first())) }

    val manifestationId by remember { mutableStateOf(journalState.manifestations.find { it.overview == manifestation.value.text }!!.id) }
    val dayId by remember { mutableStateOf(journalState.lessons.lessons.find { it.day == day.value.text.toInt() }!!.day) }
//    journalState.onFetchJournal.invoke(manifestationId!!, dayId)

    LazyColumn(modifier = Modifier.padding(end = marginHorizontal)) {
        item {
            ComboBox(
                enabled = false,
                items = journalState.manifestationMenuItems,
                label = stringResource(R.string.label_manifestation),
                field = manifestation,
                omitLabel = false,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.padding(vertical = lineSpacing))
//            ComboBox(
//                items = journalState.lessonDayItems,
//                label = stringResource(R.string.label_lesson),
//                field = day,
//                omitLabel = false,
//                modifier = Modifier
//            )
        }

        if (journals.isNotEmpty()) {
            //TODO desplegar los journals aqui
//            items(journals.first()._journals) { item ->
//                HorizontalDraggableJournalItemList(
//                    manifestation = journals.first().manifestation,
//                    item = item,
//                    lessons = journalState.lessons.lessons,
//                    day = journalState.dateDifference.invoke(Date().toFechaTimeDb(),
//                        journalState.manifestations.find { it.id == manifestationId }!!.dueDate),
//                    categoryColor = journalState.categoryColor,
//                    onItemDeleteClick = journalState.onItemDeleteClick,
//                    onLongPress = journalState.onLongPress,
//                )
//                Spacer(modifier = Modifier.padding(vertical = lineSpacing))
//
//            }
        } else {
            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.label_no_records),
                            style = MaterialTheme.typography.displaySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                        )
                        Spacer(modifier = Modifier.padding(vertical = lineSpacing))
                        Icon(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(iconSize),
                            imageVector = Icons.Outlined.NoSim,
                            contentDescription = stringResource(id = R.string.content_description_icon)
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(vertical = lineSpacing))
            }
        }
    }

    
}
        
        



@Composable
@Preview(showBackground = true)
fun JournalScreenPreview() {

    ManifestationsTheme {

        Surface (modifier = Modifier.fillMaxSize()) {
            //TODO add here the composable function
        }

    }

}

private const val TAG =  "che.JournalScreen"