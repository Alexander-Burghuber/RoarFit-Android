package at.htl_leonding.roarfit.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = ExerciseTemplate::class,
        parentColumns = ["id"],
        childColumns = ["templateId"]
    )]
)
data class UserExercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val templateId: Int,
    val sets: Int,
    val reps: Int,
    val weight: Int? = null,
    val completed: Boolean = false,
    val groupId: Int
) {
    override fun toString(): String {
        return "UserExercise(id=$id, templateId=$templateId, sets=$sets, reps=$reps, weight=$weight, completed=$completed, groupId=$groupId)"
    }
}