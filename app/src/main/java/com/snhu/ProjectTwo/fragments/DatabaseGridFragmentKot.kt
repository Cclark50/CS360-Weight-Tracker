package com.snhu.ProjectTwo.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.snhu.ProjectTwo.R
import com.snhu.ProjectTwo.activities.CoreApp
import com.snhu.ProjectTwo.utilities.LoginDatabase
import com.snhu.ProjectTwo.utilities.UserInfo
import com.snhu.ProjectTwo.utilities.WeightCardAdapter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.abs

class DatabaseGridFragmentKot: Fragment(){

    private lateinit var _adapter: WeightCardAdapter
    private lateinit var _list: MutableList<UserInfo>
    private val _userId by lazy { (activity as CoreApp).getId() }

    private val _recyclerView: RecyclerView? by lazy { view?.findViewById<RecyclerView>(R.id.recyclerView) }
    private val _weightEntry: EditText? by lazy { view?.findViewById<EditText>(R.id.weight_entry_database) }
    private val _addButton: ImageButton? by lazy { view?.findViewById<ImageButton>(R.id.weight_add_button) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceData: Bundle?): View {
        return inflater.inflate(R.layout.database_grid_recycle_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        InitData()

        setupRecyclerView()
        setupAddButton()
    }

    private fun InitData(){
        try{
            val db = LoginDatabase(context)
            _list = db.GetInfoListByUser(_userId)
        }catch(_: Exception){
            Toast.makeText(context, "Could not get user data", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupAddButton(){
        _addButton?.setOnClickListener { view ->
            val currWeight = _weightEntry?.text.toString().toFloatOrNull()
            _weightEntry?.setText("")
            if(currWeight == null || (currWeight !in 0f .. 3000f || abs(currWeight) < 0.0001f)){
                Toast.makeText(context, "Invalid weight entered", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currDate = System.currentTimeMillis()
            try{
                val db = LoginDatabase(context)
                db.AddNewWeight(_userId, currDate, currWeight)
                _list = db.GetInfoListByUser(_userId)
                _adapter.updateData(_list)
            }catch(_: Exception){
                Toast.makeText(context, "Could not add new weight", Toast.LENGTH_SHORT).show()
            }

            // TODO: add notification functionality
        }
    }

    private fun setupRecyclerView(){
        _recyclerView?.layoutManager = LinearLayoutManager(context)

        _adapter = WeightCardAdapter(requireContext(), _list, _userId,
            ::deleteClicked,
            ::dateClicked)

        _recyclerView?.adapter = _adapter
        _recyclerView?.setHasFixedSize(true)
    }

    fun deleteClicked(info: UserInfo, position: Int){
        val db = LoginDatabase(context)
        db.RemoveWeightAt(info.id, _userId)

        _adapter.updateData(db.GetInfoListByUser(_userId))
    }

    fun dateClicked(info: UserInfo, position: Int){
        val date = LocalDate.now()
        //val datePickerDialog: DatePickerDialog =
            DatePickerDialog(
            requireContext(), { _, year, month, dayOfMonth ->
                val date: Long = LocalDate.of(year, month + 1, dayOfMonth).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    val db = LoginDatabase(context)
                    db.ChangeDateAt(info.id, _userId, date)
                    _adapter.updateData(db.GetInfoListByUser(_userId))

                }, date.year, date.monthValue - 1, date.dayOfMonth
        ).show()
    }
}