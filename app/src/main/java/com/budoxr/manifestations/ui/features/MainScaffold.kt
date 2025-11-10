package com.budoxr.manifestations.ui.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.budoxr.manifestations.R
import com.budoxr.manifestations.commons.onDismissComposableType
import com.budoxr.manifestations.commons.onDismissType
import com.budoxr.manifestations.ui.theme.ManifestationsTheme
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navController: NavHostController,
    topAppBarTitle: String,
    isDarkTheme: Boolean,
    isFloatingActionVisible: Boolean,
    onFloatingActionButtonClick: (NavHostController) -> Unit,
    onBackButtonClick: (NavHostController) -> Unit,
    content: onDismissComposableType = { },
) {

    val onBackButtonClick: onDismissType = {
        Timber.tag(TAG).d("onBackButtonClick() -> invoked")
        onBackButtonClick.invoke(navController)
    }

    Scaffold(
        topBar = {
            MainScreenTopBar(
                label = topAppBarTitle,
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                onBackButtonClick = onBackButtonClick,
                iconAction = Icons.Filled.AccessTime,
                onActionClick = {},
            )
        },
        floatingActionButton = {
            if (isFloatingActionVisible) {
                FloatingActionButton(onClick = {
                    onFloatingActionButtonClick.invoke(navController)
                }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.content_description_icon))
                }
            }
        }
    ) { innerPadding ->

        Surface(modifier = Modifier.fillMaxSize()
            .padding(innerPadding)
        ) {
            content.invoke()
        }

    }
}


@Composable
@Preview(showBackground = true)
private fun MainScaffoldPreview() {
    val navController = rememberNavController()
    val topAppBarTitle = stringResource(R.string.title_lesson)

    var innerPadding by remember { mutableStateOf(PaddingValues()) }

    val currentRoute: @Composable (NavHostController) -> String? = { navController ->
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val destination: String? =  navBackStackEntry?.destination?.route
        navBackStackEntry?.destination?.route
    }
    val setInnerPadding: (PaddingValues) -> Unit = {
        Timber.tag(TAG).d("getInnerPadding() -> invoked.")
        innerPadding = it
    }

    val current = currentRoute.invoke(navController)

    ManifestationsTheme {
        MainScaffold(
            navController = navController,
            topAppBarTitle = topAppBarTitle,
            isDarkTheme = false,
            isFloatingActionVisible = true,
            onFloatingActionButtonClick = {},
            onBackButtonClick = { navController -> } ,
        ) {
            Column(modifier = Modifier
                .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(text = "Hello World")
            }
        }

    }

}

private const val TAG = "che.MainScaffold"