package com.snhu.ProjectTwo.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "goalTable",
    foreignKeys = [
        ForeignKey(
            entity = LoginEntity::class,
            parentColumns = ["_id"],
            childColumns = ["user"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["user"])]
)
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val uid: Long = 0,
    @ColumnInfo(name = "user")
    val user: Long,
    @ColumnInfo(name = "start")
    val start: Float,
    @ColumnInfo(name = "goal")
    val goal: Float,
)
