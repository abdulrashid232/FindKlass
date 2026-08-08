package data.relations

import androidx.room.Embedded
import data.models.ClassSchedule
import data.models.Course

data class ClassWithDetails(
    @Embedded val classSchedule: ClassSchedule,
    val courseCode: String,
    val courseTitle: String,
    val roomNumber: String,
    val buildingName: String,
    val latitude: Double,
    val longitude: Double
)