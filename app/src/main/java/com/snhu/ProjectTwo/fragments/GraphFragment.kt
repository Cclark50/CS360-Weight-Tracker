package com.snhu.ProjectTwo.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
import com.snhu.ProjectTwo.utilities.GoalRow
import com.snhu.ProjectTwo.utilities.LoginDatabase
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
    private lateinit var _goalRow: GoalRow

    private lateinit var _weightList: List<UserInfo>
    private lateinit var _weightPoints: List<WeightPoint>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GraphFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getWeightPoints()
        val sb = StringBuilder()
        for (weight: WeightPoint in _weightPoints){
            val str: String = "${weight.date}, ${weight.weight}\n"
            sb.append(str)
        }
        Log.d("GRAPH FRAGMENT", "weights: " + sb.toString())
        createChart()
    }

    fun getWeightPoints(){
        val db = LoginDatabase(requireContext())
        _weightList = db.GetInfoListByUser(_userId)
        _goalRow = db.GetGoalRow(_userId)
        // O(n) transformation into a dataset that is better for a chart
        _weightPoints = _weightList.map{info ->
            WeightPoint(info.date, info.weight)
        }
    }

    private fun createChart(){

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
        val entries = _weightPoints.map{ point ->
            Entry(
                (point.date / (1000 * 60 * 60 * 24)).toFloat(),
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
            override fun getFormattedValue(value: Float): String? {
                val millis = (value * 1000 * 60 * 60 * 24).toLong()
                return dateFormatter.format(Date(millis))
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

            addLimitLine(LimitLine(_goalRow.start, "Start").apply {
                lineColor = Color.RED
                lineWidth = 2f
                textColor = Color.RED
                textSize = 15f
                labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
                enableDashedLine(10f, 5f, 0f)
            })

            addLimitLine(LimitLine(_goalRow.goal, "Goal").apply {
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