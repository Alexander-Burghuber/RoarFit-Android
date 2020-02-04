package at.spiceburg.roarfit.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Class which provides a model for an exercise of an user
 * @property id the unique id of the user exercise
 * @property templateId the id of the [ExerciseTemplate]
 * @property workoutId the id of the [Workout]
 * @property sets the amount of sets
 * @property reps the amount of reps
 * @property groupId an id for a list of exercises that indicates which exercises belong together.
 * @property completed if the exercise has been completed
 * @property weight the amount weight in kg
 */
@Entity(
    foreignKeys = [ForeignKey(
        entity = ExerciseTemplate::class,
        parentColumns = ["id"],
        childColumns = ["templateId"]
    ), ForeignKey(
        entity = Workout::class,
        parentColumns = ["id"],
        childColumns = ["workoutId"]
    )]
)
data class UserExercise(
    @PrimaryKey val id: Int,
    val templateId: Int,
    val workoutId: Int,
    val sets: Int,
    val reps: Int,
    val groupId: Int,
    val completed: Boolean = false,
    val weight: Int? = null
)
