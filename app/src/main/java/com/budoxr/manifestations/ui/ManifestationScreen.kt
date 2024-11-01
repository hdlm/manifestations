package com.budoxr.manifestations.ui

import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.budoxr.manifestations.R
import com.budoxr.manifestations.commons.CATEGORIES
import com.budoxr.manifestations.commons.CategoryHelper
import com.budoxr.manifestations.data.repositories.LocalPref
import com.budoxr.manifestations.di.Modules.appModule
import com.budoxr.manifestations.presentation.domain.ManifestationModel
import com.budoxr.manifestations.presentation.presenters.ManifestationScreenUiState
import com.budoxr.manifestations.presentation.presenters.ManifestationViewModel
import com.budoxr.manifestations.ui.navigation.Screens
import com.budoxr.manifestations.ui.theme.ManifestationsTheme
import com.budoxr.manifestations.ui.theme.bright
import com.budoxr.manifestations.ui.theme.gray
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinApplication
import java.util.Date

data class ManifestationState(
    val isDarkTheme: Boolean,
    val manifestations: List<ManifestationModel>,
    val categoryColor: (String) -> Color,
)

@Composable
fun ManifestationScreen(
    navController: NavController,
    isDarkTheme: Boolean,
    innerPadding: PaddingValues,
    viewModel: ManifestationViewModel = koinViewModel()

) {
    Log.i(TAG, "compose / recompose")

    val manifestationScreenUiState = viewModel.uiState.collectAsStateWithLifecycle()
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
                    viewModel.errowShowed = true
                    viewModel.refresh(force = true)
                }
            )
        }
        is ManifestationScreenUiState.Ready -> {
            val manifestations by viewModel.flowOfManifestations.collectAsStateWithLifecycle(initialValue = emptyList())
            ManifestationScreenReady(
                innerPadding = innerPadding,
                manifestations = manifestations,
                navController = navController,
                uiState = uiState,
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
    innerPadding: PaddingValues,
    manifestations: List<ManifestationModel>,
    navController: NavController,
    uiState: ManifestationScreenUiState.Ready,
    viewModel: ManifestationViewModel,
    isDarkTheme: Boolean,
) {

    val horizontalMargin = dimensionResource(id = R.dimen.margin_horizontal)

    var searchPattern by remember { mutableStateOf("") }

    val manifestationState = ManifestationState(
        isDarkTheme = isDarkTheme,
        manifestations = manifestations,
        categoryColor = viewModel::categoryColor
    )

    MaterialTheme {
        Surface(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {

            ManifestationScreenBody(
                manifestationState = manifestationState,
            )

        }

    }

}

@Composable
fun ManifestationScreenBody(
    manifestationState: ManifestationState,
) {
    val marginHorizontal = dimensionResource(id = R.dimen.margin_horizontal)
    val lineSpacing3x = dimensionResource(id = R.dimen.line_spacing_3)
    val lineSpacing = dimensionResource(id = R.dimen.line_spacing_1)

    LazyColumn(modifier = Modifier.padding(marginHorizontal)) {
        item {
            //TODO colocar el filtro Search

        }

        items(manifestationState.manifestations) { item ->
            ManifestationListItem(
                item = item,
                isDarkTheme = manifestationState.isDarkTheme,
                categoryColor = manifestationState.categoryColor,
            )
            Spacer(modifier = Modifier.padding(vertical = lineSpacing))
        }

    }
}



@Composable
fun ManifestationListItem(
    item: ManifestationModel,
    isDarkTheme: Boolean,
    categoryColor: (String) -> Color,
    modifier: Modifier = Modifier
) {
    val lineSpacing = dimensionResource(R.dimen.line_spacing_1)
    val marginHorizontal = dimensionResource(R.dimen.margin_horizontal)
    val separator = dimensionResource(R.dimen.side_separation)

    var overview by remember { mutableStateOf(TextFieldValue(item.overview)) }
    var description by remember { mutableStateOf(TextFieldValue(item.description)) }
    var category by remember { mutableStateOf(TextFieldValue(item.category)) }

    Card(
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, gray),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
    ) {
        Column (modifier = modifier
            .fillMaxWidth()
            .padding(top = marginHorizontal, bottom = lineSpacing, start = marginHorizontal, end = marginHorizontal)
        ) {
            Text(
                text = item.overview,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.padding(vertical = lineSpacing))
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.padding(vertical = separator))
            Row (modifier = Modifier
                .fillMaxWidth()
            ) {
                Column (modifier = Modifier
                    .weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Box (
                        modifier = modifier
                            .clip(MaterialTheme.shapes.small)
                            .background(categoryColor(item.category)),
                    ) {
                        Text(
                            text = item.category,
                            style = MaterialTheme.typography.labelMedium,
                            color = bright,
                            modifier = Modifier
                                .padding(lineSpacing)
                        )
                    }
                }

            }

        }

    }

}


@Preview(showBackground = true)
@Composable
fun ManifestationScreenPreview() {

    KoinApplication( application =  {
        modules(appModule)
    }) {
        val listOfManifestations : List<ManifestationModel> = listOf(
            ManifestationModel(
                id = null,
                overview = "Ingreso de USD 6K",
                description = "Estoy muy feliz y agradecido por por haber manifestado antes del 7 de mayo del 2025, ingresos por USD 6K",
                creationDate = Date(),
                dueDate = Date(),
                category = CATEGORIES.WEALTH.key,
            ),
            ManifestationModel(
                id = null,
                overview = "Facturacion mensual de USD 250K",
                description = "estoy muy feliz y agradecido haber manifestado antes del 7 de Mayo del 2025, una facturacion mensual de ingresos por USD 250K.",
                creationDate = Date(),
                dueDate = Date(),
                category = CATEGORIES.WEALTH.key,
            ),
        )

        val viewModel = ManifestationViewModel()



        val manifestationState = ManifestationState(
            isDarkTheme = false,
            manifestations = listOfManifestations,
            categoryColor = viewModel::categoryColor
        )

        ManifestationsTheme {

            Surface( modifier = Modifier
                .fillMaxSize()
            ) {
                ManifestationScreenBody(
                    manifestationState = manifestationState,
                )
            }
        }
    }

}


private const val TAG =  "ManifestationScreen"