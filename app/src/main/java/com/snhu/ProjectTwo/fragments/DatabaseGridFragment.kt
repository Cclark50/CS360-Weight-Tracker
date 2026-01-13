package com.snhu.ProjectTwo.fragments

import android.Manifest
import android.app.DatePickerDialog
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.snhu.ProjectTwo.R
import com.snhu.ProjectTwo.activities.CoreApp
import com.snhu.ProjectTwo.databinding.DatabaseGridRecycleFragmentBinding
import com.snhu.ProjectTwo.utilities.LoginDatabase
import com.snhu.ProjectTwo.utilities.UserInfo
import com.snhu.ProjectTwo.utilities.WeightCardAdapter
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.abs

class DatabaseGridFragment: Fragment(){

    private lateinit var _adapter: WeightCardAdapter
    private lateinit var _list: MutableList<UserInfo>
    private val _userId by lazy { (activity as CoreApp).getId() }

    // Use view binding instead of getting by id
    private var _binding: DatabaseGridRecycleFragmentBinding? = null
    private val binding get() = _binding!!
    private var notificationIdCurrent: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceData: Bundle?): View {
        _binding = DatabaseGridRecycleFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()

        setupRecyclerView()
        setupAddButton()
    }

    private fun initData(){
        try{
            val db = LoginDatabase(context)
            _list = db.GetInfoListByUser(_userId)
        }catch(_: Exception){
            Toast.makeText(context, "Could not get user data", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupAddButton(){
        binding.weightAddButton.setOnClickListener { view ->
            try{
                var currWeight = binding.weightEntryDatabase.text.toString().toFloatOrNull()
                binding.weightEntryDatabase.setText("")
                if(currWeight == null || (currWeight !in 0f .. 3000f || abs(currWeight) < 0.0001f)){
                    Toast.makeText(context, getString(R.string.error_invalid_weight), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val db = LoginDatabase(context)
                val currDate = System.currentTimeMillis()
                try{
                    db.AddNewWeight(_userId, currDate, currWeight)
                    _list = db.GetInfoListByUser(_userId)
                    _adapter.updateData(_list)
                }catch(_: Exception){
                    Toast.makeText(context, "Could not add new weight", Toast.LENGTH_SHORT).show()
                }

                val goalWeight = db.GetGoalWeight(_userId)
                if(_list.isEmpty()){
                    return@setOnClickListener
                }
                currWeight = _list.get(0).weight
                var weightToGo: Float = 0f
                var alert: AlertType = AlertType.Do_Not_Send
                if(currWeight > goalWeight){
                    weightToGo = currWeight - goalWeight
                    if(weightToGo <= 10.0f){
                        alert = AlertType.Approaching
                    }
                }else if(currWeight <= goalWeight){
                    alert = AlertType.Reached_Goal
                }

                if(alert != AlertType.Do_Not_Send){
                    attemptSendNotification(alert, weightToGo)
                }

            }catch (_: Exception){
                Toast.makeText(requireContext(), getString(R.string.error_invalid_weight), Toast.LENGTH_SHORT)
            }
        }
    }

    private fun setupRecyclerView(){
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        _adapter = WeightCardAdapter(requireContext(), _list, _userId,
            ::deleteClicked,
            ::dateClicked)

        binding.recyclerView.adapter = _adapter
        binding.recyclerView.setHasFixedSize(true)
    }

    // removes an entry from the database when the delete button is tapped
    fun deleteClicked(info: UserInfo, position: Int){
        val db = LoginDatabase(context)
        db.RemoveWeightAt(info.id, _userId)

        _adapter.updateData(db.GetInfoListByUser(_userId))
    }

    // allows the user to change the date of a weight entry
    fun dateClicked(info: UserInfo, position: Int){
        val date = LocalDate.now()
        DatePickerDialog(
            requireContext(), { _, year, month, dayOfMonth ->
                val date: Long = LocalDate.of(year, month + 1, dayOfMonth).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val db = LoginDatabase(context)
                db.ChangeDateAt(info.id, _userId, date)
                _adapter.updateData(db.GetInfoListByUser(_userId))

            }, date.year, date.monthValue - 1, date.dayOfMonth
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // chooses which notification to send
    fun attemptSendNotification(type: AlertType, poundsRemaining: Float){
        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
            when (type){
                AlertType.Approaching -> sendNotification(getString(R.string.approaching_title), getString(R.string.approaching_message, poundsRemaining))
                AlertType.Reached_Goal -> sendNotification(getString(R.string.reached_title), getString(R.string.reached_message))
                else -> return
            }
        }
    }

    // sends a notification and updates the notification id
    fun sendNotification(title: String, message: String){
        var notiBuilder = NotificationCompat.Builder(requireContext(), "goal_alerts")
            .setSmallIcon(R.drawable.arrow)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notiManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notiManager.notify(notificationIdCurrent, notiBuilder.build())
        notificationIdCurrent++
    }

    // enum class to determine which notification to send
    enum class AlertType{
        Approaching,
        Reached_Goal,
        Do_Not_Send
    }
}