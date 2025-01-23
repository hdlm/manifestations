package com.budoxr.manifestations.ui

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.budoxr.manifestations.R
import com.budoxr.manifestations.commons.onDismissType
import com.budoxr.manifestations.commons.onIntType
import com.budoxr.manifestations.data.repositories.LocalPref
import com.budoxr.manifestations.ui.components.SettingBottomSheet
import com.budoxr.manifestations.ui.navigation.AppNavigation
import com.budoxr.manifestations.ui.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    askReadExternalStoragePermission: () -> Boolean,
    askWriteExternalStoragePermission: () -> Boolean,
) {
    val navController = rememberNavController()
    val navigationItems = listOf(
        Screens.Expand,
        Screens.LessonScreen,
        Screens.ManifestationScreen,
        Screens.JournalScreen,
        Screens.SettingScreen
    )

    val appName =  stringResource(id = R.string.app_name)
    val context = LocalContext.current
    val currentRoute = currentRoute(navController)
    var expanded by remember { mutableStateOf(false) }
    var topAppBarTitle by remember { mutableStateOf(appName) }
    val isDarkTheme by remember { mutableStateOf( context.resources.getConfiguration().uiMode and Configuration.UI_MODE_NIGHT_MASK === Configuration.UI_MODE_NIGHT_YES ) }
    var showSettings by remember { mutableStateOf(false) }
    var manifestationId by remember { mutableStateOf(0) }

    var isFloatingActionVisible by remember { mutableStateOf(false)}
    var isDrawerVisible by remember { mutableStateOf(true)}

    val onBackButtonClick: onDismissType = {
        if (!isDrawerVisible) {
            isDrawerVisible = true
            isFloatingActionVisible = true
        }
        val value = navController.popBackStack()
        Log.d(TAG, "onBackButtonClick() -> clicked\n\treturned value: $value")
    }
    val onEditMode: onIntType = { id ->
        Log.d(TAG, "onEditMode() -> invoked, id: $id")
        val currentScreen = LocalPref.getSession()?.currentScreen ?: ""
        when (currentScreen) {
            Screens.ManifestationScreen.route -> {
                isFloatingActionVisible = false
                isDrawerVisible = false
                val screenName = Screens.ManifestationScreen.route.substringBefore('/')
                val destination = "${screenName}/2/$id"
                navController.navigate(destination)
            }
            Screens.JournalScreen.route -> {
                isFloatingActionVisible = false
                isDrawerVisible = false
                val screenName = Screens.JournalScreen.route.substringBefore('/')
                // screen name, page, hashCode
                val destination = "${screenName}/2/$id/0"
                navController.navigate(destination)
            }
            else -> {
                // do nothing
            }
        }
    }
    val onSaveButtonClick: onDismissType = {
        val currentScreen = LocalPref.getSession()?.currentScreen ?: ""
        Log.d(TAG, "onSaveButtonClick() -> invoked, current screen: $currentScreen")
        when (currentScreen) {
            Screens.ManifestationScreen.route -> {
                // not applied, because it is auto-saved
            }
            Screens.JournalScreen.route -> {
                // not applied, because it is auto-saved
            }
        }

    }
    val onSettingsButtonClick: onDismissType = {
        Log.d(TAG, "onSettingsButtonClick() -> invoked")
        showSettings = true
    }
    val onFloatingActionButtonClick: onDismissType = {
        Log.d(TAG, "onFloatingActionButtonClick() -> invoked")

        val currentScreen = LocalPref.getSession()?.currentScreen ?: ""
        when (currentScreen) {
            Screens.ManifestationScreen.route -> {
                isFloatingActionVisible = false
                isDrawerVisible = false
                val screenName = Screens.ManifestationScreen.route.substringBefore('/')
                val destination = "${screenName}/1/0"
                navController.navigate(destination)
            }
            Screens.JournalScreen.route -> {
                isFloatingActionVisible = false
                isDrawerVisible = false
                val session = LocalPref.getSession()!!
                val screenName = Screens.JournalScreen.route.substringBefore('/')
                // screen name, page, lesson
                val destination = "${screenName}/1/${session.manifestation}/${session.lesson}"
                topAppBarTitle = Screens.JournalScreen.title
                navController.navigate(destination)
            }
            else -> {
                // do nothing
            }
        }

    }

    if (isDrawerVisible) {
        PermanentNavigationDrawer(
            drawerContent = {
                PermanentDrawerSheet(modifier = Modifier
                    .padding(end = 8.dp)
                    .width(if (expanded) 248.dp else 96.dp),
                    drawerContentColor = MaterialTheme.colorScheme.onTertiary,
                    drawerContainerColor = MaterialTheme.colorScheme.tertiary,
                    drawerShape = MaterialTheme.shapes.small
                ) {
                    Spacer(Modifier.height(12.dp))
                    navigationItems.forEach { screen ->
                        NavigationDrawerItem(
                            icon = {
                                Spacer(Modifier.width(10.dp))
                                when (screen) {
                                    Screens.Expand -> { Icon(Screens.Expand.icon, contentDescription = stringResource( id = R.string.content_description_icon )) }
                                    Screens.LessonScreen -> Icon(Screens.LessonScreen.icon, contentDescription =  stringResource( id = R.string.content_description_icon ))
                                    Screens.ManifestationScreen -> Icon(Screens.ManifestationScreen.icon, contentDescription =  stringResource( id = R.string.content_description_icon ))
                                    Screens.JournalScreen -> Icon(Screens.JournalScreen.icon, contentDescription =  stringResource( id = R.string.content_description_icon ))
                                    Screens.SettingScreen -> Icon(Screens.SettingScreen.icon, contentDescription =  stringResource( id = R.string.content_description_icon ))
                                }
                            },
                            badge = {
                                if(screen == Screens.Expand) Icon(Icons.Default.ArrowBackIosNew, contentDescription = stringResource( id = R.string.content_description_icon ))
                            },
                            label = { if (expanded && !screen.equals(Screens.Expand)) Text(screen.title) else null },
                            selected = currentRoute == screen.route,
                            onClick = {
                                if (screen.route == Screens.Expand.route) {
                                    expanded = !expanded
                                    Log.d(TAG, "expanded: $expanded")
                                }
                                else {
                                    if (screen.route == Screens.LessonScreen.route ) {
                                        topAppBarTitle = screen.title
                                        isFloatingActionVisible = false
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id){
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                        }
                                    } else if (screen.route == Screens.SettingScreen.route) {
                                        onSettingsButtonClick.invoke()
                                    } else {
                                        topAppBarTitle = screen.title
                                        isFloatingActionVisible = true
                                        val screenName = screen.route.substringBefore('/')
                                        val destination = "${screenName}/0/0"
                                        navController.navigate(destination) {
                                            popUpTo(navController.graph.findStartDestination().id){
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                        }
                                    }

                                }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                        if(screen.route == Screens.Expand.route) Spacer(Modifier.height(24.dp))
                    }

                }
            }
        ) {

            if (askReadExternalStoragePermission.invoke()) {
                askWriteExternalStoragePermission.invoke()
            }
            MainScaffold(
                navController = navController,
                topAppBarTitle = topAppBarTitle,
                isDarkTheme = isDarkTheme,
                isDrawerVisible = isDrawerVisible,
                isFloatingActionVisible = isFloatingActionVisible,
                onFloatingActionButtonClick = onFloatingActionButtonClick,
                onBackButtonClick = onBackButtonClick,
                onEditMode = onEditMode,
                onSaveButtonClick = onSaveButtonClick,
            )

        }
    } else {

        MainScaffold(
            navController = navController,
            topAppBarTitle = topAppBarTitle,
            isDrawerVisible = isDrawerVisible,
            isFloatingActionVisible = isFloatingActionVisible,
            onFloatingActionButtonClick = onFloatingActionButtonClick,
            isDarkTheme = isDarkTheme,
            onBackButtonClick = onBackButtonClick,
            onEditMode = onEditMode,
            onSaveButtonClick = onSaveButtonClick,
        )

    }

    if (showSettings) {
        SettingBottomSheet(
            onDismissBottomSheet = { showSettings = false },
            askReadExternalStoragePermission = askReadExternalStoragePermission,
            askWriteExternalStoragePermission = askWriteExternalStoragePermission
        )
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navController: NavHostController,
    topAppBarTitle: String,
    isDarkTheme: Boolean,
    isDrawerVisible: Boolean,
    isFloatingActionVisible: Boolean,
    onFloatingActionButtonClick: onDismissType,
    onBackButtonClick: onDismissType,
    onEditMode: onIntType,
    onSaveButtonClick: onDismissType,
) {
    val iconSize = dimensionResource(R.dimen.icon_topbar_size)
    val marginHorizontal = dimensionResource(R.dimen.margin_horizontal)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topAppBarTitle) },
                navigationIcon = {
                    if (!isDrawerVisible) {
                        IconButton(
                            onClick = {
                                onBackButtonClick.invoke()
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .size(iconSize),
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = stringResource(id = R.string.content_description_icon)
                            )
                        }
                    }

                },
                actions = {
                    if (!isDrawerVisible) {
                        IconButton(
                            onClick = {
                                //TODO save button event
                            }
                        ) {
                            IconButton(
                                onClick = {
                                    onSaveButtonClick.invoke()
                                }
                            ) {
                                Icon(
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .size(iconSize),
                                    imageVector = Icons.Default.Save,
                                    contentDescription = stringResource(id = R.string.content_description_icon)
                                )
                            }

                        }

                    }

                },
//                    colors = TopAppBarDefaults.topAppBarColors(
//                        containerColor = MaterialTheme.colorScheme.primaryContainer,
//                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
//                    )
            )
        },
        bottomBar = {
//            BottomAppBar(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
//            ) {
//                Text(
//                    modifier = Modifier.fillMaxWidth(),
//                    textAlign = TextAlign.Center,
//                    text = "Bottom App Bar"
//                )
//            }
        },
        floatingActionButton = {
            if (isFloatingActionVisible) {
                FloatingActionButton(onClick = {
                    onFloatingActionButtonClick.invoke()
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            startDest = Screens.LessonScreen,
            innerPadding = innerPadding,
            isDarkTheme = isDarkTheme,
            onEditMode = onEditMode,
        )
    }
}


@Composable
private fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
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