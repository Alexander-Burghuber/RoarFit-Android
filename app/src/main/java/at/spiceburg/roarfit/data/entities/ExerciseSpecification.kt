package at.spiceburg.roarfit.data.entities

import java.io.Serializable

data class ExerciseSpecification(
    val id: Int,
    val reps: Int,
    val sets: Int,
    val weight: Float,
    val info: String?,
    val exercise: Exercise
) : Serializable
