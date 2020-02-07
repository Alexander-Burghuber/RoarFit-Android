package at.spiceburg.roarfit.data.entities

import at.spiceburg.roarfit.data.Equipment
import java.io.Serializable

data class ExerciseTemplate(
    val id: Int,
    val name: String,
    val equipment: Equipment?,
    val bodyPart: String
) : Serializable
