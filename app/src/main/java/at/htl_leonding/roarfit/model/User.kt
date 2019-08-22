package at.htl_leonding.roarfit.model

/**
 * Class which provides a model for user
 * @constructor Sets all properties of the user
 * @property id the unique identifier of the user
 * @property firstName the first name of the user
 * @property lastName the last name of the user
 */
data class User(
    val id: Int,
    val firstName: String,
    val lastName: String
)
