package data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import data.models.ClassSchedule
import data.relations.ClassWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassScheduleDao {
    @Transaction
    @Query("""
        SELECT cs.*, 
               c.code as courseCode, 
               c.title as courseTitle, 
               r.roomNumber, 
               b.name as buildingName, 
               b.latitude, 
               b.longitude,
               b.id as buildingId
        FROM class_schedules cs
        INNER JOIN courses c ON cs.courseId = c.id
        INNER JOIN rooms r ON cs.roomId = r.id
        INNER JOIN buildings b ON r.buildingId = b.id
        WHERE cs.courseId IN (
            SELECT courseId 
            FROM user_courses 
            WHERE studentId = :studentId
        )
        ORDER BY cs.dayOfWeek, cs.startTime
    """)
    fun getUserSchedule(studentId: String): Flow<List<ClassWithDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSchedules(schedules: List<ClassSchedule>)
}