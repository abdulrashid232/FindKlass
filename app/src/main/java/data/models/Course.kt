package data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val code: String,
    val title: String,
    val credits: Int? = null,
    val instructor: String? = null
)