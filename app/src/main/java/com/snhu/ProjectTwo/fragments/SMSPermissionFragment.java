package com.snhu.ProjectTwo.fragments;

import android.app.Dialog;
import android.content.res.Resources;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

//unused class please ignore
public class SMSPermissionFragment extends BottomSheetDialogFragment {

    @Override
    public void onStart(){
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog != null){
            View bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if(bottomSheet != null){
                BottomSheetBehavior<View> behaviour = BottomSheetBehavior.from(bottomSheet);
                behaviour.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels/2);
                behaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }
}
