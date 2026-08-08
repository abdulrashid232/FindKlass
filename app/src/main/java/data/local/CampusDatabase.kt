package data.local

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import data.SampleDataProvider
import data.dao.BuildingDao
import data.dao.ClassScheduleDao
import data.dao.CourseDao
import data.dao.RoomDao
import data.dao.UserCourseDao
import data.models.Building
import data.models.CampusRoom
import data.models.ClassSchedule
import data.models.Course
import data.models.UserCourse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    abstract fun userCourseDao(): UserCourseDao

    companion object {
        @Volatile
        private var INSTANCE: CampusDatabase? = null

        fun getInstance(context: Context): CampusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    CampusDatabase::class.java,
                    "findklass_database"
                )
                    .addCallback(object : Callback() {
                        // Pre-populate with sample data the first time the DB is created
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    val buildings = SampleDataProvider.getSampleBuildings()
                                    val rooms = SampleDataProvider.getSampleRooms()
                                    val courses = SampleDataProvider.getSampleCourses()
                                    val schedules = SampleDataProvider.getSampleSchedules()
                                    val userCourses = SampleDataProvider.getSampleUserCourses()

                                    database.buildingDao().insertAllBuildings(buildings)
                                    database.roomDao().insertAllRooms(rooms)
                                    database.courseDao().insertAllCourses(courses)
                                    database.scheduleDao().insertAllSchedules(schedules)
                                    database.userCourseDao().insertAllUserCourses(userCourses)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}