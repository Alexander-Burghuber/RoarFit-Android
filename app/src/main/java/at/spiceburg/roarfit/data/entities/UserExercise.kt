package at.spiceburg.roarfit.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Class which provides a model for an exercise of an user
 * @property id the unique id of the user exercise
 * @property templateId the id of the [ExerciseTemplate]
 * @property sets the amount of sets
 * @property reps the amount of reps
 * @property weight the amount weight in kg
 * @property completed if the exercise has been completed
 * @property groupId an id for a list of exercises that indicates which exercises belong together.
 */
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
)
