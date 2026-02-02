package com.snhu.ProjectTwo.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.snhu.ProjectTwo.R
import com.snhu.ProjectTwo.databinding.AccountCreateInputBinding
import kotlin.math.abs
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.snhu.ProjectTwo.databinding.LoginBinding
import com.snhu.ProjectTwo.entities.LoginEntity
import com.snhu.ProjectTwo.utilities.AddNewUser
import com.snhu.ProjectTwo.utilities.AddNewWeight
import com.snhu.ProjectTwo.utilities.SetGoal
import com.snhu.ProjectTwo.utilities.WeightDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


//@Author Christian Clark
//@Date 1-9-26

/* This is the launched activity that holds the account creation screen
** this screen allows a user to create or log into an account
*/
class LoginActivity : AppCompatActivity() {

    private val GOAL_CHANNEL_ID: String = "goal_alerts"
    private var _checkBoxStatus: Boolean = false

    // With view binding I can forgo needing to store each element I need to access in code
    private lateinit var binding: LoginBinding

    override fun onCreate(savedInstanceData : Bundle?){
        super.onCreate(savedInstanceData)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get the app preferences to get the stored username and checkbox status
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        _checkBoxStatus = prefs.getBoolean("remember_me", false)
        binding.checkBox.isChecked = _checkBoxStatus

        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            binding.checkBox.isChecked = isChecked
        }

        // if we've stored the username, set the edit text to it or an empty string
        binding.usernameEnter.setText(prefs.getString("remembered_username", ""))

        setupNotificationChannel()
    }

    fun setupNotificationChannel(){
        // https://developer.android.com/develop/ui/views/notifications/build-notification
        // Necessary code to create a notification channel in the application
        val name: String = getString(R.string.notification_channel)
        val descriptionStr: String = getString(R.string.notification_channel_description)
        val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(GOAL_CHANNEL_ID, name, importance).apply{
            description = descriptionStr
        }
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        // (Google, 2026)
        // Google. (2026, January 19). Create a notification.
        // Retrieved from Android Developers: https://developer.android.com/develop/ui/views/notifications/build-notification
    }

    fun createAccountButton(view: View){
        val username: String = binding.usernameEnter.text.toString()
        val password: String = binding.passwordEnter.text.toString()

        // If the username or password is empty then let the user know
        if(username.isEmpty() || password.isEmpty()){
            AlertDialog.Builder(this).apply{
                setTitle(getString(R.string.missing_username_or_password))
                setPositiveButton(getString(R.string.ok), null)
            }.show()
            return
        }

        // create a dialog for a user to input their goal weight before creating their account
        AlertDialog.Builder(this).apply{
            setTitle("Please Enter a Goal Weight")
            val accCreate = AccountCreateInputBinding.inflate(layoutInflater)
            setView(accCreate.root)
            setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                val goalWeightStr = accCreate.weightEntry.text.toString()
                val currWeightStr = accCreate.currWeightEntry.text.toString()
                try{
                    val goalWeight = goalWeightStr.toFloatOrNull()
                    val currWeight = currWeightStr.toFloatOrNull()
                    // making sure the value we enter is valid
                    if((goalWeight == null || currWeight == null) ||
                        (goalWeight !in 0.0..3000.0 || abs(goalWeight) < 0.0001f) ||
                        (currWeight !in 0.0..3000.0 || abs(currWeight) < 0.0001f)
                        ) {
                        Toast.makeText(
                            this@LoginActivity,
                            getString(R.string.invalid_weight),
                            Toast.LENGTH_LONG
                        ).show()
                        dialog.dismiss()
                        return@setPositiveButton
                    }
                    createAccount(username, password, goalWeight, currWeight)
                    loginButton(view)
                }catch(ex: NumberFormatException){
                    Toast.makeText(this@LoginActivity, getString(R.string.error_invalid_weight), Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }
            }
            setNegativeButton(getString(R.string.cancel), null)
        }.show()
    }

    fun createAccount(username: String, password :String, goalWeight: Float, currWeight: Float){
        try{
            // creates database values for all required information
            lifecycleScope.launch(Dispatchers.IO) {
                val db = WeightDatabase.getInstance(applicationContext)
                val currUser = AddNewUser(username, password, goalWeight, db.loginDao())
                SetGoal(currUser, currWeight, goalWeight, db.goalDao())
                AddNewWeight(currUser, System.currentTimeMillis(), currWeight, db.userInfoDao())
            }
            //LoginDatabase(this).use{ db ->
                //val currUser = db.AddNewUser(username, password, goalWeight)
                //db.SetGoal(currUser, currWeight, goalWeight)
                //db.AddNewWeight(currUser, System.currentTimeMillis(), currWeight)
            //}
        }
        catch(sqlEx: SQLiteException)
        {
            Toast.makeText(this@LoginActivity, sqlEx.toString(), Toast.LENGTH_LONG).show()
        }
        catch (ex: Exception){
            Log.d("Create Account EX", "${ex}")
            Toast.makeText(this@LoginActivity,
                getString(R.string.something_went_wrong_accessing_the_internal_database), Toast.LENGTH_LONG).show()
        }
    }

    fun loginButton(view: View){
        val username = binding.usernameEnter.text.toString()
        val password = binding.passwordEnter.text.toString()

        // if we're missing login information inform the user
        if(username.isEmpty() || password.isEmpty()){
            Toast.makeText(this, getString(R.string.missing_username_or_password), Toast.LENGTH_SHORT).show()
            return
        }

        try{
            lifecycleScope.launch {
                val loginData = withContext(Dispatchers.IO){
                    val db = WeightDatabase.getInstance(applicationContext)
                    db.loginDao().GetUserLoginByUsername(username)
                }
                if(loginData == null){
                    Toast.makeText(this@LoginActivity,
                        getString(R.string.invalid_username_or_password), Toast.LENGTH_SHORT).show()
                    return@launch
                }
                login(loginData)
            }
        }catch (ex: Exception){
            Toast.makeText(this, getString(R.string.something_went_wrong_accessing_the_internal_database), Toast.LENGTH_SHORT).show()
            Log.e("Database Login", "ex: ", ex)
        }
    }

    // Logs into the application
    fun login(loginData: LoginEntity){
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        // if we have the remember me box checked store the username
        if(binding.checkBox.isChecked){
            prefs.edit {
                putBoolean("remember_me", true)
                putString("remembered_username", loginData.username)
            }
        }else{
            // else clear the stored username
            prefs.edit {
                putBoolean("remember_me", false)
                putString("remembered_username", "")
            }
        }
        // start the next activity
        startActivity(Intent(this@LoginActivity, CoreApp::class.java).apply{
            putExtra("UserId", loginData.uid)
        })
    }

}
