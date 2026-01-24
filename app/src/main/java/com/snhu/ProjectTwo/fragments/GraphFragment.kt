package com.snhu.ProjectTwo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.snhu.ProjectTwo.databinding.GraphFragmentBinding
import com.snhu.ProjectTwo.utilities.UserInfo
import com.snhu.ProjectTwo.activities.CoreApp


class GraphFragment: Fragment() {

    private var _binding: GraphFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var _weightList: List<UserInfo>
    private val _userId: Long by lazy { (activity as? CoreApp)?.getId() ?: -1L }

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
    }

}