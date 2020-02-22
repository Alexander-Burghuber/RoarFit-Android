package at.spiceburg.roarfit.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Class which provides a model for an user
 * @property id the unique id of the user
 * @property firstName the first name of the user
 * @property lastName the last name of the user
 */
@Entity
data class UserDB(
    @PrimaryKey val id: Int,
    val firstName: String,
    val lastName: String
)
