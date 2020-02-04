package at.spiceburg.roarfit.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import at.spiceburg.roarfit.data.db.dao.UserDao
import at.spiceburg.roarfit.data.db.dao.WorkoutExerciseDao
import at.spiceburg.roarfit.data.db.entities.*
import at.spiceburg.roarfit.utils.Constants

@Database(
    entities = [User::class, WorkoutPlan::class, Workout::class, UserExercise::class, ExerciseTemplate::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun workoutExerciseDao(): WorkoutExerciseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) return tempInstance
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    Constants.DB_NAME
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
