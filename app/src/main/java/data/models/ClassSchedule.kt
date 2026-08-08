package data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "class_schedules",
    foreignKeys = [
        ForeignKey(
            entity = Course::class,
            parentColumns = ["id"],
            childColumns = ["courseId"]
        ),
        ForeignKey(
            entity = CampusRoom::class,
            parentColumns = ["id"],
            childColumns = ["roomId"]
        )
    ]
)
data class ClassSchedule(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val courseId: String,
    val roomId: String,
    val dayOfWeek: Int, // 1 = Monday, 7 = Sunday
    val startTime: String, // "HH:mm"
    val endTime: String,
    val semester: String
)