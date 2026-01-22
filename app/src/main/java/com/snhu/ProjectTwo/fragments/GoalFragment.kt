package com.snhu.ProjectTwo.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.snhu.ProjectTwo.R
import com.snhu.ProjectTwo.activities.CoreApp
import com.snhu.ProjectTwo.databinding.GoalFragmentBinding
import com.snhu.ProjectTwo.utilities.LoginDatabase
import com.snhu.ProjectTwo.utilities.isValidWeight
import kotlin.math.abs


//@Author Christian Clark
//@Date 1-9-26

/* Fragment that shows a user's progress towards their goal
** Shows a progress bar to visualize how close they are to reaching their goal
*/

class GoalFragment : Fragment(){

    // Tag for use when logging
    private val TAG: String = "GOAL FRAGMENT"
    private val _userId: Long by lazy {(activity as? CoreApp)?.getId() ?: -1L}
    private var _goalWeight: Float = 0f
    private var _currWeight: Float = 0f
    private var _startWeight: Float = 0f
    private var _progressEnd: Float = 0f
    private var _currProgress: Float = 0f

    // https://developer.android.com/topic/libraries/view-binding
    // Using view binding to be cleaner
    private var _binding: GoalFragmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceData: Bundle?): View {
        _binding = GoalFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceData: Bundle?){
        super.onViewCreated(view, savedInstanceData)

        updateValues()
        updateProgress()

    }

    // Runs when the fragment has already been created and is loaded again
    override fun onResume() {
        super.onResume()
        updateValues()
        updateProgress()
    }

    fun updateValues(){
        val db = LoginDatabase(context)
        try{
            val goals = db.GetGoalRow(_userId)
            _goalWeight = goals.goal
            _startWeight = goals.start
        }catch (_: Exception){
            _goalWeight = 0f
            binding.goalWeightShow.text = getString(R.string.goal_weight_missing_or_corrupted)
        }
        try{
            val list = db.GetInfoListByUser(_userId)
            _currWeight = list.get(0).weight
        }catch(_: Exception){
            _currWeight = 0f
            binding.currWeightShow.text = getString(R.string.no_current_weight_found)
        }
        if(_goalWeight.isValidWeight()){
            binding.goalWeightShow.text = getString(R.string.pounds, _goalWeight)
        }
        if(_currWeight.isValidWeight()){
            binding.currWeightShow.text = getString(R.string.pounds, _currWeight)
        }
    }

    // Update the progress bar to represent the progress toward a users' goal
    fun updateProgress(){
        _progressEnd = _startWeight - _goalWeight // 300 - 100 = 200
        val journey = _startWeight - _currWeight // 300 - 250 = 50
        _currProgress = (journey / _progressEnd) * 100f // 50/200 = 25%
        val remaining: Float = _progressEnd - journey
        binding.progressBar.isIndeterminate = false
        _currProgress = (if (_currProgress < 0) 0 else _currProgress) as Float
        binding.progressBar.setProgress(_currProgress.toInt(), false)
        if(remaining < 0f){
            binding.progressText.text = getString(R.string.you_reached_your_goal)
        }else{
            binding.progressText.text = getString(R.string.remaining_pounds, remaining)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}