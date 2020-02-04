package at.spiceburg.roarfit.data.db

import androidx.room.Embedded
import androidx.room.Relation
import at.spiceburg.roarfit.data.db.entities.UserExercise
import at.spiceburg.roarfit.data.db.entities.Workout

data class WorkoutWithUserExercises(
    @Embedded val workout: Workout,
    @Relation(
        parentColumn = "id",
        entityColumn = "workoutId"
    )
    val userExercises: List<UserExercise>
)
