package com.snhu.ProjectTwo.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.snhu.ProjectTwo.R;
import com.snhu.ProjectTwo.activities.CoreApp;
import com.snhu.ProjectTwo.utilities.LoginDatabase;
import com.snhu.ProjectTwo.utilities.UserInfo;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

//@Author Christian Clark
//@Date 8-14-25

//Holds the display of a user's latest weight and goal weight together
public class GoalFragment extends Fragment {
    private float _goalWeight;
    private float _currWeight;
    private long _userId;

    TextView _goalText;
    TextView _currText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle SavedInstaneData){
        return inflater.inflate(R.layout.goal_fragment, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceData){
        super.onViewCreated(view, savedInstanceData);
        _goalText = view.findViewById(R.id.goal_weight_show);
        _currText = view.findViewById(R.id.curr_weight_show);

        _userId = ((CoreApp)getActivity()).getId();
        LoginDatabase db = new LoginDatabase(getContext());
        try{
            _goalWeight = db.GetGoalWeight(_userId);
        }catch (Exception e){
            _goalWeight = 0;
            _goalText.setText("Goal weight missing or corrupted");
        }
        try{
            ArrayList<UserInfo> list = db.GetInfoListByUser(_userId);
            _currWeight = list.get(0).getWeight();
        }catch (Exception e){
            _currWeight = 0;
            _currText.setText("No current weight found");
        }

        if(_goalWeight > 0){
            _goalText.setText(_goalWeight + " Pounds");
        }
        if(_currWeight > 0){
            _currText.setText(_currWeight + " Pounds");
        }

    }
}
