package com.snhu.ProjectTwo.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//@Author Christian Clark
//@Date 2-2-26
// LoginEntity gives us the schema for the login table
// Also provides a data class we can use to reference a row of that table

@Entity(
    tableName = "login"
)
data class LoginEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val uid: Long = 0,
    @ColumnInfo(name = "username")
    val username: String,
    @ColumnInfo(name = "password")
    val password: String,
    @ColumnInfo(name = "goal")
    val goal: Float
)
