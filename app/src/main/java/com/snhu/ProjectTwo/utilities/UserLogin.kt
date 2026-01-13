package com.snhu.ProjectTwo.utilities

//@Author Christian Clark
//@Date 1-9-26

/* Data class to hold the data for a user's login data
** Does not include the password security
*/

data class UserLogin(val id: Long, val username: String, val goal: Float)
