package at.spiceburg.roarfit.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.data.entities.UserExercise

/**
 * Dao for [UserExercise] & [ExerciseTemplate]
 */
@Dao
interface ExerciseDao {

    @Insert
    fun insertUserExercise(userExercise: UserExercise)

    @Query("select * from UserExercise")
    fun getAllUserExercises(): List<UserExercise>
}
