package at.spiceburg.roarfit.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable

@Dao
interface Dao {

    @Query("select * from UserDB where id = :userId")
    fun getUser(userId: Int): LiveData<UserDB>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserDB): Completable
}
