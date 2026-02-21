package data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import data.models.Course
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY code")
    fun getAllCourses(): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE code LIKE '%' || :query || '%' OR title LIKE '%' || :query || '%'")
    fun searchCourses(query: String): Flow<List<Course>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCourses(courses: List<Course>)

    @Query("SELECT * FROM courses WHERE id = :courseId")
    fun getCourseById(courseId: String): Flow<Course>
}