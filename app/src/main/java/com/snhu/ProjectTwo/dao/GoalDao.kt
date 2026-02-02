package com.snhu.ProjectTwo.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.snhu.ProjectTwo.utilities.GoalRow

@Dao
interface GoalDao {
    @Insert
    fun SetGoal(id: Long, start: Float, goal: Float)

    @Query("SELECT * FROM goalTable WHERE _id = :id LIMIT 1")
    fun GetGoalRow(id: Long)
}