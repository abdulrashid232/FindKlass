package domain

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Wraps FusedLocationProviderClient with coroutine-friendly helpers.
 * Assumes permissions have already been granted before calling these functions;
 * the UI layer is responsible for the permission request flow.
 */
class LocationHelper(context: Context) {

    private val client: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * Returns the device's last known location as a one-shot suspend call.
     * Returns null if no cached location is available (e.g. device just rebooted).
     */
    @SuppressLint("MissingPermission")
    suspend fun getLastLocation(): LatLng? {
        val location: Location? = client.lastLocation.await()
        return location?.let { LatLng(it.latitude, it.longitude) }
    }

    /**
     * Emits continuous location updates as a cold Flow.
     * The flow is automatically cancelled when the collector's coroutine is cancelled,
     * which removes the location callback and prevents memory leaks.
     */
    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(
        intervalMs: Long = 5_000L,
        priority: Int = Priority.PRIORITY_HIGH_ACCURACY
    ): Flow<LatLng> = callbackFlow {
        val request = LocationRequest.Builder(priority, intervalMs)
            .setWaitForAccurateLocation(false)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    trySend(LatLng(loc.latitude, loc.longitude))
                }
            }
        }

        client.requestLocationUpdates(request, callback, null)

        // Remove updates when the Flow collector cancels (e.g. screen leaves composition)
        awaitClose { client.removeLocationUpdates(callback) }
    }
}
