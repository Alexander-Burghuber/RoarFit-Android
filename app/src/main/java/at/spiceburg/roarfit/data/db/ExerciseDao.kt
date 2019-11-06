package at.spiceburg.roarfit.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import at.spiceburg.roarfit.data.Equipment
import at.spiceburg.roarfit.data.entities.ExerciseTemplate
import at.spiceburg.roarfit.data.entities.UserExercise
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface ExerciseDao {

    @Insert
    fun insertUserExercise(userExercise: UserExercise)

    @Query("select * from UserExercise")
    fun getAllUserExercises(): List<UserExercise>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTemplates(exerciseTemplates: List<ExerciseTemplate>): Completable

    @Query("select * from ExerciseTemplate where equipment = :equipment")
    fun getTemplates(equipment: Equipment): Single<List<ExerciseTemplate>>

    @Query("select * from ExerciseTemplate")
    fun getAllTemplates(): Single<List<ExerciseTemplate>>
}
