package data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "user_courses",
    foreignKeys = [ForeignKey(
        entity = Course::class,
        parentColumns = ["id"],
        childColumns = ["courseId"]
    )]
)
data class UserCourse(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val studentId: String,
    val courseId: String
)