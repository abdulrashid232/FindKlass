package presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import data.models.Building
import data.remote.DirectionsRepository
import data.remote.RouteResult
import data.repository.CampusRepository
import domain.LocationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ---- Route state machine ----
sealed class RouteState {
    object Idle : RouteState()
    object Loading : RouteState()
    data class Success(
        val points: List<LatLng>,
        val distanceText: String,
        val durationText: String
    ) : RouteState()
    object NoLocation : RouteState()   // GPS unavailable when user tapped Directions
    object NoRoute : RouteState()      // API returned ZERO_RESULTS
    data class Error(val message: String) : RouteState()
}

data class MapUiState(
    val buildings: List<Building> = emptyList(),
    val userLocation: LatLng? = null,
    val isLocationPermissionGranted: Boolean = false,
    val isLoading: Boolean = true,
    // Destination set when the user picks a class from Timetable or Search
    val destination: LatLng? = null,
    val destinationName: String = "",
    val routeState: RouteState = RouteState.Idle
)

class MapViewModel(
    private val repository: CampusRepository,
    private val locationHelper: LocationHelper,
    private val directionsRepository: DirectionsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadBuildings()
    }

    private fun loadBuildings() {
        viewModelScope.launch {
            repository.getAllBuildings().collect { buildings ->
                _uiState.value = _uiState.value.copy(buildings = buildings, isLoading = false)
            }
        }
    }

    /** Called by the UI once the user grants location permission. */
    fun onPermissionGranted() {
        _uiState.value = _uiState.value.copy(isLocationPermissionGranted = true)
        viewModelScope.launch {
            // Quick last-known fix for immediate camera snap
            locationHelper.getLastLocation()?.let { latLng ->
                _uiState.value = _uiState.value.copy(userLocation = latLng)
            }
            // Then stream live updates
            locationHelper.requestLocationUpdates().collect { latLng ->
                _uiState.value = _uiState.value.copy(userLocation = latLng)
            }
        }
    }

    /**
     * Called from TimetableScreen or SearchScreen when the user taps "Get Directions".
     * Stores the destination and immediately kicks off the route fetch.
     */
    fun setDestination(latLng: LatLng, name: String) {
        _uiState.value = _uiState.value.copy(
            destination = latLng,
            destinationName = name,
            routeState = RouteState.Idle
        )
        fetchDirections()
    }

    /** Clears the active route so the polyline disappears. */
    fun clearRoute() {
        _uiState.value = _uiState.value.copy(
            destination = null,
            destinationName = "",
            routeState = RouteState.Idle
        )
    }

    private fun fetchDirections() {
        val origin = _uiState.value.userLocation
        val destination = _uiState.value.destination

        // Guard: no GPS fix yet
        if (origin == null) {
            _uiState.value = _uiState.value.copy(routeState = RouteState.NoLocation)
            return
        }
        if (destination == null) return

        _uiState.value = _uiState.value.copy(routeState = RouteState.Loading)

        viewModelScope.launch {
            val result = directionsRepository.getWalkingRoute(origin, destination)
            _uiState.value = _uiState.value.copy(
                routeState = when (result) {
                    is RouteResult.Success -> RouteState.Success(
                        points = result.points,
                        distanceText = result.distanceText,
                        durationText = result.durationText
                    )
                    is RouteResult.NoRoute -> RouteState.NoRoute
                    is RouteResult.Error   -> RouteState.Error(result.message)
                }
            )
        }
    }
}
