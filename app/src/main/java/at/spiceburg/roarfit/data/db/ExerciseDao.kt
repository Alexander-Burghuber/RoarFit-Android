package at.spiceburg.roarfit.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.data.entities.UserExercise

@Dao
interface ExerciseDao {

    @Insert
    suspend fun insertUserExercise(userExercise: UserExercise)

    @Query("select * from UserExercise")
    suspend fun getAllUserExercises(): List<UserExercise>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTemplates(exerciseTemplates: List<ExerciseTemplate>)

    @Query("select * from ExerciseTemplate")
    suspend fun getAllTemplates(): List<ExerciseTemplate>
}