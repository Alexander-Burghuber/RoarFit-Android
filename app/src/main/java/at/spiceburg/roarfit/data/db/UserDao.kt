package at.spiceburg.roarfit.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import at.spiceburg.roarfit.data.entities.User
import io.reactivex.Completable

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User): Completable

    @Query("select * from user where id = :userId")
    fun getUser(userId: Int): LiveData<User?>
}
