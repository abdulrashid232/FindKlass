package data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import data.models.CampusRoom
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {
    @Query("SELECT * FROM rooms WHERE buildingId = :buildingId")
    fun getRoomsByBuilding(buildingId: String): Flow<List<CampusRoom>>

    @Query("SELECT * FROM rooms WHERE roomNumber LIKE '%' || :query || '%'")
    fun searchRooms(query: String): Flow<List<CampusRoom>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRooms(rooms: List<CampusRoom>)
}