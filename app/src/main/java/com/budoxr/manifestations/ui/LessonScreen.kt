package com.budoxr.manifestations.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.budoxr.manifestations.R
import com.budoxr.manifestations.commons.CommonValues
import com.budoxr.manifestations.commons.onBooleanType
import com.budoxr.manifestations.commons.onDismissType
import com.budoxr.manifestations.commons.onIntType
import com.budoxr.manifestations.commons.onStringType
import com.budoxr.manifestations.data.mapper.emptyLessonModel
import com.budoxr.manifestations.data.repositories.LocalPref
import com.budoxr.manifestations.presentation.domain.LessonModel
import com.budoxr.manifestations.presentation.domain.LessonsWrapper
import com.budoxr.manifestations.presentation.presenters.LessonScreenUiState
import com.budoxr.manifestations.presentation.presenters.LessonViewModel
import com.budoxr.manifestations.ui.components.CustomSearchView
import com.budoxr.manifestations.ui.navigation.Screens
import com.budoxr.manifestations.ui.theme.ManifestationsTheme
import com.budoxr.manifestations.ui.theme.grayLight
import org.koin.androidx.compose.koinViewModel

data class LessonState(
    val isDarkTheme: Boolean = false,
    val lessons: LessonsWrapper,
    val selectedDay: Int,
    val statusPlayer: CommonValues.STATUS_PLAYER,
    val onBackButtonClick: onDismissType,
    val onSearchApply: onStringType,
    val onItemClick: onIntType,
    val onStatusPlayerClick: onIntType
)

@Composable
fun LessonScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    innerPadding: PaddingValues,
    viewModel: LessonViewModel = koinViewModel()
) {

    val lifecycleOwner = LocalLifecycleOwner.current

    val observer = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_START -> {
                Log.i(TAG, "compose / recompose")
                viewModel.restartSpeak()
            }
            Lifecycle.Event.ON_DESTROY -> {
                Log.d(TAG, "Composable destroyed")
                viewModel.textToSpeech.shutdown()
            }
            else -> {
                // Other lifecycle events can be handled here as needed
            }
        }
    }
    lifecycleOwner.lifecycle.addObserver(observer)

    val lessonScreenUiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val uiState = lessonScreenUiState) {
        is LessonScreenUiState.Loading -> {
            if (viewModel.session.currentScreen != Screens.LessonScreen.route) {
                LocalPref.saveSession(
                    viewModel.session.apply {
                        currentScreen = Screens.LessonScreen.route
                    }
                )
                LessonScreenLoading(innerPadding = innerPadding)
            }
        }
        is LessonScreenUiState.Error -> {
            LessonScreenError(
                innerPadding = innerPadding,
                msg = uiState.errorMessage!!,
                onRetry = {
                    viewModel.errorShowed = false
                    viewModel.refresh(true)
                }
            )
        }
        is LessonScreenUiState.Ready -> {
            LessonScreenReady(
                innerPadding = innerPadding,
                navController = navController,
                uiState = uiState,
                viewModel = viewModel,
                isDarkTheme = isDarkTheme,
            )
        }
    }

}


