package com.snhu.ProjectTwo.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.snhu.ProjectTwo.entities.GoalEntity
import com.snhu.ProjectTwo.utilities.GoalRow

@Dao
interface GoalDao {
    @Insert
    fun SetGoal(goal: GoalEntity): Long

    @Query("SELECT * FROM goalTable WHERE _id = :id LIMIT 1")
    fun GetGoalRow(id: Long): GoalEntity?
}