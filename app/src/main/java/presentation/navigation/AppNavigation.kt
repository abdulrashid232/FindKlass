package presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import presentation.map.MapScreen
import presentation.map.MapViewModel
import presentation.search.SearchScreen
import presentation.search.SearchViewModel
import presentation.timetable.TimetableScreen
import presentation.timetable.TimetableViewModel

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Map : Screen("map", "Map", Icons.Default.LocationOn)
    object Search : Screen("search", "Search", Icons.Default.Search)
    object Timetable : Screen("timetable", "Timetable", Icons.Default.DateRange)
}

private val BOTTOM_NAV_ITEMS = listOf(Screen.Map, Screen.Search, Screen.Timetable)

@Composable
fun AppNavigation(
    mapViewModel: MapViewModel,
    searchViewModel: SearchViewModel,
    timetableViewModel: TimetableViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                BOTTOM_NAV_ITEMS.forEach { screen ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to start so back stack doesn't grow unbounded
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Map.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Map.route) { MapScreen(mapViewModel) }
            composable(Screen.Search.route) { SearchScreen(searchViewModel) }
            composable(Screen.Timetable.route) { TimetableScreen(timetableViewModel) }
        }
    }
}
