package data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "rooms",
    foreignKeys = [ForeignKey(
        entity = Building::class,
        parentColumns = ["id"],
        childColumns = ["buildingId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class CampusRoom(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val roomNumber: String,
    val floor: Int,
    val buildingId: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)