@Composable
fun LessonScreenLoading(modifier: Modifier = Modifier,
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
fun LessonScreenError(innerPadding: PaddingValues, msg: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
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
fun LessonScreenReady(
    innerPadding: PaddingValues,
    navController: NavController,
    uiState: LessonScreenUiState.Ready,
    viewModel: LessonViewModel,
    isDarkTheme: Boolean,
) {

    var searchPattern by remember { mutableStateOf("") }
    var selectedDay by remember { mutableStateOf(0) }
    var showLessonDetails by remember { mutableStateOf(false) }
    var statusPlayer by remember { mutableStateOf(CommonValues.STATUS_PLAYER.pause) }


    val onBackButtonClick: onDismissType = {
        val value = navController.popBackStack()
        Log.d(TAG, "onBackButtonClick() -> clicked\n\treturned value: $value")
    }
    val onSearchApply: onStringType = { pattern ->
        Log.d(TAG, "onSearchApply() -> invoked, pattern: $pattern")
        searchPattern = pattern
    }
    val onItemClick: onIntType = { day ->
        if (statusPlayer == CommonValues.STATUS_PLAYER.playing) {
            Log.d(TAG, "onItemClick() -> invoked, cannot selected while is Playing")
        } else {
            Log.d(TAG, "onItemClick() -> invoked, it was playing, stop the speak, and select day: $day")
            selectedDay = day
            showLessonDetails = true
            viewModel.stopSpeak()
        }
    }
    val onDoneParagraph : onDismissType = {
        if (statusPlayer == CommonValues.STATUS_PLAYER.playing) {
            val newCount = viewModel.meditationContent.paragraphCount++
            if (newCount >= viewModel.meditationContent.paragraphs.size) {
                Log.i(TAG, "onDoneParagraph() -> invoked, Meditation finished")
                statusPlayer = CommonValues.STATUS_PLAYER.stop
            } else {
                Log.d(TAG, "onDoneParagraph() -> invoked, next paragraph: $newCount")
                viewModel.speak(paragraphIndex = newCount)
            }
        }
    }
    val onErrorTTS: onStringType = { errorMessage ->
        Log.e(TAG, "onErrorTTS() -> invoked")
        viewModel.error(errorMessage)
    }
    val onStatusPlayerClick: onIntType = { status ->
        val statusName = CommonValues.STATUS_PLAYER.entries.toTypedArray()[status].name
        Log.d(TAG, "onStatusPlayerClick() -> invoked, status: $statusName")

        statusPlayer = CommonValues.STATUS_PLAYER.entries.toTypedArray()[status]
        when (statusPlayer) {
            CommonValues.STATUS_PLAYER.stop -> {
                viewModel.meditationContent.paragraphCount = 1
            }
            CommonValues.STATUS_PLAYER.rewind -> {
                with(viewModel.meditationContent) {
                    paragraphCount -= 2
                    if (paragraphCount <= 0 ) paragraphCount = 0
                }
            }
            CommonValues.STATUS_PLAYER.forward -> {
                with(viewModel.meditationContent) {
                    paragraphCount += 1
                    if (paragraphCount >= paragraphs.size) paragraphCount = paragraphs.size - 1
                }
            }
            CommonValues.STATUS_PLAYER.playing -> {
                with(viewModel.meditationContent) {
                    if (paragraphCount == 0) {
                        // start playing
                        val fileName = uiState.lessons.lessons[selectedDay-1].meditation!!
                        viewModel.loadMeditation(fileName)
                        viewModel.textToSpeech.onDone = onDoneParagraph
                        viewModel.textToSpeech.onError = onErrorTTS
                    }
                    with(viewModel.meditationContent) {
                        Log.i(TAG, "speak now")
                        paragraphCount++
                        Log.d(TAG, "\t> paragraph: $paragraphCount")
                        viewModel.speak(paragraphCount)
                        paragraphCount++  // fix the issue that repeat two times the first paragraph when start playing
                    }

                }
            }
            CommonValues.STATUS_PLAYER.pause -> {
                if ( viewModel.meditationContent.paragraphCount > 0 ) {
                    viewModel.meditationContent.paragraphCount--  // fix the issue that skip the next paragraph when pause
                    Log.d(TAG, "\t> paragraph: ${viewModel.meditationContent.paragraphCount}")
                }

            }
            CommonValues.STATUS_PLAYER.previous -> {
                Log.d(TAG, "\t> paragraph: ${viewModel.meditationContent.paragraphCount}")

            }
        }
    }

    val lessonState = LessonState(
        isDarkTheme = isDarkTheme,
        lessons = uiState.lessons,
        selectedDay = selectedDay,
        statusPlayer = statusPlayer,
        onBackButtonClick = onBackButtonClick,
        onSearchApply = onSearchApply,
        onItemClick = onItemClick,
        onStatusPlayerClick = onStatusPlayerClick,
    )

    Surface(modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
        LessonScreenBody(
            isDarkTheme = isDarkTheme,
            showLessonDetails = showLessonDetails,
            lessonState = lessonState,
        )
    }

}

@Composable
fun LessonScreenBody(
    isDarkTheme: Boolean,
    showLessonDetails: Boolean,
    lessonState: LessonState,
) {
    var search by remember { mutableStateOf("") }
    val lineSpacing = dimensionResource(id = R.dimen.line_spacing_1)
    val marginHorizontal = dimensionResource(id = R.dimen.margin_horizontal)

    LazyColumn {
        item {
            CustomSearchView(
                modifier = Modifier,
                search = search,
                onValueChange =  {
                    search = it
                    lessonState.onSearchApply.invoke(search)
                }
            )
        }

        items(lessonState.lessons.lessons) { item ->

            if (showLessonDetails && item.day == lessonState.selectedDay) {
                LessonListItemSelected(
                    item = item,
                    isDarkTheme = isDarkTheme,
                    selectedDay = lessonState.selectedDay,
                    statusPlayer = lessonState.statusPlayer,
                    onItemClick = lessonState.onStatusPlayerClick,
                    modifier = Modifier.padding(vertical = lineSpacing, horizontal = marginHorizontal)
                )
            } else {
                LessonListItemNotSelected(
                    item = item,
                    isDarkTheme = isDarkTheme,
                    statusPlayer = lessonState.statusPlayer,
                    onItemClick = lessonState.onItemClick,
                    modifier = Modifier.padding(vertical = lineSpacing, horizontal = marginHorizontal)
                )
            }

            HorizontalDivider(modifier = Modifier,
                thickness = 1.dp,
                color = grayLight
            )

        }


        item {
            Spacer(modifier = Modifier.height(96.dp))
        }

    }

}


@Composable
fun LessonListItemNotSelected(
    item: LessonModel,
    isDarkTheme: Boolean,
    statusPlayer: CommonValues.STATUS_PLAYER,
    onItemClick: onIntType,
    modifier: Modifier
) {
    val marginHorizontal = dimensionResource(id = R.dimen.margin_horizontal)
    val lineSpacing = dimensionResource(id = R.dimen.line_spacing_1)
    val iconSize = dimensionResource(id = R.dimen.icon_tiny_size)

    Card(
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .clickable {
                    if (statusPlayer != CommonValues.STATUS_PLAYER.playing) {
                        onItemClick.invoke(item.day)
                    }
                }
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(0.2f)
            ) {
                Box {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(grayLight)
                    ) {
                        Text(
                            text = item.day.toString(), style = MaterialTheme.typography.labelMedium
                        )
                    }

                    item.meditation?.let {
                        Icon(
                            modifier = Modifier
                                .padding(start = 25.dp, top = 20.dp)
                                .size(iconSize),
                            imageVector = Icons.Filled.MusicNote,
                            contentDescription = stringResource(id = R.string.content_description_icon)
                        )
                    }

                }

            }
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(text = item.subject, style = MaterialTheme.typography.bodyMedium)
            }

        }
    }
}

