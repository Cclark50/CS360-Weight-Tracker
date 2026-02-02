package com.snhu.ProjectTwo.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.snhu.ProjectTwo.entities.GoalEntity

// GoalDao gives us functions to interact with the goals table in our database

@Dao
interface GoalDao {

    // Sets a user's goal by inserting it into the table
    @Insert
    fun SetGoal(goal: GoalEntity): Long

    // gets a user's goal row
    @Query("SELECT * FROM goalTable WHERE _id = :id LIMIT 1")
    fun GetGoalRow(id: Long): GoalEntity?
}