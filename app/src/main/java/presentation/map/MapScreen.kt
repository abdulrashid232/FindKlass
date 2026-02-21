package presentation.map

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

private val DEFAULT_LOCATION = LatLng(40.7128, -74.0060)

@Composable
fun MapScreen(viewModel: MapViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(DEFAULT_LOCATION, 15f)
    }
    val snackbarHostState = remember { SnackbarHostState() }
    var hasCenteredOnUser by remember { mutableStateOf(false) }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) viewModel.onPermissionGranted()
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // Centre camera on user once on first location fix
    LaunchedEffect(uiState.userLocation) {
        if (!hasCenteredOnUser && uiState.userLocation != null) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(uiState.userLocation!!, 16f)
            )
            hasCenteredOnUser = true
        }
    }

    // When a route loads successfully, animate camera to fit both endpoints
    LaunchedEffect(uiState.routeState) {
        when (val state = uiState.routeState) {
            is RouteState.Success -> {
                val origin = uiState.userLocation
                val dest = uiState.destination
                if (origin != null && dest != null) {
                    val bounds = LatLngBounds.Builder()
                        .include(origin)
                        .include(dest)
                        // Include all polyline points for accuracy
                        .also { builder -> state.points.forEach { builder.include(it) } }
                        .build()
                    // 100 dp padding so markers aren't clipped by the screen edge
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngBounds(bounds, 160)
                    )
                }
            }
            is RouteState.NoLocation ->
                snackbarHostState.showSnackbar("Enable GPS to get directions")
            is RouteState.NoRoute ->
                snackbarHostState.showSnackbar("No walking route found to ${uiState.destinationName}")
            is RouteState.Error ->
                snackbarHostState.showSnackbar("Directions error: ${state.message}")
            else -> Unit
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = uiState.isLocationPermissionGranted)
        ) {
            // Building markers
            uiState.buildings.forEach { building ->
                Marker(
                    state = MarkerState(LatLng(building.latitude, building.longitude)),
                    title = building.name,
                    snippet = "${building.code} · ${building.address}"
                )
            }

            // Destination marker (shown when directions are active)
            uiState.destination?.let { dest ->
                Marker(
                    state = MarkerState(dest),
                    title = uiState.destinationName,
                    snippet = "Destination"
                )
            }

            // Route polyline — only drawn when the route is successfully loaded
            if (uiState.routeState is RouteState.Success) {
                Polyline(
                    points = (uiState.routeState as RouteState.Success).points,
                    color = Color(0xFF1565C0),   // Material Blue 800
                    width = 12f
                )
            }
        }

        // Route info card (distance + walking time)
        if (uiState.routeState is RouteState.Success) {
            val state = uiState.routeState as RouteState.Success
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box {
                    Text(
                        "${state.distanceText}  ·  ${state.durationText} walk",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                    IconButton(
                        onClick = viewModel::clearRoute,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Clear route")
                    }
                }
            }
        }

        // Loading spinner while fetching directions
        if (uiState.routeState is RouteState.Loading || uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
