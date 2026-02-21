package data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import data.models.Building
import kotlinx.coroutines.flow.Flow

@Dao
interface BuildingDao {
    @Query("SELECT * FROM buildings ORDER BY name")
    fun getAllBuildings(): Flow<List<Building>>

    @Query("SELECT * FROM buildings WHERE id = :buildingId")
    fun getBuildingById(buildingId: String): Flow<Building>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuilding(building: Building)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllBuildings(buildings: List<Building>)

    @Query("SELECT * FROM buildings WHERE name LIKE '%' || :query || '%' OR code LIKE '%' || :query || '%'")
    fun searchBuildings(query: String): Flow<List<Building>>
}