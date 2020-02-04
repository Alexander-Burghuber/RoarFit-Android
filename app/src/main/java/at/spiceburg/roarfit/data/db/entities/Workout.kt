package at.spiceburg.roarfit.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Class which provides a model for a workout
 * @property id the unique id of the workout
 * @property workoutPlanId the id of the [WorkoutPlan]
 * @property day to which day of the [WorkoutPlan] this workout belongs to
 */
@Entity(
    foreignKeys = [ForeignKey(
        entity = WorkoutPlan::class,
        parentColumns = ["id"],
        childColumns = ["workoutPlanId"]
    )]
)
data class Workout(
    @PrimaryKey val id: Int,
    val workoutPlanId: Int,
    val day: Int
)
