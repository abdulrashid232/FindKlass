package data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "buildings")
data class Building(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val code: String,
    val latitude: Double,
    val longitude: Double,
    val photoUrl: String? = null,
    val floorPlanUrl: String? = null,
    val address: String = ""
)