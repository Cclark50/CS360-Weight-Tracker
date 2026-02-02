package com.snhu.ProjectTwo.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.snhu.ProjectTwo.R
import com.snhu.ProjectTwo.databinding.GraphFragmentBinding
import com.snhu.ProjectTwo.utilities.UserInfo
import com.snhu.ProjectTwo.activities.CoreApp
import com.snhu.ProjectTwo.entities.GoalEntity
import com.snhu.ProjectTwo.entities.UserInfoEntity
import com.snhu.ProjectTwo.utilities.GoalRow
import com.snhu.ProjectTwo.utilities.LoginDatabase
import com.snhu.ProjectTwo.utilities.WeightDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class GraphFragment: Fragment() {

    data class WeightPoint(
        val date: Long,
        val weight: Float
    )

    private var _binding: GraphFragmentBinding? = null
    private val binding get() = _binding!!

    private val _userId: Long by lazy { (activity as? CoreApp)?.getId() ?: -1L }
    private var _goalRow: GoalEntity? = null

    private lateinit var _weightList: List<UserInfoEntity>
    private lateinit var _weightPoints: List<WeightPoint>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GraphFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch{
            try{
                getWeightPoints()
                createChart()
            }catch (ex: Exception){
                binding.weightChart.setNoDataText("Could not find a start and goal weight.")
                binding.weightChart.setNoDataTextColor(Color.BLACK)
                binding.weightChart.getPaint(Chart.PAINT_INFO).textSize = 48f
                return@launch
            }
        }
    }

    suspend fun getWeightPoints(){
        withContext(Dispatchers.IO){
            val db = WeightDatabase.getInstance(requireContext())
            _weightList = db.userInfoDao().GetInfoListByUser(_userId)
            _goalRow = db.goalDao().GetGoalRow(_userId)
        }
        if(_goalRow == null){
            throw Exception("No goal row exists")
        }
        // O(n) transformation into a dataset that is better for a chart
        _weightPoints = _weightList.map{info ->
            WeightPoint(info.date, info.weight)
        }
    }

    private fun createChart(){

        // if the weights are empty then let the user know
        if(_weightPoints.isEmpty()){
            binding.weightChart.setNoDataText(getString(R.string.no_data_available))
            binding.weightChart.setNoDataTextColor(Color.BLACK)
            binding.weightChart.getPaint(Chart.PAINT_INFO).textSize = 48f
            return
        }

        // Set interactions with the chart
        binding.weightChart.apply {
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
        }

        // O(n) acquisition of the max out of the possible weights
        val max = _weightPoints.reduce { max, it ->
            if(it.weight > max.weight) it else max
        }

        // O(n) acquisition of the minimum of the possible weights
        val min = _weightPoints.reduce { min, it ->
            if(it.weight < min.weight) it else min
        }

        // O(n) transformation of the weights from our data base into an entry list
        // make sure to reverse the list so that when tying dates to indices
        // that it still shows from left to right on the graph
        val entries = _weightPoints.reversed().mapIndexed{ index, point ->
            Entry(
                index.toFloat(),
                point.weight
            )
        }

        val dataSet = LineDataSet(entries, "Weight").apply{
            color = Color.BLUE
            lineWidth = 2f
            setDrawCircles(true)
            circleRadius = 4f
            setDrawValues(true)
        }

        binding.weightChart.data = LineData(dataSet)

        // Xaxis settings
        binding.weightChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setLabelCount(5, true)
        }

        // This method allows me to create the date formatter inside this object that derives
        // From ValueFormatter and has better control over how i show the date
        binding.weightChart.xAxis.valueFormatter = object : ValueFormatter(){
            private val dateFormatter = SimpleDateFormat("MM/dd/yy", Locale.getDefault())
            // because of a bug where if values dates get too far apart we have to
            // map graph points to dates instead of calculating dates directly
            override fun getFormattedValue(value: Float): String? {
                val index = value.toInt()
                // need to reverse the graph one more time
                val reversed = _weightPoints.reversed()
                if(index in reversed.indices){
                    return dateFormatter.format(Date(_weightPoints[index].date))
                }
                return ""
            }
        }



        // Yaxis settings
        binding.weightChart.axisLeft.apply{
            axisMinimum = min.weight - 20
            axisMaximum = max.weight + 20
            valueFormatter = object: ValueFormatter(){
                override fun getFormattedValue(value: Float): String? {
                    return "${value.toInt()} lbs"
                }
            }

            addLimitLine(LimitLine(_goalRow!!.start, "Start").apply {
                lineColor = Color.RED
                lineWidth = 2f
                textColor = Color.RED
                textSize = 15f
                labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
                enableDashedLine(10f, 5f, 0f)
            })

            addLimitLine(LimitLine(_goalRow!!.goal, "Goal").apply {
                lineColor = Color.GREEN
                lineWidth = 2f
                textColor = Color.GREEN
                textSize = 15f
                labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
                enableDashedLine(10f, 5f, 0f)
            })

            setDrawLimitLinesBehindData(true)
        }

        binding.weightChart.invalidate()

    }

}