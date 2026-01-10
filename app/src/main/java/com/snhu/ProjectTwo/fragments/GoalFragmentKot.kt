package com.snhu.ProjectTwo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.snhu.ProjectTwo.R
import org.w3c.dom.Text

class GoalFragmentKot : Fragment(){

    private var _goalWeight: Float = 0
    private var _currWeight: Float = 0
    private var _startWeight: Float = 0

    private val _goalText: TextView? by lazy {view?.findViewById<TextView>(R.id.goal_weight_show)}
    private val _currText: TextView? by lazy {view?.findViewById<TextView>(R.id.curr_weight_show)}


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceData: Bundle?): View {
        return inflater.inflate(R.layout.goal_fragment, container, false);
    }


    override fun onViewCreated(view: View, savedInstanceData: Bundle?){
        super.onViewCreated(view, savedInstanceData)

    }
}