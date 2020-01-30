package at.spiceburg.roarfit.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import at.spiceburg.roarfit.data.entities.WorkoutPlan
import io.reactivex.Completable

@Dao
interface WorkoutPlanDao {

    @Query("select * from workoutplan where userId = :userId")
    fun getWorkoutPlans(userId: Int): LiveData<Array<WorkoutPlan>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWorkoutPlans(workoutPlans: Array<WorkoutPlan>): Completable
}