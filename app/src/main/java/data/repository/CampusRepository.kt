package data.repository

import data.dao.BuildingDao
import data.dao.ClassScheduleDao
import data.dao.CourseDao
import data.dao.RoomDao
import data.models.Building
import data.models.CampusRoom
import data.models.Course
import data.relations.ClassWithDetails
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for all campus data.
 * The ViewModel layer talks only to this repository, never directly to DAOs.
 */
class CampusRepository(
    private val buildingDao: BuildingDao,
    private val roomDao: RoomDao,
    private val courseDao: CourseDao,
    private val scheduleDao: ClassScheduleDao
) {
    // ---- Buildings ----
    fun getAllBuildings(): Flow<List<Building>> = buildingDao.getAllBuildings()

    fun searchBuildings(query: String): Flow<List<Building>> =
        buildingDao.searchBuildings(query)

    // ---- Rooms ----
    fun searchRooms(query: String): Flow<List<CampusRoom>> =
        roomDao.searchRooms(query)

    fun getRoomsByBuilding(buildingId: String): Flow<List<CampusRoom>> =
        roomDao.getRoomsByBuilding(buildingId)

    // ---- Courses ----
    fun searchCourses(query: String): Flow<List<Course>> =
        courseDao.searchCourses(query)

    fun getAllCourses(): Flow<List<Course>> = courseDao.getAllCourses()

    // ---- Schedule ----
    fun getUserSchedule(studentId: String): Flow<List<ClassWithDetails>> =
        scheduleDao.getUserSchedule(studentId)
}
