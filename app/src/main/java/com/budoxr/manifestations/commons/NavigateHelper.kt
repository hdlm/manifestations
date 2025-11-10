package com.budoxr.manifestations.commons

import androidx.navigation.NavHostController
import com.budoxr.manifestations.ui.navigation.Screens
import timber.log.Timber

class NavigateHelper {

    /**
     * The method handles navigating to the indicated screen.
     * @param route The name of the screen to navigate to.
     */
    fun navigateTo(route: String, navController: NavHostController) {
        Timber.tag(TAG).i("navigateTo: $route")

        when (val screen = getScreenFromRoute(route)) {
            is Screens.Expand -> {
                //TODO not implemented yet
            }
            is Screens.LessonScreen,
            Screens.ManifestationScreen,
            Screens.SettingScreen -> {
                Timber.tag(TAG).i("navigateTo: ${screen.route}")
                navController.navigate(route)
            }
            else -> {
                Timber.tag(TAG).w("navigateTo: Unhandled screen object or null screen for route: $route")
            }
        }
    }

    fun getScreenFromRoute(route: String): Screens? {
        Timber.tag(TAG).d("getScreenFromRoute() -> route: $route")
        val baseRoute = route.substringBefore('/')
        val screen : Screens? = allMenuScreens
            .find { it.baseRoute == baseRoute }

        return screen
    }


    companion object {
        private const val TAG = "che.NavigateHelper"

    }
}


val allMenuScreens: List<Screens>
    get() = listOf(
        Screens.Expand,
        Screens.LessonScreen,
        Screens.ManifestationScreen,
        Screens.JournalScreen,
        Screens.SettingScreen
    )