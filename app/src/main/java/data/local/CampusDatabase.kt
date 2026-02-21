package data.local

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import data.dao.BuildingDao
import data.dao.ClassScheduleDao
import data.dao.CourseDao
import data.dao.RoomDao
import data.models.Building
import data.models.CampusRoom
import data.models.ClassSchedule
import data.models.Course
import data.models.UserCourse

@Database(
    entities = [
        Building::class,
        CampusRoom::class,
        Course::class,
        ClassSchedule::class,
        UserCourse::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CampusDatabase : RoomDatabase() {
    abstract fun buildingDao(): BuildingDao
    abstract fun roomDao(): RoomDao
    abstract fun courseDao(): CourseDao
    abstract fun scheduleDao(): ClassScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: CampusDatabase? = null

        fun getInstance(context: Context): CampusDatabase {
            return INSTANCE ?: synchronized(this) {
                androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    CampusDatabase::class.java,
                    "findklass_database"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}