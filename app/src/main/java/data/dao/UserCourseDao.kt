package data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import data.models.UserCourse
import kotlinx.coroutines.flow.Flow

@Dao
interface UserCourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllUserCourses(userCourses: List<UserCourse>)

    @Query("SELECT * FROM user_courses WHERE studentId = :studentId")
    fun getCoursesByStudent(studentId: String): Flow<List<UserCourse>>

    @Query("DELETE FROM user_courses WHERE studentId = :studentId")
    suspend fun deleteByStudent(studentId: String)
}
