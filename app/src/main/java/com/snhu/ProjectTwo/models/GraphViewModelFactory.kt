package com.snhu.ProjectTwo.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.snhu.ProjectTwo.utilities.LoginDatabase

class GraphViewModelFactory(val _userId: Long, val loginDatabase: LoginDatabase): ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T{
        @Suppress("UNCHECKED_CAST")
        return GraphViewModel(_userId, loginDatabase) as T
    }

}