package com.snhu.ProjectTwo.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "userInfo",
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
data class UserInfoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val uid: Long = 0,
    @ColumnInfo(name = "user")
    val user: Long,
    @ColumnInfo(name = "date")
    val date: Long,
    @ColumnInfo(name = "weight")
    val weight: Float
)
