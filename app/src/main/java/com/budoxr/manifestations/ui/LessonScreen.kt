package com.budoxr.manifestations.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.budoxr.manifestations.R
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
    val onBackButtonClick: onDismissType,
    val onSearchApply: onStringType,
    val onItemClick: onIntType,
)

@Composable
fun LessonScreen(
    navController: NavController,
    innerPadding: PaddingValues,
    isDarkTheme: Boolean,
    viewModel: LessonViewModel = koinViewModel()
) {
    Log.i(TAG, "compose / recompose")

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
                    viewModel.errorShowed = true
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

    val onBackButtonClick: onDismissType = {
        val value = navController.popBackStack()
        Log.d(TAG, "onBackButtonClick() -> clicked\n\treturned value: $value")
    }
    val onSearchApply: onStringType = { pattern ->
        Log.d(TAG, "onSearchApply() -> invoked, pattern: $pattern")
        searchPattern = pattern
    }
    val onItemClick: onIntType = { day ->
        Log.d(TAG, "onItemClick() -> invoked, day: $day")
        selectedDay = day
        showLessonDetails = true
    }

    val lessonState = LessonState(
        isDarkTheme = isDarkTheme,
        lessons = uiState.lessons,
        onBackButtonClick = onBackButtonClick,
        onSearchApply = onSearchApply,
        onItemClick = onItemClick,
    )

    Surface(modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
        LessonScreenBody(
            isDarkTheme = isDarkTheme,
            lessonState = lessonState,
        )
    }

}

@Composable
fun LessonScreenBody(
    isDarkTheme: Boolean,
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

            LessonListItem(
                item = item,
                isDarkTheme = isDarkTheme,
                onItemClick = lessonState.onItemClick,
                modifier = Modifier.padding(vertical = lineSpacing, horizontal = marginHorizontal)
            )
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
fun LessonListItem(
    item: LessonModel,
    isDarkTheme: Boolean,
    onItemClick: onIntType,
    modifier: Modifier
) {
    val marginHorizontal = dimensionResource(id = R.dimen.margin_horizontal)
    val iconSize = dimensionResource(id = R.dimen.icon_tiny_size)

    Card(
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = modifier
    ) {
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

            Column( modifier = Modifier
                .weight(1f)
            ) {
                Text(text = item.subject)
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
            onBackButtonClick = { },
            onSearchApply = onSearchApply,
            onItemClick = onItemClick,
        )

        Surface(modifier = Modifier
            .fillMaxSize()
        ) {
            LessonScreenBody(
                isDarkTheme = false,
                lessonState = lessonState,
            )
        }


    }

}


private const val TAG =  "che.LessonScreen"
