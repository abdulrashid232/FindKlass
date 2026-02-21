package data

import data.models.*

object SampleDataProvider {
    fun getSampleBuildings(): List<Building> {
        return listOf(
            Building(
                id = "b1",
                name = "Science Building",
                code = "SCI",
                latitude = 40.7128,
                longitude = -74.0060,
                address = "123 Science Ave"
            ),
            Building(
                id = "b2",
                name = "Engineering Hall",
                code = "ENG",
                latitude = 40.7138,
                longitude = -74.0050,
                address = "456 Engineering Blvd"
            ),
            Building(
                id = "b3",
                name = "Library",
                code = "LIB",
                latitude = 40.7118,
                longitude = -74.0070,
                address = "789 Knowledge St"
            )
        )
    }

    fun getSampleRooms(): List<CampusRoom> {
        return listOf(
            CampusRoom(
                id = "r1",
                roomNumber = "101",
                floor = 1,
                buildingId = "b1"
            ),
            CampusRoom(
                id = "r2",
                roomNumber = "201",
                floor = 2,
                buildingId = "b1"
            ),
            CampusRoom(
                id = "r3",
                roomNumber = "301",
                floor = 3,
                buildingId = "b2"
            )
        )
    }

    fun getSampleCourses(): List<Course> {
        return listOf(
            Course(
                id = "c1",
                code = "CS101",
                title = "Introduction to Programming",
                credits = 3,
                instructor = "Dr. Smith"
            ),
            Course(
                id = "c2",
                code = "MATH201",
                title = "Calculus II",
                credits = 4,
                instructor = "Prof. Johnson"
            )
        )
    }
}