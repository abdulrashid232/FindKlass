package data

import data.models.Building
import data.models.CampusRoom
import data.models.ClassSchedule
import data.models.Course
import data.models.UserCourse

object SampleDataProvider {

    // Hardcoded student ID for demo purposes
    const val DEMO_STUDENT_ID = "student_001"

    fun getSampleBuildings(): List<Building> {
        return listOf(
            Building(
                id = "b1", name = "Science Building", code = "SCI",
                latitude = 40.7128, longitude = -74.0060, address = "123 Science Ave"
            ),
            Building(
                id = "b2", name = "Engineering Hall", code = "ENG",
                latitude = 40.7138, longitude = -74.0050, address = "456 Engineering Blvd"
            ),
            Building(
                id = "b3", name = "Library", code = "LIB",
                latitude = 40.7118, longitude = -74.0070, address = "789 Knowledge St"
            ),
            Building(
                id = "b4", name = "Arts & Humanities", code = "ART",
                latitude = 40.7145, longitude = -74.0045, address = "10 Arts Lane"
            )
        )
    }

    fun getSampleRooms(): List<CampusRoom> {
        return listOf(
            CampusRoom(id = "r1", roomNumber = "101", floor = 1, buildingId = "b1"),
            CampusRoom(id = "r2", roomNumber = "201", floor = 2, buildingId = "b1"),
            CampusRoom(id = "r3", roomNumber = "301", floor = 3, buildingId = "b2"),
            CampusRoom(id = "r4", roomNumber = "Lab A", floor = 1, buildingId = "b2"),
            CampusRoom(id = "r5", roomNumber = "Lecture Hall 1", floor = 0, buildingId = "b3")
        )
    }

    fun getSampleCourses(): List<Course> {
        return listOf(
            Course(id = "c1", code = "CS101", title = "Intro to Programming", credits = 3, instructor = "Dr. Smith"),
            Course(id = "c2", code = "MATH201", title = "Calculus II", credits = 4, instructor = "Prof. Johnson"),
            Course(id = "c3", code = "ENG301", title = "Algorithms", credits = 3, instructor = "Dr. Brown"),
            Course(id = "c4", code = "PHY101", title = "Physics I", credits = 4, instructor = "Prof. Davis")
        )
    }

    fun getSampleSchedules(): List<ClassSchedule> {
        return listOf(
            ClassSchedule(id = "s1", courseId = "c1", roomId = "r1", dayOfWeek = 1, startTime = "08:00", endTime = "09:30", semester = "Spring 2025"),
            ClassSchedule(id = "s2", courseId = "c2", roomId = "r2", dayOfWeek = 2, startTime = "10:00", endTime = "11:30", semester = "Spring 2025"),
            ClassSchedule(id = "s3", courseId = "c3", roomId = "r3", dayOfWeek = 3, startTime = "13:00", endTime = "14:30", semester = "Spring 2025"),
            ClassSchedule(id = "s4", courseId = "c4", roomId = "r4", dayOfWeek = 4, startTime = "09:00", endTime = "10:30", semester = "Spring 2025"),
            ClassSchedule(id = "s5", courseId = "c1", roomId = "r5", dayOfWeek = 5, startTime = "15:00", endTime = "16:30", semester = "Spring 2025")
        )
    }

    fun getSampleUserCourses(): List<UserCourse> {
        return listOf(
            UserCourse(id = "uc1", studentId = DEMO_STUDENT_ID, courseId = "c1"),
            UserCourse(id = "uc2", studentId = DEMO_STUDENT_ID, courseId = "c2"),
            UserCourse(id = "uc3", studentId = DEMO_STUDENT_ID, courseId = "c3"),
            UserCourse(id = "uc4", studentId = DEMO_STUDENT_ID, courseId = "c4")
        )
    }
}