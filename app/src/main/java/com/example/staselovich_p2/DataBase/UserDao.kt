package com.example.staselovich_p2.DataBase

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface UserDao {
    @Query("SELECT * FROM user_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<User>>

    @Query("SELECT * FROM user_table")
    fun getAllMessages(): List<User>?

    @Query("SELECT * FROM user_table WHERE id = :id")
    fun getById(id: Long): User?

    @Update
    fun update(messages: User?)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("DELETE FROM user_table")
    suspend fun deleteAllUsers()

}