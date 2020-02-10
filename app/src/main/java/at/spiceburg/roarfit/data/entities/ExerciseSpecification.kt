package at.spiceburg.roarfit.data.entities

data class ExerciseSpecification(
    val id: Int,
    val reps: String,
    val sets: String,
    val weight: String?,
    val info: String?,
    val exercise: Exercise,
    val completed: Boolean
)
