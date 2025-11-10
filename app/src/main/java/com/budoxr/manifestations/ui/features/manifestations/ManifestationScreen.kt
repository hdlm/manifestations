package com.budoxr.manifestations.ui.features.manifestations

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
import androidx.compose.material.icons.filled.Delete
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
import com.budoxr.manifestations.commons.onDismissType
import com.budoxr.manifestations.commons.onIntType
import com.budoxr.manifestations.commons.onManifestationType
import com.budoxr.manifestations.commons.toFechaTimeDb
import com.budoxr.manifestations.data.repositories.LocalPref
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import com.budoxr.manifestations.presentation.presenters.ManifestationScreenUiState
import com.budoxr.manifestations.presentation.presenters.ManifestationViewModel
import com.budoxr.manifestations.ui.components.HorizontalDraggableManifestationItemList
import com.budoxr.manifestations.ui.components.ManifestationListItem
import com.budoxr.manifestations.ui.components.SwipeableListItem
import com.budoxr.manifestations.ui.navigation.Screens
import com.budoxr.manifestations.ui.theme.ManifestationsTheme
import com.budoxr.manifestations.ui.theme.alert
import com.budoxr.manifestations.ui.theme.spirituality
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber
import java.util.Date

data class ManifestationState(
    val isDarkTheme: Boolean,
    val manifestations: List<ManifestationModel>,
    val onCategoryColor: (String) -> Color,
    val onDateDifference: (String, String) -> Long,
    val onItemDeleteClick: (ManifestationModel) -> Unit,
    val dateDifference: (String, String) -> Long,
    val onLongPress: onIntType,
    val onBackButtonClick: onDismissType,
)

@Composable
fun ManifestationScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    viewModel: ManifestationViewModel = koinViewModel()
) {
    Timber.tag(TAG).i("compose / recompose")

    val manifestationScreenUiState by viewModel.uiState.collectAsStateWithLifecycle()
    when (val uiState = manifestationScreenUiState) {
        is ManifestationScreenUiState.Loading -> {
            ManifestationScreenLoading()
        }
        is ManifestationScreenUiState.ListManifestation -> {
            ManifestationScreenListManifestation(
                uiState = uiState,
                isDarkTheme = isDarkTheme,
                navController = navController,
                onAddClick = viewModel::changeToAddManifestationState,
                onEditClick = viewModel::changeToEditManifestationState,
                onDeleteClick = viewModel::changeToDeleteManifestationState,
                onManifestationDetails = viewModel::changeToManifestationDetailsState,
                onCategoryColor = viewModel::categoryColor,
                onDateDifference = viewModel::dateDifference,
            )

        }

        is ManifestationScreenUiState.AddManifestation -> {
            ManifestationScreenAddManifestation(
                uiState = uiState,
                isDarkTheme = isDarkTheme,
                navController = navController,
            )
        }
        is ManifestationScreenUiState.EditManifestation -> {
            ManifestationScreenEditManifestation(
                uiState = uiState,
                isDarkTheme = isDarkTheme,
                navController = navController,
            )
        }

        is ManifestationScreenUiState.DeleteManifestation -> TODO()
        is ManifestationScreenUiState.ManifestationDetails -> TODO()
        is ManifestationScreenUiState.Error -> TODO()
    }

}

@Composable
fun ManifestationScreenLoading(modifier: Modifier = Modifier,
) {

    val iconSize = dimensionResource(id = R.dimen.icon_huge_size)
    val areaSize = 94.dp

    Box {
        CircularProgressIndicator(
            modifier = Modifier
                .size(areaSize)
                .align(Alignment.Center),
            strokeWidth = 8.dp,
            color = MaterialTheme.colorScheme.primary
        )

        Image(
            modifier = Modifier
                .align(Alignment.Center)
                .clip(CircleShape)
                .size(iconSize),
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(id = R.string.content_description_logo),
            contentScale = ContentScale.Fit,
        )
    }
}


