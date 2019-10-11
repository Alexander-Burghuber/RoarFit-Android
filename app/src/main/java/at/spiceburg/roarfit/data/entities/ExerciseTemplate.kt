package at.spiceburg.roarfit.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Class which provides a model for a exercise template
 * @property id the unique id of the exercise template
 * @property name the name of the exercise
 * @property equipment the equipment used for this exercise
 * @property bodyPart the body part trained in this exercise
 */
@Entity
data class ExerciseTemplate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val equipment: String?,
    val bodyPart: String
)
