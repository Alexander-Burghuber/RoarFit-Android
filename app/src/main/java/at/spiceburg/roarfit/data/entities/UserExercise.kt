package at.spiceburg.roarfit.data.entities

data class UserExercise(
    val id: Int,
    val template: ExerciseTemplate,
    val sets: Int,
    val reps: Int,
    val groupId: Int,
    val completed: Boolean = false,
    val weight: Int? = null
)
