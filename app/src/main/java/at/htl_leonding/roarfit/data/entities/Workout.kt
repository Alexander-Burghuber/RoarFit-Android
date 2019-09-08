package at.htl_leonding.roarfit.data.entities

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
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val workoutPlanId: Int,
    val day: Int
)