@Composable
fun LessonListItemSelected(
    item: LessonModel,
    isDarkTheme: Boolean,
    selectedDay: Int,
    statusPlayer: CommonValues.STATUS_PLAYER,
    onItemClick: onIntType,
    modifier: Modifier
) {
    val marginHorizontal = dimensionResource(id = R.dimen.margin_horizontal)
    val lineSpacing = dimensionResource(id = R.dimen.line_spacing_1)
    val iconTinySize = dimensionResource(id = R.dimen.icon_tiny_size)
    val iconSize = dimensionResource(id = R.dimen.icon_large_size)

    Card(
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = modifier
    ) {
        Column {
            Row( modifier = Modifier
                .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column( modifier = Modifier
                    .weight(0.2f)
                ) {
                    Box {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(grayLight)
                        ) {
                            Text(
                                text = item.day.toString(), style = MaterialTheme.typography.titleSmall
                            )
                        }

                        item.meditation?.let {
                            Icon(
                                modifier = Modifier
                                    .padding(start = 25.dp, top = 20.dp)
                                    .size(iconTinySize),
                                imageVector = Icons.Filled.MusicNote,
                                contentDescription = stringResource(id = R.string.content_description_icon)
                            )
                        }

                    }

                }
                Column( modifier = Modifier
                    .weight(1f)
                ) {
                    Text(text = item.subject, style = MaterialTheme.typography.titleSmall )
                }

            }

            Row( modifier = Modifier
                .padding(top = lineSpacing)
                .fillMaxWidth(),
            ) {
                Column( modifier = Modifier
                    .weight(1f)
                ) {
                    item.summary.forEach { summary ->
                        Text(
                            text = "> $summary",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }

            item.meditation?.let {
                Row( modifier = Modifier
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column( modifier = Modifier.fillMaxHeight())  {
                        IconButton(
                            onClick = {
                                onItemClick.invoke(CommonValues.STATUS_PLAYER.previous.ordinal)
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(iconSize),
                                imageVector = Icons.Filled.SkipPrevious,
                                contentDescription = stringResource(id = R.string.content_description_icon)
                            )
                        }
                    }
                    Column {
                        IconButton(
                            onClick = {
                                onItemClick.invoke(CommonValues.STATUS_PLAYER.rewind.ordinal)
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(iconSize),
                                imageVector = Icons.Filled.FastRewind,
                                contentDescription = stringResource(id = R.string.content_description_icon)
                            )
                        }

                    }
                    Column {
                        IconButton(
                            onClick = {
                                if ( statusPlayer == CommonValues.STATUS_PLAYER.pause || statusPlayer == CommonValues.STATUS_PLAYER.playing) {
                                    val status = if (statusPlayer == CommonValues.STATUS_PLAYER.pause) CommonValues.STATUS_PLAYER.playing.ordinal else CommonValues.STATUS_PLAYER.pause.ordinal
                                    onItemClick.invoke( status )
                                } else {
                                    val status = CommonValues.STATUS_PLAYER.playing.ordinal
                                    onItemClick.invoke( status )
                                }
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(iconSize),
                                imageVector = if (statusPlayer == CommonValues.STATUS_PLAYER.playing) Icons.Filled.PauseCircle else Icons.Filled.PlayCircle,
                                contentDescription = stringResource(id = R.string.content_description_icon)
                            )
                        }
                    }
                    Column {
                        IconButton(
                            onClick = {
                                onItemClick.invoke(CommonValues.STATUS_PLAYER.rewind.ordinal)
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(iconSize),
                                imageVector = Icons.Filled.FastForward,
                                contentDescription = stringResource(id = R.string.content_description_icon)
                            )
                        }
                    }

                }
            }

        }


    }

}

@Preview(showBackground = false)
@Composable
fun LessonScreenPreview() {
    val onSearchApply: onStringType = { }
    val onItemClick: onIntType = { }
    val lessons = listOf(
        emptyLessonModel().copy(
            day = 1,
            subject = "The 80/20 Trap",
            meditation = "meditation_file.txt"
        ),
        emptyLessonModel().copy(
            day = 2,
            subject = "The Power of 2%. Take Control of Your Inner Reality",
            summary = listOf("Summary 1", "Summary 2")
        ),
        emptyLessonModel().copy(
            day = 3,
            subject = "Tap Into Surrendered Manifestation",
        ),
        emptyLessonModel().copy(
            day = 25,
            subject = "Allow Yourself to Dream Again",
            meditation = "meditation_file.txt"
        ),
    )

    ManifestationsTheme {
        val lessonState = LessonState(
            isDarkTheme = false,
            lessons = LessonsWrapper(lessons),
            selectedDay = 2,
            statusPlayer = CommonValues.STATUS_PLAYER.pause,
            onBackButtonClick = { },
            onSearchApply = onSearchApply,
            onItemClick = onItemClick,
            onStatusPlayerClick = onItemClick,
        )

        Surface(modifier = Modifier
            .fillMaxSize()
        ) {
            LessonScreenBody(
                isDarkTheme = false,
                showLessonDetails = true,
                lessonState = lessonState,
            )
        }


    }

}


private const val TAG =  "che.LessonScreen"
