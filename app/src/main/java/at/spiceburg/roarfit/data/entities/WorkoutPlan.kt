package at.spiceburg.roarfit.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import at.spiceburg.roarfit.data.Period

/**
 * Class which provides a model for a workout plan
 * @property id the unique id of the workout plan
 * @property userId the id of the [User]
 * @property name the name of the workout plan
 * @property warmup the recommended warmup time for this workout plan
 * @property cooldown the recommended cooldown time for this workout plan
 */
@Entity(
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"]
    )]
)
data class WorkoutPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val name: String,
    val warmup: Period,
    val cooldown: Period
)