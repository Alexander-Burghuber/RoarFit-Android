package at.htl_leonding.roarfit.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import at.htl_leonding.roarfit.data.entities.ExerciseTemplate
import at.htl_leonding.roarfit.data.entities.UserExercise

@Database(entities = [UserExercise::class, ExerciseTemplate::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao

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
                    "roarfit_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}