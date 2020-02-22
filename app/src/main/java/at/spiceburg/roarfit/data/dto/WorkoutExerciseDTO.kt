package at.spiceburg.roarfit.data.dto

data class WorkoutExerciseDTO(
    val exerciseId: Int,
    val time: String,
    val sets: Int,
    val reps: Int,
    val weight: String?
)
