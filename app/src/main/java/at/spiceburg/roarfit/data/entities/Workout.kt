package at.spiceburg.roarfit.data.entities

data class Workout(
    val id: Int,
    val day: Int,
    val week: Int,
    val info: String?,
    val specifications: List<ExerciseSpecification>
)
