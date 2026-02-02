package com.snhu.ProjectTwo.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

//@Author Christian Clark
//@Date 2-2-26
// UserInfoEntity gives us the schema for the userinfo table
// Also provides a data class we can use to reference a row of that table

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
){

    fun getDateString(): String{
        return Instant.ofEpochMilli(date)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("MM/dd/yyyy\nhh:mm"))
    }
}
