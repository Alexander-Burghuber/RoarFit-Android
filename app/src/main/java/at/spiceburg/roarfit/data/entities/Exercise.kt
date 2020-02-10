package at.spiceburg.roarfit.data.entities

data class Exercise(
    val id: Int,
    val template: ExerciseTemplate,
    val reps: Int,
    val sets: Int,
    val weight: String?,
    val time: String
)
