package com.snhu.ProjectTwo.utilities

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class UserInfo(val id: Long, val user: Long, val date: Long, val weight: Float){
    fun getDateString(): String{
        return Instant.ofEpochMilli(date)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("MM/dd/yyyy\nhh:mm"))
    }
}
