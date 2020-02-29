package at.spiceburg.roarfit.data.entities

import java.io.Serializable

data class ExerciseTemplate(
    val id: Int,
    val name: String,
    val equipment: String?,
    val description: String?,
    val videoUrl: String?,
    val bodyParts: List<String>
) : Serializable
