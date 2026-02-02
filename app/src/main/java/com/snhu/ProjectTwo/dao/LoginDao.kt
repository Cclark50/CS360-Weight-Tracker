package com.snhu.ProjectTwo.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.snhu.ProjectTwo.entities.LoginEntity

@Dao
interface LoginDao {
    @Insert
    fun AddNewUser(login: LoginEntity): Long

    @Query("SELECT * FROM login WHERE _id = :id LIMIT 1")
    fun GetUserLogin(id: Long): LoginEntity?

    @Query("SELECT * FROM login WHERE username = :username LIMIT 1")
    fun GetUserLoginByUsername(username: String): LoginEntity?
}