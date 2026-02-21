package data.remote

import com.example.findklass.BuildConfig
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val DIRECTIONS_BASE_URL = "https://maps.googleapis.com/maps/api/"

/**
 * Repository that calls the Directions API and decodes the route polyline.
 * Using sealed Result pattern so the ViewModel never needs to catch exceptions.
 */
class DirectionsRepository {

    private val api: DirectionsApiService by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(DIRECTIONS_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DirectionsApiService::class.java)
    }

    /**
     * Fetches a walking route and returns the decoded list of [LatLng] points
     * that can be used directly by the Maps Compose Polyline composable.
     *
     * Returns [RouteResult.NoRoute] when the API returns ZERO_RESULTS
     * (common if the origin and destination are too close or off the road network).
     */
    suspend fun getWalkingRoute(
        origin: LatLng,
        destination: LatLng
    ): RouteResult {
        return try {
            val response = api.getDirections(
                origin = "${origin.latitude},${origin.longitude}",
                destination = "${destination.latitude},${destination.longitude}",
                key = BuildConfig.MAPS_API_KEY
            )

            when {
                response.status == "OK" && response.routes.isNotEmpty() -> {
                    val encoded = response.routes.first().overviewPolyline.points
                    // PolyUtil.decode converts the encoded string → List<LatLng>
                    val points = PolyUtil.decode(encoded)
                    val leg = response.routes.first().legs.firstOrNull()
                    RouteResult.Success(
                        points = points,
                        distanceText = leg?.distance?.text ?: "",
                        durationText = leg?.duration?.text ?: ""
                    )
                }
                response.status == "ZERO_RESULTS" -> RouteResult.NoRoute
                else -> RouteResult.Error("Directions API error: ${response.status}")
            }
        } catch (e: Exception) {
            RouteResult.Error(e.message ?: "Network error")
        }
    }
}

/** Sealed result type so no exception handling is needed in the ViewModel */
sealed class RouteResult {
    data class Success(
        val points: List<LatLng>,
        val distanceText: String,
        val durationText: String
    ) : RouteResult()

    object NoRoute : RouteResult()
    data class Error(val message: String) : RouteResult()
}
