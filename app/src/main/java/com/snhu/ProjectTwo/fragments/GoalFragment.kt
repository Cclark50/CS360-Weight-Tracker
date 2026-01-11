package com.snhu.ProjectTwo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.snhu.ProjectTwo.R
import com.snhu.ProjectTwo.activities.CoreApp
import com.snhu.ProjectTwo.utilities.LoginDatabase

class GoalFragment : Fragment(){

    private val _userId: Long by lazy {(activity as? CoreApp)?.getId() ?: -1L}
    private var _goalWeight: Float = 0f
    private var _currWeight: Float = 0f
    private var _startWeight: Float = 0f

    private val _goalText: TextView? by lazy {view?.findViewById<TextView>(R.id.goal_weight_show)}
    private val _currText: TextView? by lazy {view?.findViewById<TextView>(R.id.curr_weight_show)}
    private val _progressBar: ProgressBar? by lazy {view?.findViewById<ProgressBar>(R.id.progressBar)}
    private val _progressText: TextView? by lazy {view?.findViewById<TextView>(R.id.progress_text)}
    private var _progressEnd: Float = 0f
    private var _currProgress: Float = 0f


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceData: Bundle?): View {
        return inflater.inflate(R.layout.goal_fragment, container, false);
    }

    override fun onViewCreated(view: View, savedInstanceData: Bundle?){
        super.onViewCreated(view, savedInstanceData)
        val db = LoginDatabase(context)
        try{
            val goals = db.GetGoalRow(_userId)
            _goalWeight = goals.goal
            _startWeight = goals.start
        }catch (ex: Exception){
            _goalWeight = 0f
            _goalText?.setText("Goal weight missing or corrupted")
        }
        try{
            val list = db.GetInfoListByUser(_userId)
            _currWeight = list.get(0).weight
        }catch(ex: Exception){
            _currWeight = 0f
            _currText?.setText("No current weight found")
        }

        if(_goalWeight > 0){
            _goalText?.setText(_goalWeight.toString() + " Pounds")
        }
        if(_currWeight > 0){
            _currText?.setText(_currWeight.toString() + " Pounds")
        }

        updateProgress()

    }

    // Runs when the fragment has already been created and is loaded again
    override fun onResume() {
        super.onResume()
        updateProgress()
    }

    // Update the progress bar to represent the progress toward a users' goal
    fun updateProgress(){
        _progressEnd = _startWeight - _goalWeight // 300 - 100 = 200
        val journey = _startWeight - _currWeight // 300 - 250 = 50
        _currProgress = (journey / _progressEnd) * 100f // 50/200 = 25%
        val remaining: Float = _progressEnd - journey
        _progressBar?.isIndeterminate = false
        _currProgress = (if (_currProgress < 0) 0 else _currProgress) as Float
        _progressBar?.setProgress(_currProgress.toInt(), false)
        //Toast.makeText(context, "progress% is: " + _currProgress, Toast.LENGTH_LONG).show()
        _progressText?.setText("Remaining:\n" + remaining + " Pounds")
    }

}