@Composable
fun ManifestationScreenError(
    innerPadding: PaddingValues,
    msg: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
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
fun ManifestationScreenListManifestation(
    uiState: ManifestationScreenUiState.ListManifestation,
    isDarkTheme: Boolean,
    navController: NavController,
    onAddClick: onDismissType,
    onEditClick: onManifestationType,
    onDeleteClick: onManifestationType,
    onManifestationDetails: onManifestationType,
    onCategoryColor: (String) -> Color,
    onDateDifference: (String, String) -> Long,
) {
    val horizontalMargin = dimensionResource(id = R.dimen.margin_horizontal)

    var searchPattern by remember { mutableStateOf("") }


    val onItemClick: onManifestationType = {
        Timber.tag(TAG).d("onItemClick() -> invoked, id: ${it.id}")
    }
    val onLongPress: onIntType = { id ->
        Timber.tag(TAG).d("onLongPress() -> invoked, id: $id")
//        onEdit.invoke(id)
    }
    val onItemDeleteClick: (ManifestationModel) -> Unit = { manifestation ->
        Timber.tag(TAG).d("onItemDeleteClick() -> invoked, manifestation id: ${manifestation.id}")
    }


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = horizontalMargin)
    ) {
        val iconSize = dimensionResource(id = R.dimen.icon_big_size)
        val marginHorizontal = dimensionResource(id = R.dimen.margin_horizontal)
        val lineSpacing = dimensionResource(id = R.dimen.line_spacing_1)

        LazyColumn(modifier = Modifier.padding(end = marginHorizontal)) {
            item {
                Text(text = stringResource(R.string.label_manifestation), style = MaterialTheme.typography.titleLarge)
            }

            if (uiState.manifestations.isNotEmpty()) {
                items(uiState.manifestations) { item ->
                    SwipeableListItem(
                        modifier = Modifier,
                        item = item,
                        alertColor = alert,
                        iconSize = dimensionResource(id = R.dimen.icon_medium_size),
                        itemClickDisable = false,
                        actionIcon = Icons.Filled.Delete,
                        actionLabel = stringResource(id = R.string.label_icon_delete),
                        onActionClick = onItemDeleteClick,
                        onItemClick = onItemClick,
                    ) {
                        ManifestationListItem(
                            item = item,
                            days = onDateDifference.invoke(Date().toFechaTimeDb(), item.dueDate),
                            isDarkTheme = isDarkTheme,
                            categoryColor = onCategoryColor,
                            onLongPress = onLongPress,
                            modifier = Modifier
                        )

                    }
//                    HorizontalDraggableManifestationItemList(
//                        item = item,
//                        days = onDateDifference.invoke(Date().toFechaTimeDb(), item.dueDate),
//                        isDarkTheme = isDarkTheme,
//                        categoryColor = onCategoryColor,
//                        onItemDeleteClick = onItemDeleteClick,
//                        navigateToJournals = {},
//                        onLongPress = onLongPress,
//                    )
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
    
}


@Composable
fun ManifestationScreenAddManifestation(
    uiState: ManifestationScreenUiState.AddManifestation,
    isDarkTheme: Boolean,
    navController: NavController,
) {
    //TODO not yet implemented

}

@Composable
fun ManifestationScreenEditManifestation(
    uiState: ManifestationScreenUiState.EditManifestation,
    isDarkTheme: Boolean,
    navController: NavController,
) {
    //TODO not yet implemented

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
            Text(text = stringResource(R.string.label_manifestation), style = MaterialTheme.typography.titleLarge)
        }

        if (manifestationState.manifestations.isNotEmpty()) {
            items(manifestationState.manifestations) { item ->
                HorizontalDraggableManifestationItemList(
                    item = item,
                    days = manifestationState.dateDifference.invoke(Date().toFechaTimeDb(), item.dueDate),
                    isDarkTheme = manifestationState.isDarkTheme,
                    categoryColor = manifestationState.onCategoryColor,
                    onItemDeleteClick = manifestationState.onItemDeleteClick,
                    navigateToJournals = {},
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
            startDate = Date().toFechaTimeDb(),
            dueDate = Date().toFechaTimeDb(),
            category = CATEGORIES.WEALTH.key,
        ),
        ManifestationModel(
            id = 2,
            overview = "Facturacion mensual de USD 250K",
            description = "estoy muy feliz y agradecido haber manifestado antes del 7 de Mayo del 2025, una facturacion mensual de ingresos por USD 250K.",
            startDate = Date().toFechaTimeDb(),
            dueDate = Date().toFechaTimeDb(),
            category = CATEGORIES.WEALTH.key,
        ),
    )

    val manifestationState = ManifestationState(
        isDarkTheme = false,
        manifestations = listOfManifestations,
        onCategoryColor = { _ -> spirituality },
        onItemDeleteClick = { _ -> },
        dateDifference = { _, _ -> 5L },
        onLongPress = { _ -> },
        onBackButtonClick = { },
        onDateDifference = { _,_ -> 0L},
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


private const val TAG =  "che.ManifestationScreen"