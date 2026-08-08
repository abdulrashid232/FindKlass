package data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for the Google Maps Directions API.
 * Base URL: https://maps.googleapis.com/maps/api/
 */
interface DirectionsApiService {

    /**
     * Fetches walking directions between two points.
     *
     * @param origin      "lat,lng" string for the user's location
     * @param destination "lat,lng" string for the target building
     * @param mode        Travel mode — always "walking" for campus nav
     * @param key         Google Maps API key from BuildConfig
     */
    @GET("directions/json")
    suspend fun getDirections(
        @Query("origin")      origin: String,
        @Query("destination") destination: String,
        @Query("mode")        mode: String = "walking",
        @Query("key")         key: String
    ): DirectionsResponse
}
