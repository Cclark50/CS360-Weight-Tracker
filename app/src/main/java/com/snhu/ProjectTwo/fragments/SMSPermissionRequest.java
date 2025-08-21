package com.snhu.ProjectTwo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.snhu.ProjectTwo.R;

//unused class please ignore
public class SMSPermissionRequest extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        return inflater.inflate(R.layout.sms_permissions_promt, container, false);

    }

}
