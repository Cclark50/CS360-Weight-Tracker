package com.snhu.ProjectTwo.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.snhu.ProjectTwo.entities.LoginEntity

//@Author Christian Clark
//@Date 2-2-26
// LoginDao gives functions to interact with the login table in the database

@Dao
interface LoginDao {
    // adds a new user to the login table
    @Insert
    fun AddNewUser(login: LoginEntity): Long

    // gets a user's login by their id
    @Query("SELECT * FROM login WHERE _id = :id LIMIT 1")
    fun GetUserLogin(id: Long): LoginEntity?

    // gets a user's login by their username
    @Query("SELECT * FROM login WHERE username = :username LIMIT 1")
    fun GetUserLoginByUsername(username: String): LoginEntity?
}