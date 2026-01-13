package com.snhu.ProjectTwo.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.snhu.ProjectTwo.R

class SettingsFragment: Fragment(){

    val SEND_NOTIFICATION_PERMISSION_REQUEST_CODE = 101;

    private var _notificationSwitch: SwitchCompat? = null
    private var _hasNotificationPermission: Boolean = false
    private var _isSetOnStart: Boolean = false

    private lateinit var _requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.settings_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceData: Bundle?){
        super.onViewCreated(view, savedInstanceData)

        _notificationSwitch = view.findViewById<SwitchCompat>(R.id.noti_switch)
        _isSetOnStart = updatePermissionState()
        updateSwitchState()

        _requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission(),
            {isGranted ->
                updatePermissionState()
                updateSwitchState()

                if(isGranted){
                    Toast.makeText(requireContext(), "Notification Permission Granted", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireContext(), "Notification Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
            )

        _notificationSwitch?.setOnCheckedChangeListener {buttonView, isChecked ->
            if(isChecked && !_hasNotificationPermission && !_isSetOnStart){
                requestNotificationPermission()
            }
        }
    }

    private fun updatePermissionState(): Boolean{
        _hasNotificationPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        return _hasNotificationPermission
    }

    private fun updateSwitchState(){
        _notificationSwitch?.isChecked = _hasNotificationPermission
    }

    private fun requestNotificationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.POST_NOTIFICATIONS)){
            AlertDialog.Builder(requireContext()).apply{
                setTitle(getString(R.string.permission_title))
                setMessage(getString(R.string.permission_message))
                setPositiveButton(getString(R.string.allow_button)){ _, _ -> _requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }
                setNegativeButton(getString(R.string.deny_button), null)
            }.show()
        }else{
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.POST_NOTIFICATIONS), SEND_NOTIFICATION_PERMISSION_REQUEST_CODE)
        }
    }
}