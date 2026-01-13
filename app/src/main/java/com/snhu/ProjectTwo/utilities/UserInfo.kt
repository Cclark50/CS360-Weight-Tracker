package com.snhu.ProjectTwo.utilities

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

//@Author Christian Clark
//@Date 1-9-26

/* Data class that contains the information of one row of the weights table
** Holds a date, weight, and the user it belongs to
*/

data class UserInfo(val id: Long, val user: Long, val date: Long, val weight: Float){

    //gets the date as a formatted as a string
    fun getDateString(): String{
        return Instant.ofEpochMilli(date)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("MM/dd/yyyy\nhh:mm"))
    }
}
