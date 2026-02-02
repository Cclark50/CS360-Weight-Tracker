package com.snhu.ProjectTwo.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LoginDao {
    @Insert
    fun AddNewUser(username: String, password: String, goalWeight: Float)

    @Query("SELECT * FROM login WHERE _id = :id LIMIT 1")
    fun GetUserLogin(id: Long)
}