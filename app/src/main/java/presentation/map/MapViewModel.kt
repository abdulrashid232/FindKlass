package presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import data.models.Building
import data.repository.CampusRepository
import domain.LocationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MapUiState(
    val buildings: List<Building> = emptyList(),
    val userLocation: LatLng? = null,
    val isLocationPermissionGranted: Boolean = false,
    val isLoading: Boolean = true
)

class MapViewModel(
    private val repository: CampusRepository,
    private val locationHelper: LocationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadBuildings()
    }

    private fun loadBuildings() {
        viewModelScope.launch {
            repository.getAllBuildings().collect { buildings ->
                _uiState.value = _uiState.value.copy(
                    buildings = buildings,
                    isLoading = false
                )
            }
        }
    }

    /** Called by the UI once the user grants location permission. */
    fun onPermissionGranted() {
        _uiState.value = _uiState.value.copy(isLocationPermissionGranted = true)
        viewModelScope.launch {
            // Get a quick last-known fix first for immediate camera positioning
            locationHelper.getLastLocation()?.let { latLng ->
                _uiState.value = _uiState.value.copy(userLocation = latLng)
            }
            // Then subscribe to live updates
            locationHelper.requestLocationUpdates().collect { latLng ->
                _uiState.value = _uiState.value.copy(userLocation = latLng)
            }
        }
    }
}
