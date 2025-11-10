package com.budoxr.manifestations.ui.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.budoxr.manifestations.R
import com.budoxr.manifestations.commons.onNavigateType
import com.budoxr.manifestations.commons.onStringType
import com.budoxr.manifestations.ui.navigation.Screens
import com.budoxr.manifestations.ui.theme.ManifestationsTheme
import timber.log.Timber

@Composable
fun MainScreenDrawer(
    navController: NavHostController,
    expanded: Boolean,
    currentRoute: String?,
    navigationItems: List<Screens>,
    navigateTo: onNavigateType,
    content: @Composable () -> Unit
) {
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
                                Screens.SettingScreen -> Icon(Screens.SettingScreen.icon, contentDescription =  stringResource( id = R.string.content_description_icon ))
                                else -> {
                                    // do nothing
                                }
                            }
                        },
                        badge = {
                            if(screen == Screens.Expand) Icon(Icons.Default.ArrowBackIosNew, contentDescription = stringResource( id = R.string.content_description_icon ))
                        },
                        label = { if (expanded && !screen.equals(Screens.Expand)) Text(screen.title) else null },
                        selected = currentRoute == screen.route,
                        onClick = {
                            Timber.tag(TAG).i("navigate to: ${screen.route.substringBefore('/')}")
                            navigateTo.invoke(screen.route, navController)
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                    if(screen == Screens.Expand) Spacer(Modifier.height(24.dp))
                }

            }
        }
    ) {
        content.invoke()
    }
}



@Composable
@Preview(showBackground = true)
private fun MainScreenDrawerPreview() {

    val navController = rememberNavController()
    val navigationItems = listOf(
        Screens.Expand,
        Screens.LessonScreen,
        Screens.ManifestationScreen,
        Screens.SettingScreen
    )

    val currentRoute: @Composable (NavHostController) -> String? = { navController ->
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val destination: String? =  navBackStackEntry?.destination?.route
        navBackStackEntry?.destination?.route
    }

    ManifestationsTheme {
        MainScreenDrawer(
            navController = navController,
            expanded = true,
            currentRoute = currentRoute.invoke(navController),
            navigateTo = { _, _ ->},
            navigationItems = navigationItems,
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


private const val TAG = "che.MainScreenDrawer"