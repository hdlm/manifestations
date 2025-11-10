package com.budoxr.manifestations.presentation.presenters

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.budoxr.manifestations.commons.NavigateHelper
import com.budoxr.manifestations.data.repositories.LocalPref
import com.budoxr.manifestations.presentation.domain.SessionModel
import com.budoxr.manifestations.ui.navigation.Screens
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import timber.log.Timber

class MainViewModel : KoinViewModel() {
    private val navigateHelper: NavigateHelper by inject()

    private val _isDrawerVisible = MutableStateFlow(true)
    val isDrawerVisible : Boolean
        get() = _isDrawerVisible.asStateFlow().value

    private val _isFloatingActionVisible = MutableStateFlow(true)
    var isFloatingActionVisible : Boolean
        get() = _isFloatingActionVisible.asStateFlow().value
        set(value) {
            _isFloatingActionVisible.update { value }
        }

    private val _expanded = MutableStateFlow(true)
    val expanded : StateFlow<Boolean>
        get() = _expanded.asStateFlow()

    private val _sessionModel = LocalPref.getSession() ?: SessionModel()
    val session: SessionModel
        get() = _sessionModel

    private val _topAppBarTitle = MutableStateFlow(Screens.LessonScreen.title)
    var topAppBarTitle: String
        get() = _topAppBarTitle.asStateFlow().value
        set(value) {
            _topAppBarTitle.update { value }
        }

    val navigationItems = navigateHelper.allMenuScreens

    private val _uiState = MutableStateFlow<MainScreenUiState>(MainScreenUiState.Manifestations)
    val uiState : StateFlow<MainScreenUiState>
        get() = _uiState.asStateFlow()

    init {
        Timber.tag(TAG).d("init() -> called")

        viewModelScope.launch {
            com.budoxr.manifestations.commons.util.combine(
                _expanded,
                _isDrawerVisible,
                _isFloatingActionVisible
            ) { expanded,
                isDrawerVisible,
                isFloatingActionVisible ->

                MainScreenUiState.Manifestations

            }.catch { throwable ->
                throwable.printStackTrace()
                _uiState.value = MainScreenUiState.Error(throwable.message)
                Timber.tag(TAG).e(throwable.localizedMessage, throwable)
            }.collect {
                _uiState.value = it
            }
        }

    }


    //TODO this method was replaced by [NavigateHelper] class
    fun onDrawerClick(navController: NavHostController, screen: Screens, ) {
        if (screen.route == Screens.Expand.route) {
            _expanded.update { !expanded.value }
            Timber.tag(TAG).d("onDrawerClick() -> expanded: $expanded")
        } else {
            Timber.tag(TAG)
            when (screen.route) {
                Screens.LessonScreen.route -> {
                    Timber.tag(TAG).i("onDrawerClick() -> LessonScreen clicked.")
                    _topAppBarTitle.update { screen.title }
                    _isFloatingActionVisible.update { !isFloatingActionVisible }
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id){
                            saveState = true
                        }
                        launchSingleTop = true
                    }
                }
                Screens.SettingScreen.route -> {
                    Timber.tag(TAG).i("onDrawerClick() -> SettingScreen clicked.")
//                    onSettingsButtonClick.invoke()
                }
                Screens.ManifestationScreen.route -> {
                    Timber.tag(TAG).i("onDrawerClick() -> ManifestationScreen clicked.")
                    val destination = screen.route
                    navigateTo(navController = navController, destination = destination )
                }
                else -> {
                    _topAppBarTitle.update { screen.title }
                    _isFloatingActionVisible.update { !isFloatingActionVisible }
                    val screenName = screen.route.substringBefore('/')
                    val destination = "${screenName}/0/0"
                    navigateTo(navController = navController, destination = destination)

                }
            }
        }
    }

    private fun navigateTo(navController: NavHostController, destination: String) {
        navController.navigate(destination) {
            popUpTo(navController.graph.findStartDestination().id){
                saveState = true
            }
            launchSingleTop = true
        }
    }


    fun backButtonClick(navController: NavHostController) {
        if (!isDrawerVisible) {
            _isDrawerVisible.update { true }
            _isFloatingActionVisible.update { true }
        }
        navController.popBackStack()
        Timber.tag(TAG).d("onBackButtonClick() -> clicked")
    }

    fun floatingActionButtonClick(currentRoute: String, navController: NavHostController) {
        Timber.tag(TAG).d("floatingActionButtonClick() -> invoked, currentRoute: $currentRoute")
        val screen = navigateHelper.getScreenFromRoute(currentRoute)
        if (screen != null) {
            when (screen) {
                is Screens.ManifestationScreen -> {
                    _isFloatingActionVisible.update { false }
                    _isDrawerVisible.update { false }
                    val destination = Screens.ManifestationAddFormScreen.route
                    navController.navigate(destination)
                }
                else -> {
                    //TODO not implemented
                }
            }
        }

    }


    fun navigateTo(route: String, navController: NavHostController) {
        navigateHelper.navigateTo(
            route = route,
            navController = navController,
            switchExpanded = ::switchExpanded
        )
    }

    @Composable
    fun currentRoute(navController: NavHostController): String? {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val destination: String? =  navBackStackEntry?.destination?.route
        Timber.tag(TAG).d("currentRoute() -> called, destination: ${destination?.substringBefore('/')}")
        return navBackStackEntry?.destination?.route
    }


    fun switchExpanded() {
        Timber.tag(TAG).i("switch 'Expanded': ${expanded}")
        _expanded.update { !expanded.value }
    }

    fun changeToManifestationScreen() {
        _uiState.update { MainScreenUiState.Manifestations }

    }

    fun changeToJournalsScreen() {
        _uiState.update { MainScreenUiState.Journals }

    }

    fun changeToLessonsScreen() {
        _uiState.update { MainScreenUiState.Lessons }

    }

    companion object {
        private const val TAG = "che.MainViewModel"
    }

}


sealed interface MainScreenUiState {
    data object Lessons : MainScreenUiState
    data object Manifestations : MainScreenUiState
    data object Journals : MainScreenUiState

    data class Error(
        val errorMessage: String? = null
    ) : MainScreenUiState
}
