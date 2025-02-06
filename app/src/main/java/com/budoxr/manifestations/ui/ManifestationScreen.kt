package com.budoxr.manifestations.ui

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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.budoxr.manifestations.R
import com.budoxr.manifestations.commons.CATEGORIES
import com.budoxr.manifestations.commons.CommonValues
import com.budoxr.manifestations.commons.onBooleanType
import com.budoxr.manifestations.commons.onDismissType
import com.budoxr.manifestations.commons.onIntType
import com.budoxr.manifestations.commons.toFechaTimeDb
import com.budoxr.manifestations.data.mapper.copy
import com.budoxr.manifestations.data.mapper.emptyManifestationModel
import com.budoxr.manifestations.data.repositories.LocalPref
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import com.budoxr.manifestations.presentation.presenters.ManifestationScreenUiState
import com.budoxr.manifestations.presentation.presenters.ManifestationViewModel
import com.budoxr.manifestations.ui.components.HorizontalDraggableManifestationItemList
import com.budoxr.manifestations.ui.components.ManifestationForm
import com.budoxr.manifestations.ui.components.ValidationDialog
import com.budoxr.manifestations.ui.navigation.Screens
import com.budoxr.manifestations.ui.theme.ManifestationsTheme
import com.budoxr.manifestations.ui.theme.spirituality
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.Date
import java.util.concurrent.TimeUnit

data class ManifestationState(
    val isDarkTheme: Boolean,
    val manifestations: List<ManifestationModel>,
    val categoryColor: (String, Context) -> Color,
    val onItemDeleteClick: (ManifestationModel) -> Unit,
    val dateDifference: (String, String) -> Long,
    val onLongPress: onIntType,
    val onBackButtonClick: onDismissType,
    val navigateToJournals: onIntType
)

@Composable
fun ManifestationScreen(
    navController: NavController,
    page: Int,
    id: Int,
    isDarkTheme: Boolean,
    innerPadding: PaddingValues,
    onEditMode: onIntType,
    navigateToJournals: onIntType,
    viewModel: ManifestationViewModel = koinViewModel()
) {
    Log.i(TAG, "compose / recompose")

    val manifestationScreenUiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val uiState = manifestationScreenUiState) {
        is ManifestationScreenUiState.Loading -> {
            LocalPref.saveSession(
                viewModel.session.apply {
                    currentScreen = Screens.ManifestationScreen.route
                }
            )
            ManifestationScreenLoading(innerPadding = innerPadding)
        }
        is ManifestationScreenUiState.Error -> {
            ManifestationScreenError(
                innerPadding = innerPadding,
                msg = uiState.errorMessage!!,
                onRetry = {
                    viewModel.errorShowed = true
                    viewModel.refresh(force = true)
                }
            )
        }
        is ManifestationScreenUiState.Ready -> {
            val manifestations by viewModel.flowOfManifestations.collectAsStateWithLifecycle(initialValue = emptyList())
            ManifestationScreenReady(
                page = page,
                id = id,
                innerPadding = innerPadding,
                manifestations = manifestations,
                navController = navController,
                uiState = uiState,
                onEditMode = onEditMode,
                navigateToJournals = navigateToJournals,
                viewModel = viewModel,
                isDarkTheme = isDarkTheme,
            )
        }
    }

}

