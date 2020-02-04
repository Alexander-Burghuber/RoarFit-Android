package at.spiceburg.roarfit.data.db

import androidx.room.Embedded
import androidx.room.Relation
import at.spiceburg.roarfit.data.db.entities.Workout
import at.spiceburg.roarfit.data.db.entities.WorkoutPlan

data class WorkoutPlanWithWorkouts(
    @Embedded val workoutPlan: WorkoutPlan,
    @Relation(
        parentColumn = "id",
        entityColumn = "workoutPlanId"
    )
    val workouts: List<Workout>
)
