package com.snhu.ProjectTwo.utilities


//@Author Christian Clark
//@Date 1-9-26

/* Data class that holds a row of the user goal table
*/

data class GoalRow(
    val id: Long,
    val user: Long,
    val start: Float,
    val goal: Float
)
