package com.snhu.ProjectTwo.utilities

import android.util.Log
import com.snhu.ProjectTwo.dao.GoalDao
import com.snhu.ProjectTwo.dao.LoginDao
import com.snhu.ProjectTwo.dao.UserInfoDao
import com.snhu.ProjectTwo.entities.GoalEntity
import com.snhu.ProjectTwo.entities.LoginEntity
import com.snhu.ProjectTwo.entities.UserInfoEntity
import org.mindrot.jbcrypt.BCrypt

class DatabaseUtilities() {

    fun ConfirmLogin(username: String, unhashedPassword: String, loginDao: LoginDao): LoginEntity?{
        val login = loginDao.GetUserLoginByUsername(username) ?: return null
        val success = BCrypt.checkpw(unhashedPassword, login.password)
        return if (success) login else null
    }

    fun AddNewUser(username: String, password: String, goalWeight: Float, loginDao: LoginDao): Long{
        if (!goalWeight.isValidWeight()) throw Exception("Goal Weight is not valid");
        val hashedPw = BCrypt.hashpw(password, BCrypt.gensalt(12))
        val login = LoginEntity(
            username = username,
            password = hashedPw,
            goal = goalWeight
        )
        return loginDao.AddNewUser(login)
    }

    fun AddNewWeight(id: Long, date: Long, weight: Float, userInfoDao: UserInfoDao): Long{
        if (!weight.isValidWeight()) throw Exception("Weight is not vaild")
        val entry = UserInfoEntity(
            user = id,
            date = date,
            weight = weight
        )
        return userInfoDao.AddNewWeight(entry)
    }

    fun SetGoal(id: Long, start: Float, goal: Float, goalDao: GoalDao): Long{
        if(!start.isValidWeight() || !goal.isValidWeight()) throw Exception("A weight is invaild")
        val entry = GoalEntity(
            user = id,
            start = start,
            goal = goal
        )
        return goalDao.SetGoal(entry)
    }
}