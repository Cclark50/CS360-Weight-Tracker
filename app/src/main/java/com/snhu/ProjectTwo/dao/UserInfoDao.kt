package com.snhu.ProjectTwo.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.snhu.ProjectTwo.entities.UserInfoEntity

// UserInfoDao gives us functions to interact with the userInfo table in the database

@Dao
interface UserInfoDao {
    // adds a weight to the table
    @Insert
    fun AddNewWeight(info: UserInfoEntity): Long

    // gets all weights of a user
    @Query("SELECT * FROM userInfo WHERE user = :user ORDER BY date DESC")
    fun GetInfoListByUser(user: Long): List<UserInfoEntity>

    //removes a weight at a certain id
    @Query("DELETE FROM userInfo WHERE _id = :id")
    fun RemoveWeightAt(id: Long)

    // changes the date of a certain weight
    @Update
    fun ChangeDateAt(info: UserInfoEntity)

    // gets the weight at a certain id
    @Query("SELECT * FROM userInfo WHERE _id = :id LIMIT 1")
    fun GetInfoById(id: Long): UserInfoEntity?

    // gets the latest weight of a user
    @Query("SELECT * FROM userInfo WHERE user = :user ORDER BY date DESC")
    fun GetLatestWeight(user: Long): UserInfoEntity?
}