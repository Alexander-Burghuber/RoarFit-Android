package at.spiceburg.roarfit.data.entities

import java.io.Serializable

data class Exercise(
    val id: Int,
    val template: ExerciseTemplate,
    val reps: Int,
    val sets: Int,
    val weight: String?,
    val time: String,
    val completedDate: Long?
) : Serializable
