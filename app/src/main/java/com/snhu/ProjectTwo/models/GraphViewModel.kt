package com.snhu.ProjectTwo.models

import androidx.lifecycle.ViewModel
import com.patrykandpatrick.vico.views.cartesian.data.CartesianChartModelProducer
import com.snhu.ProjectTwo.fragments.GraphFragment
import com.snhu.ProjectTwo.utilities.GoalRow
import com.snhu.ProjectTwo.utilities.LoginDatabase
import com.snhu.ProjectTwo.utilities.UserInfo

class GraphViewModel(userId: Long, database: LoginDatabase) : ViewModel() {

    val _chartModelProducer = CartesianChartModelProducer()

    private lateinit var _weightList: List<UserInfo>
    private lateinit var _weightPoints: List<GraphFragment.WeightPoint>
    private val _userId: Long = userId
    private lateinit var _goalRow: GoalRow
    private val _db = database

    public fun getWeightPoints(){
        _weightList = _db.GetInfoListByUser(_userId)
        _goalRow = _db.GetGoalRow(_userId)
        // O(n) transformation into a dataset that is better for a chart
        _weightPoints = _weightList.map{info ->
            GraphFragment.WeightPoint(info.date, info.weight)
        }
    }


}