@Composable
fun ManifestationScreenLoading(modifier: Modifier = Modifier,
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
fun ManifestationScreenError(innerPadding: PaddingValues, msg: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
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
fun ManifestationScreenReady(
    page: Int,
    id: Int,
    innerPadding: PaddingValues,
    manifestations: List<ManifestationModel>,
    navController: NavController,
    uiState: ManifestationScreenUiState.Ready,
    onEditMode: onIntType,
    navigateToJournals: onIntType,
    viewModel: ManifestationViewModel,
    isDarkTheme: Boolean,
) {
    val horizontalMargin = dimensionResource(id = R.dimen.margin_horizontal)

    val coroutineScope = rememberCoroutineScope()
    var searchPattern by remember { mutableStateOf("") }
    var nextId by remember { mutableStateOf(0) }
    var selectedItem by remember { mutableStateOf(id) }
    var page by remember { mutableStateOf(page) }
    var showDialog by remember { mutableStateOf(false) }

    val onBackButtonClick: onDismissType = {
        val firstPop = navController.popBackStack()
        Log.d(TAG, "onBackButtonClick() -> clicked\n\treturned first pop: $firstPop")
    }
    val onLongPress: onIntType = { id ->
        Log.d(TAG, "onLongPress() -> invoked, id: $id")
        onEditMode.invoke(id)
    }
    val onItemDeleteClick: (ManifestationModel) -> Unit = { manifestation ->
        Log.d(TAG, "onItemDeleteClick() -> invoked, manifestation id: ${manifestation.id}")
        selectedItem = manifestation.id!!
        showDialog = true
    }
    val onButtonManifestationConfirmationDelete: onBooleanType = { confirm ->
        Log.d(TAG, "onButtonManifestationConfirmationDelete() -> invoked: $confirm, id: ${selectedItem} ")
        if (confirm) {
            viewModel.deleteManifestation(manifestations.filter { it.id == selectedItem }.first())
        }
        showDialog = false
        selectedItem = 0
    }

    val manifestationState = ManifestationState(
        isDarkTheme = isDarkTheme,
        manifestations = manifestations,
        categoryColor = viewModel::categoryColor,
        onItemDeleteClick = onItemDeleteClick,
        dateDifference = viewModel::dateDifference,
        onLongPress = onLongPress,
        onBackButtonClick = onBackButtonClick,
        navigateToJournals = navigateToJournals
    )

    Surface(modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {

        when (page) {
            0 -> {
                ManifestationScreenBody(
                    manifestationState = manifestationState,
                )
            }
            1 -> { // add a new manifestation
                LaunchedEffect(Unit) {
                    Log.d(TAG, "LaunchedEffect running the coroutine")
                    coroutineScope.launch {
                        nextId = viewModel.util.performAsyncOperation(scope = this, timeout = CommonValues.WAIT_DEFERRED, timeUnit = TimeUnit.SECONDS, dispatcher = Dispatchers.IO) {
                            viewModel.lastId()
                        }.await()
                        nextId++
                    }
                }

                if (nextId > 0) {
                    ManifestationForm(
                        item = emptyManifestationModel().copy(id = nextId),
                        isDarkTheme = isDarkTheme,
                        onBackButtonClick = onBackButtonClick,
                        saveManifestation = viewModel::saveManifestation,
                        modifier = Modifier.padding(horizontal = horizontalMargin)
                    )
                }
            }
            2 -> {  // edit a manifestation
                if( manifestations.isNotEmpty()) {
                    ManifestationForm(
                        item = manifestations.find { it.id == selectedItem }!!,
                        isDarkTheme = isDarkTheme,
                        onBackButtonClick = onBackButtonClick,
                        saveManifestation = viewModel::saveManifestation,
                        modifier = Modifier.padding(horizontal = horizontalMargin)
                    )
                }

            }
        }

        if (showDialog) {
            val msg = stringResource(id = R.string.label_retry).replace("AAA", manifestations.find { it.id == selectedItem }!!.overview)
            ValidationDialog(
                modifier = Modifier,
                title = stringResource(id = R.string.title_confirm_remove),
                msg = msg,
                buttonLabels = Pair(
                    stringResource(id = R.string.button_validation_confirm),
                    stringResource(id = R.string.button_validation_cancel),
                ),
                onDone = onButtonManifestationConfirmationDelete
            )
        }
    }

}


@Composable
fun ManifestationScreenBody(
    manifestationState: ManifestationState,
) {
    val iconSize = dimensionResource(id = R.dimen.icon_big_size)
    val marginHorizontal = dimensionResource(id = R.dimen.margin_horizontal)
    val lineSpacing = dimensionResource(id = R.dimen.line_spacing_1)

    LazyColumn(modifier = Modifier.padding(end = marginHorizontal)) {
        item {
            //TODO colocar el filtro Search
        }

        if (manifestationState.manifestations.isNotEmpty()) {
            items(manifestationState.manifestations) { item ->
                HorizontalDraggableManifestationItemList(
                    item = item,
                    days = manifestationState.dateDifference.invoke(Date().toFechaTimeDb(), item.dueDate),
                    isDarkTheme = manifestationState.isDarkTheme,
                    categoryColor = manifestationState.categoryColor,
                    onItemDeleteClick = manifestationState.onItemDeleteClick,
                    navigateToJournals = manifestationState.navigateToJournals,
                    onLongPress = manifestationState.onLongPress,
                )
                Spacer(modifier = Modifier.padding(vertical = lineSpacing))
            }
        } else {
            item {
                Row (modifier = Modifier.fillMaxWidth()) {
                    Column (modifier = Modifier.fillMaxWidth(),
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


@Preview(showBackground = true)
@Composable
fun ManifestationScreenPreview() {

    val marginHorizontal = dimensionResource(R.dimen.margin_horizontal)
    val lineSpacing = dimensionResource(R.dimen.line_spacing_1)

    val listOfManifestations: List<ManifestationModel> = listOf(
        ManifestationModel(
            id = 1,
            overview = "Ingreso de USD 6K",
            description = "Estoy muy feliz y agradecido por por haber manifestado antes del 7 de mayo del 2025, ingresos por USD 6K",
            creationDate = Date().toFechaTimeDb(),
            dueDate = Date().toFechaTimeDb(),
            category = CATEGORIES.WEALTH.key,
        ),
        ManifestationModel(
            id = 2,
            overview = "Facturacion mensual de USD 250K",
            description = "estoy muy feliz y agradecido haber manifestado antes del 7 de Mayo del 2025, una facturacion mensual de ingresos por USD 250K.",
            creationDate = Date().toFechaTimeDb(),
            dueDate = Date().toFechaTimeDb(),
            category = CATEGORIES.WEALTH.key,
        ),
    )

    val manifestationState = ManifestationState(
        isDarkTheme = false,
        manifestations = listOfManifestations,
        categoryColor = { _, _ -> spirituality },
        onItemDeleteClick = { _ ->},
        dateDifference = { _, _ -> 5L },
        onLongPress = { _ ->},
        onBackButtonClick = { },
        navigateToJournals = { _ ->}
    )

    ManifestationsTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column {
                ManifestationScreenBody(
                    manifestationState = manifestationState,
                )
            }
        }
    }

}


private const val TAG =  "ManifestationScreen"