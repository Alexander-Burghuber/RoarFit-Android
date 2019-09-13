package at.htl_leonding.roarfit.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import at.htl_leonding.roarfit.data.entities.User

@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: User)

    @Query("select * from user where id = :userId")
    suspend fun getUser(userId: Int): User?
}