package at.htlleonding.roarfit.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import at.htlleonding.roarfit.data.entities.User

@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: User)

    @Query("select * from user where id = :userId")
    suspend fun getUser(userId: Int): User?
}