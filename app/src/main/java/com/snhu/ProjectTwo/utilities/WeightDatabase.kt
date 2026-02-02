package com.snhu.ProjectTwo.utilities

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.snhu.ProjectTwo.dao.GoalDao
import com.snhu.ProjectTwo.dao.LoginDao
import com.snhu.ProjectTwo.dao.UserInfoDao
import com.snhu.ProjectTwo.entities.GoalEntity
import com.snhu.ProjectTwo.entities.LoginEntity
import com.snhu.ProjectTwo.entities.UserInfoEntity

@Database(
    entities = [LoginEntity::class, UserInfoEntity::class, GoalEntity::class],
    version = 2
)
abstract class WeightDatabase: RoomDatabase() {

    abstract fun loginDao(): LoginDao
    abstract fun userInfoDao(): UserInfoDao
    abstract fun goalDao(): GoalDao

    // https://developer.android.com/codelabs/basic-android-kotlin-compose-persisting-data-room#0
    // creating a singleton pattern instance of this database (Google Developers Training Team, 2024)
    // Google Developers Training Team. (May 17, 2024). Persist Data with Room. Android.com. https://developer.android.com/codelabs/basic-android-kotlin-compose-persisting-data-room#0
    companion object{
        @Volatile
        private var INSTANCE: WeightDatabase? = null

        fun getInstance(context: Context): WeightDatabase{
            return INSTANCE ?: synchronized(this){
                Room.databaseBuilder(
                    context.applicationContext,
                    WeightDatabase::class.java,
                    "userLogins.db"
                ).fallbackToDestructiveMigration().build().also {INSTANCE = it}
            }
        }
    }
}