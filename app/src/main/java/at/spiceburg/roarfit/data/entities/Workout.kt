package at.spiceburg.roarfit.data.entities

data class Workout(
    val id: Int,
    val day: Int,
    var userExercises: List<UserExercise> = emptyList()
)
