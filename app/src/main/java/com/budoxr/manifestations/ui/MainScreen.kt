package com.budoxr.manifestations.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.budoxr.manifestations.R
import com.budoxr.manifestations.ui.navigation.AppNavigation
import com.budoxr.manifestations.ui.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navigationItems = listOf(
        Screens.Expand,
        Screens.LessonScreen,
        Screens.ManifestationScreen,
        Screens.ExerciseScreen
    )
    val currentRoute = currentRoute(navController)
    var presses by remember { mutableIntStateOf(0) }
    var expanded by remember { mutableStateOf(false) }

    PermanentNavigationDrawer(
        drawerContent = {
            PermanentDrawerSheet(modifier = Modifier.width(if (expanded) 248.dp else 96.dp)) {
                Spacer(Modifier.height(12.dp))
                navigationItems.forEach { screen ->
                    NavigationDrawerItem(
                        icon = {
                            Spacer(Modifier.width(10.dp))
                            when (screen) {
                                Screens.Expand -> Icon(Screens.Expand.icon, contentDescription = stringResource( id = R.string.content_description_icon ))
                                Screens.LessonScreen -> Icon(Screens.LessonScreen.icon, contentDescription =  stringResource( id = R.string.content_description_icon ))
                                Screens.ManifestationScreen -> Icon(Screens.ManifestationScreen.icon, contentDescription =  stringResource( id = R.string.content_description_icon ))
                                Screens.ExerciseScreen -> Icon(Screens.ExerciseScreen.icon, contentDescription =  stringResource( id = R.string.content_description_icon ))
                            }
                        },
                        badge = {
                            if(screen == Screens.Expand) Icon(Icons.Default.Settings, contentDescription = stringResource( id = R.string.content_description_icon ))
                        },
                        label = { if (expanded && !screen.equals(Screens.Expand)) Text(screen.title) else null },
                        selected = currentRoute == screen.route,
                        onClick = {
                            if (screen.route == Screens.Expand.route) {
                                expanded = !expanded
                            } 
                            else {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id){
                                        saveState = true
                                    }
                                    launchSingleTop = true
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
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Top App Bar") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "Bottom App Bar"
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { presses++ }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) { innerPadding ->
            AppNavigation(
                navController = navController,
                startDest = Screens.LessonScreen,
                innerPadding = innerPadding
            )
        }
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
        MainScreen()
    }
}
