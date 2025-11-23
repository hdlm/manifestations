package com.budoxr.manifestations.ui.features

import android.content.res.Configuration
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.budoxr.manifestations.presentation.presenters.MainViewModel
import com.budoxr.manifestations.ui.components.SettingBottomSheet
import com.budoxr.manifestations.ui.navigation.AppNavigation
import com.budoxr.manifestations.ui.navigation.Screens
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    askReadExternalStoragePermission: () -> Boolean,
    askWriteExternalStoragePermission: () -> Boolean,
    viewModel: MainViewModel = koinViewModel(),
) {
    val navController = rememberNavController()
    val navigationItems =  viewModel.navigationItems

    val context = LocalContext.current
    val currentRoute = viewModel.currentRoute(navController)
    val isDarkTheme by remember { mutableStateOf( context.resources.getConfiguration().uiMode and Configuration.UI_MODE_NIGHT_MASK === Configuration.UI_MODE_NIGHT_YES ) }
    var showSettings by remember { mutableStateOf(false) }
    val isDrawerVisible = viewModel.isDrawerVisible


//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val expanded by viewModel.expanded.collectAsStateWithLifecycle()


    //#region high-order functions
    val onFloatingActionButtonClick: (NavHostController) -> Unit = { navController ->
        Timber.tag(TAG).i("show Add Manifestation Form.")
        viewModel.floatingActionButtonClick(currentRoute!!, navController)
    }
    //#endregion

//#region legacy code
//    val onBackButtonClick: onDismissType = {
//        if (!isDrawerVisible) {
//            isDrawerVisible = true
//            isFloatingActionVisible = true
//        }
//        val value = navController.popBackStack()
//        Log.d(TAG, "onBackButtonClick() -> clicked\n\treturned value: $value")
//    }
//    val onEditMode: onIntType = { manifestationId ->
//        Log.d(TAG, "onEditMode() -> invoked, manifestationId: $manifestationId")
//        val currentScreen = LocalPref.getSession()?.currentScreen ?: ""
//        when (currentScreen) {
//            Screens.ManifestationScreen.route -> {
//                isFloatingActionVisible = false
//                isDrawerVisible = false
//                val screenName = Screens.ManifestationScreen.route.substringBefore('/')
//                val destination = "${screenName}/2/$manifestationId"
//                Log.d(TAG, "from Manifestation to destination: $destination")
//                navController.navigate(destination)
//            }
//            Screens.JournalScreen.route -> {
//                isFloatingActionVisible = false
//                isDrawerVisible = false
//                val screenName = Screens.JournalScreen.route.substringBefore('/')
//                // screen name, page, hashCode
//                val destination = "${screenName}/2/$manifestationId/0"
//                Log.d(TAG, "from Journal to destination: $destination")
//                navController.navigate(destination)
//            }
//            else -> {
//                // do nothing
//            }
//        }
//    }
//    val onSaveButtonClick: onDismissType = {
//        val currentScreen = LocalPref.getSession()?.currentScreen ?: ""
//        Log.d(TAG, "onSaveButtonClick() -> invoked, current screen: $currentScreen")
//        when (currentScreen) {
//            Screens.ManifestationScreen.route -> {
//                // not applied, because it is auto-saved
//            }
//            Screens.JournalScreen.route -> {
//                // not applied, because it is auto-saved
//            }
//        }
//
//    }
//    val onSettingsButtonClick: onDismissType = {
//        Log.d(TAG, "onSettingsButtonClick() -> invoked")
//        showSettings = true
//    }
//    val navigateToJournals: onIntType = { manifestationId ->
//        Log.d(TAG, "navigateToJournals() -> invoked, manifestation-Id: $manifestationId")
//        isFloatingActionVisible = true
//        isDrawerVisible = false
//        topAppBarTitle = Screens.JournalScreen.title
//        val screenName = Screens.JournalScreen.route.substringBefore('/')
//        val destination = "${screenName}/0/$manifestationId/0"  // screen, page, manifestationId, lesson
//        navController.navigate(destination)
//    }
//    val onFloatingActionButtonClick: onDismissType = {
//        Log.d(TAG, "onFloatingActionButtonClick() -> invoked")
//
//        val currentScreen = LocalPref.getSession()?.currentScreen ?: ""
//        when (currentScreen) {
//            Screens.ManifestationScreen.route -> {
//                isFloatingActionVisible = false
//                isDrawerVisible = false
//                val screenName = Screens.ManifestationScreen.route.substringBefore('/')
//                val destination = "${screenName}/1/0"
//                Log.d(TAG, "from Manifestation to destination: $destination")
//                navController.navigate(destination)
//            }
//            Screens.JournalScreen.route -> {
//                isFloatingActionVisible = false
//                isDrawerVisible = false
//                val session = LocalPref.getSession()!!
//                val screenName = Screens.JournalScreen.route.substringBefore('/')
//                // screen name, page, lesson
//                val destination = "${screenName}/1/${session.manifestation}/${session.lesson}"
//                Log.d(TAG, "from Journal to destination: $destination")
//                topAppBarTitle = Screens.JournalScreen.title
//                navController.navigate(destination)
//            }
//            else -> {
//                // do nothing
//            }
//        }
    //#endregion


    if (isDrawerVisible) {
        MainScreenDrawer(
            navController = navController,
            expanded = expanded,
            currentRoute = currentRoute,
            navigationItems = navigationItems,
            navigateTo = viewModel::navigateTo,
        ) {
            MainScaffold(
                navController = navController,
                topAppBarTitle = viewModel.topAppBarTitle,
                isDarkTheme = isDarkTheme,
                isFloatingActionVisible = viewModel.isFloatingActionVisible,
                onFloatingActionButtonClick = onFloatingActionButtonClick,
                onBackButtonClick = viewModel::backButtonClick,
            ) {
                AppNavigation(
                    navController = navController,
                    startDest = Screens.ManifestationScreen,
                    isDarkTheme = isDarkTheme,
                )
            }
        }
    } else {
        MainScaffold(
            navController = navController,
            topAppBarTitle = viewModel.topAppBarTitle,
            isDarkTheme = isDarkTheme,
            isFloatingActionVisible = viewModel.isFloatingActionVisible,
            onFloatingActionButtonClick= onFloatingActionButtonClick,
            onBackButtonClick = viewModel::backButtonClick,
        ) {
            AppNavigation(
                navController = navController,
                startDest = Screens.ManifestationScreen,
                isDarkTheme = isDarkTheme,
            )
        }
    }

    if (showSettings) {
        SettingBottomSheet(
            onDismissBottomSheet = { showSettings = false },
            askReadExternalStoragePermission = askReadExternalStoragePermission,
            askWriteExternalStoragePermission = askWriteExternalStoragePermission
        )
    }

}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MaterialTheme {
        MainScreen(
            askWriteExternalStoragePermission = {true},
            askReadExternalStoragePermission = {true}
        )
    }
}

private const val TAG = "che.MainScreen"