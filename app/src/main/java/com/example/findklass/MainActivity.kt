package com.example.findklass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.findklass.ui.theme.FindKlassTheme
import presentation.ViewModelFactory
import presentation.map.MapViewModel
import presentation.navigation.AppNavigation
import presentation.search.SearchViewModel
import presentation.timetable.TimetableViewModel

class MainActivity : ComponentActivity() {

    private val factory by lazy { ViewModelFactory(application) }

    private val mapViewModel: MapViewModel by viewModels { factory }
    private val searchViewModel: SearchViewModel by viewModels { factory }
    private val timetableViewModel: TimetableViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FindKlassTheme {
                AppNavigation(
                    mapViewModel = mapViewModel,
                    searchViewModel = searchViewModel,
                    timetableViewModel = timetableViewModel
                )
            }
        }
    }
}