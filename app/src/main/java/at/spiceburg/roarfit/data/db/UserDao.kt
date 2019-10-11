package at.spiceburg.roarfit.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import at.spiceburg.roarfit.data.entities.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("select * from user where id = :userId")
    suspend fun getUser(userId: Int): User?
}
