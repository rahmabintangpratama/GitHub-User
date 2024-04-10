package com.dicoding.githubuser.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(favorite: Favorite)

    @Delete
    fun delete(favorite: Favorite)

    @Query("SELECT * from favorite ORDER BY id ASC")
    fun getAllFavorite(): LiveData<List<Favorite>>

    @Query("SELECT EXISTS (SELECT 1 FROM favorite WHERE login = :login)")
    fun isFavorite(login: String): LiveData<Boolean>
}