package at.htl_leonding.roarfit.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ExerciseTemplate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val equipment: String?,
    val bodyPart: String
) {
    override fun toString(): String {
        return "ExerciseTemplate(id=$id, name='$name', equipment=$equipment, bodyPart='$bodyPart')"
    }
}