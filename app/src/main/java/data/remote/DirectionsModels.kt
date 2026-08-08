package data.remote

import com.google.gson.annotations.SerializedName

// ----- Top-level response -----

data class DirectionsResponse(
    val status: String,           // "OK", "ZERO_RESULTS", "NOT_FOUND", etc.
    val routes: List<Route> = emptyList()
)

// ----- Route -----

data class Route(
    @SerializedName("overview_polyline") val overviewPolyline: OverviewPolyline,
    val legs: List<Leg> = emptyList()
)

/** Encoded polyline string for the whole route (cheapest to decode client-side) */
data class OverviewPolyline(
    val points: String           // e.g. "a_lnFnzjHxD_@..."
)

// ----- Leg (one origin→destination segment) -----

data class Leg(
    val distance: TextValue,
    val duration: TextValue,
    @SerializedName("start_address") val startAddress: String = "",
    @SerializedName("end_address") val endAddress: String = ""
)

data class TextValue(
    val text: String,            // "0.4 km"
    val value: Int               // metres
)
