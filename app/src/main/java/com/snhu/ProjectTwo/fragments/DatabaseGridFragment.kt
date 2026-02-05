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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.snhu.ProjectTwo.R
import com.snhu.ProjectTwo.activities.CoreApp
import com.snhu.ProjectTwo.databinding.DatabaseGridRecycleFragmentBinding
import com.snhu.ProjectTwo.entities.GoalEntity
import com.snhu.ProjectTwo.entities.UserInfoEntity
import com.snhu.ProjectTwo.utilities.AddNewWeight
import com.snhu.ProjectTwo.utilities.WeightCardAdapter
import com.snhu.ProjectTwo.utilities.WeightDatabase
import com.snhu.ProjectTwo.utilities.isValidWeight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId


//@Author Christian Clark
//@Date 1-9-26

/* This is the fragment that contains the list of recoded weights
** Each record allows for deletion or changing the date
** When a user gets close to, or reaches, their goal, a notification will be sent
*/

class DatabaseGridFragment: Fragment(){

    private lateinit var _adapter: WeightCardAdapter
    private lateinit var _weightList: MutableList<UserInfoEntity>
    private val _userId: Long by lazy { (activity as CoreApp).getId() }

    // https://developer.android.com/topic/libraries/view-binding
    // Use view binding instead of getting by id
    private var _binding: DatabaseGridRecycleFragmentBinding? = null
    private val binding get() = _binding!!
    // (Google, 2025)
    private var notificationIdCurrent: Int = 0
    // Google. (2025, February 10). View binding.
    // Retrieved from Android Developers: https://developer.android.com/topic/libraries/view-binding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceData: Bundle?): View {
        _binding = DatabaseGridRecycleFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            initData()

            setupRecyclerView()
            setupAddButton()
        }
    }

    suspend private fun initData(){
        try{
            withContext(Dispatchers.IO){
                val db = WeightDatabase.getInstance(requireContext()).userInfoDao()
                _weightList = db.GetInfoListByUser(_userId).toMutableList()
            }
        }catch(_: Exception){
            Toast.makeText(context, getString(R.string.could_not_get_user_data), Toast.LENGTH_LONG).show()
        }
    }

    suspend private fun setupAddButton(){
        binding.weightAddButton.setOnClickListener { _ ->
            lifecycleScope.launch {
                try{
                    var currWeight = binding.weightEntryDatabase.text.toString().toFloatOrNull()
                    binding.weightEntryDatabase.setText("")
                    if(currWeight == null || !currWeight.isValidWeight()){
                        Toast.makeText(context, getString(R.string.error_invalid_weight), Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    val currDate = System.currentTimeMillis()
                    try{
                        withContext(Dispatchers.IO){
                            val db = WeightDatabase.getInstance(requireContext())
                            AddNewWeight(_userId, currDate, currWeight, db.userInfoDao())
                            _weightList = db.userInfoDao().GetInfoListByUser(_userId).toMutableList()
                        }
                        _adapter.updateData(_weightList)
                    }catch (_: Exception){
                        Toast.makeText(context,
                            getString(R.string.could_not_add_new_weight), Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    var goalWeightRow: GoalEntity?
                    var goalWeight: Float? = null
                    try{
                        goalWeightRow = withContext(Dispatchers.IO){
                            WeightDatabase.getInstance(requireContext()).goalDao().GetGoalRow(_userId)
                        }
                        if(goalWeightRow == null){
                            throw Exception()
                        }
                        goalWeight = goalWeightRow.goal
                    }catch (_: Exception){
                        Toast.makeText(context,
                            getString(R.string.goal_weight_could_not_be_accessed), Toast.LENGTH_SHORT).show()
                    }
                    if (goalWeight == null){
                        Toast.makeText(context, getString(R.string.goal_weight_could_not_be_accessed),
                            Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    if(_weightList.isEmpty()){
                        return@launch
                    }
                    currWeight = _weightList.get(0).weight
                    var weightToGo = 0f
                    var alert: AlertType = AlertType.DO_NOT_SEND
                    if(currWeight > goalWeight){
                        weightToGo = currWeight - goalWeight
                        if(weightToGo <= 10.0f){
                            alert = AlertType.APPROACHING
                        }
                    }else if(currWeight <= goalWeight){
                        alert = AlertType.REACHED_GOAL
                    }

                    if(alert != AlertType.DO_NOT_SEND){
                        attemptSendNotification(alert, weightToGo)
                    }

                }catch (_: Exception){
                    Toast.makeText(requireContext(), getString(R.string.error_invalid_weight), Toast.LENGTH_SHORT).show()
                }

            }

        }
    }

    private fun setupRecyclerView(){
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        _adapter = WeightCardAdapter(requireContext(), _weightList, _userId,
            ::deleteClicked,
            ::dateClicked)

        binding.recyclerView.adapter = _adapter
        binding.recyclerView.setHasFixedSize(true)
    }

    // removes an entry from the database when the delete button is tapped
    fun deleteClicked(info: UserInfoEntity, position: Int){
        lifecycleScope.launch {
           withContext(Dispatchers.IO){
               val db = WeightDatabase.getInstance(requireContext()).userInfoDao()
               db.RemoveWeightAt(info.uid)
           }
            _adapter.RemoveItem(position)
        }
    }

    fun dateClicked(info: UserInfoEntity, position: Int){
        val date = LocalDate.now()
        DatePickerDialog(
            requireContext(), { _, year, month, dayOfMonth ->
                lifecycleScope.launch {
                    val date: Long = LocalDate.of(year, month + 1, dayOfMonth).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    val list = withContext(Dispatchers.IO){
                        val db = WeightDatabase.getInstance(requireContext()).userInfoDao()
                        val copy = info.copy(date = date)
                        db.ChangeDateAt(copy)
                        db.GetInfoListByUser(_userId)
                    }
                    _adapter.updateData(list)
                }

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
                AlertType.APPROACHING -> sendNotification(getString(R.string.approaching_title), getString(R.string.approaching_message, poundsRemaining))
                AlertType.REACHED_GOAL -> sendNotification(getString(R.string.reached_title), getString(R.string.reached_message))
                else -> return
            }
        }
    }

    // sends a notification and updates the notification id
    fun sendNotification(title: String, message: String){
        val notiBuilder = NotificationCompat.Builder(requireContext(), "goal_alerts")
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
        APPROACHING,
        REACHED_GOAL,
        DO_NOT_SEND
    }
}
