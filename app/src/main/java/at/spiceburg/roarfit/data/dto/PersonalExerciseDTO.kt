package at.spiceburg.roarfit.data.dto

data class PersonalExerciseDTO(
    val templateId: Int,
    val time: String,
    val sets: Int,
    val reps: Int,
    val weight: String?,
    val completedDate: Long?
)
