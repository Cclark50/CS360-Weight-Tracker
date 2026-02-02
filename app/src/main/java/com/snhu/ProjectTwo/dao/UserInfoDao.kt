package com.snhu.ProjectTwo.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserInfoDao {
    @Insert
    fun AddNewWeight(id: Long, date: Long, weight: Float)

    @Query("SELECT * FROM userInfo WHERE user = :user")
    fun GetInfoListByUser(user: Long)

    @Delete
    fun RemoveWeightAt(id: Long)

    @Update
    fun ChangeDateAt(id: Long, user: Long, date: Long)
}