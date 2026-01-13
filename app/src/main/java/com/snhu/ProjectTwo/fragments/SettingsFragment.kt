package com.snhu.ProjectTwo.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.PermissionRequest;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.snhu.ProjectTwo.R;

//@Author Christian Clark
//@Date 8-14-25

//Settings fragment for turning on and off sending SMS
public class SettingsFragment extends Fragment {

    public static final int SEND_SMS_PERMISSION_REQUEST_CODE = 101;
    SwitchCompat _smsSwitch;
    boolean _hasSmsPermissions;
    boolean _isSetUponStart;

    ActivityResultLauncher<String> _requestPermissionLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceData){
        return inflater.inflate(R.layout.settings_fragment, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceData){
        super.onViewCreated(view, savedInstanceData);

        _smsSwitch = view.findViewById(R.id.sms_switch);

        _isSetUponStart = UpdatePermissionState();
        UpdateSwitchState();

        //allows for a callback when asking for permissions
        _requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted ->{
                    UpdatePermissionState();
                    UpdateSwitchState();

                    if(isGranted){
                        Toast.makeText(getContext(), "SMS Permission granted", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(), "SMS Permission denied", Toast.LENGTH_SHORT).show();
                    }
                });

        _smsSwitch.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if(isChecked && !_hasSmsPermissions && !_isSetUponStart){
                RequestSMSPermission();
            }
        }));
    }

    private boolean UpdatePermissionState(){
        _hasSmsPermissions = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
        return _hasSmsPermissions;
    }

    private void UpdateSwitchState(){
        _smsSwitch.setChecked(_hasSmsPermissions);
    }

    private void RequestSMSPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.SEND_SMS)){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Send SMS Permission");
            builder.setMessage("This app needs SMS permissions to send alerts when you get close to, or reach, your goal weight");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    _requestPermissionLauncher.launch(Manifest.permission.SEND_SMS);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }else{
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
        }
    }
}
