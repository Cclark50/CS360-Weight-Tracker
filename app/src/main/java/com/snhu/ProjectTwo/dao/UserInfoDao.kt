package com.snhu.ProjectTwo.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.snhu.ProjectTwo.entities.UserInfoEntity

@Dao
interface UserInfoDao {
    @Insert
    fun AddNewWeight(info: UserInfoEntity): Long

    @Query("SELECT * FROM userInfo WHERE user = :user ORDER BY date DESC")
    fun GetInfoListByUser(user: Long): List<UserInfoEntity>

    @Query("DELETE FROM userInfo WHERE _id = :id")
    fun RemoveWeightAt(id: Long)

    @Update
    fun ChangeDateAt(info: UserInfoEntity)

    @Query("SELECT * FROM userInfo WHERE _id = :id LIMIT 1")
    fun GetInfoById(id: Long): UserInfoEntity?
}