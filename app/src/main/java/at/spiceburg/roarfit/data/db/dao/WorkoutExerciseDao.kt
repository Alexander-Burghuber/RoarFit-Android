package at.spiceburg.roarfit.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import at.spiceburg.roarfit.data.Equipment
import at.spiceburg.roarfit.data.db.WorkoutPlanWithWorkouts
import at.spiceburg.roarfit.data.db.entities.ExerciseTemplate
import at.spiceburg.roarfit.data.db.entities.UserExercise
import at.spiceburg.roarfit.data.db.entities.Workout
import at.spiceburg.roarfit.data.db.entities.WorkoutPlan
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface WorkoutExerciseDao {

    /* ExerciseTemplate */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTemplates(exerciseTemplates: List<ExerciseTemplate>): Completable

    @Query("select * from ExerciseTemplate where equipment = :equipment")
    fun getTemplates(equipment: Equipment): Single<List<ExerciseTemplate>>

    @Query("select * from ExerciseTemplate")
    fun getAllTemplates(): Single<Array<ExerciseTemplate>>

    /* WorkoutPlan */
    @Query("select * from workoutplan where userId = :userId")
    fun getWorkoutPlans(userId: Int): LiveData<Array<WorkoutPlan>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWorkoutPlan(workoutPlans: WorkoutPlan): Completable

    /* Workout */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWorkouts(workouts: Array<Workout>): Completable

    /* UserExercise */
    @Insert
    fun insertUserExercise(userExercise: UserExercise)

    @Query("select * from UserExercise")
    fun getAllUserExercises(): List<UserExercise>

    /* Combined */
    @Transaction
    @Query("select * from workoutplan where userId = :userId")
    fun getWorkoutPlanWithWorkouts(userId: Int): LiveData<WorkoutPlanWithWorkouts?>
}
