package presentation

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import data.local.CampusDatabase
import data.remote.DirectionsRepository
import data.repository.CampusRepository
import domain.LocationHelper
import presentation.map.MapViewModel
import presentation.search.SearchViewModel
import presentation.timetable.TimetableViewModel

/**
 * Manual DI factory — creates all ViewModels by injecting their dependencies.
 * No Hilt/Dagger required. Easy to swap later.
 */
class ViewModelFactory(application: Application) : ViewModelProvider.Factory {

    private val db = CampusDatabase.getInstance(application)

    private val repository = CampusRepository(
        buildingDao = db.buildingDao(),
        roomDao = db.roomDao(),
        courseDao = db.courseDao(),
        scheduleDao = db.scheduleDao()
    )

    private val locationHelper = LocationHelper(application)

    // DirectionsRepository is stateless — one instance is fine
    private val directionsRepository = DirectionsRepository()

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(MapViewModel::class.java) ->
            MapViewModel(repository, locationHelper, directionsRepository) as T

        modelClass.isAssignableFrom(SearchViewModel::class.java) ->
            SearchViewModel(repository) as T

        modelClass.isAssignableFrom(TimetableViewModel::class.java) ->
            TimetableViewModel(repository) as T

        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
