package com.snhu.ProjectTwo.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.snhu.ProjectTwo.R;
import com.snhu.ProjectTwo.activities.CoreApp;
import com.snhu.ProjectTwo.interfaces.OnItemActionListener;
import com.snhu.ProjectTwo.utilities.DatabaseAdapter;
import com.snhu.ProjectTwo.utilities.LoginDatabase;
import com.snhu.ProjectTwo.utilities.UserInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//@Author Christian Clark
//@Date 8-14-25

//This fragment contains the database grid and is also where a user inputs their current weight of that moment
public class DatabaseGridFragment extends Fragment {

    private DatabaseAdapter _adapter;
    private List<UserInfo> _list;
    long _userId;

    private RecyclerView _recyclerView;
    private EditText _weightEntry;
    private ImageButton _addButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceData){
        return inflater.inflate(R.layout.database_grid_recycle_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        _recyclerView = view.findViewById(R.id.recyclerView);
        _weightEntry = view.findViewById(R.id.weight_entry_database);
        _addButton = view.findViewById(R.id.weight_add_button);

        _userId = ((CoreApp)getActivity()).getId();

        InitData();

        setupRecyclerView();
        setupAddButton();
    }

    private void InitData(){
        LoginDatabase db = new LoginDatabase(getContext());
        try
        {
            _list = db.GetInfoListByUser(_userId);
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(), "Could not get user data", Toast.LENGTH_SHORT).show();
        }
    }

    //Add current weight button
    private void setupAddButton(){
        _addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                try{
                    float currWeight = Float.parseFloat(_weightEntry.getText().toString());
                    _weightEntry.setText("");
                    if(Float.compare(currWeight, 0.0f) == 0 || currWeight < 0){
                        Toast.makeText(getContext(), "Cannot have negative or 0 weight", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(currWeight >= 3000f){
                        Toast.makeText(getContext(), "Weight too high", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String dateString = dateFormat.format(new Date());

                    //adds the current weight to the database
                    LoginDatabase db = new LoginDatabase(getContext());
                    db.AddNewWeight(_userId, dateString, currWeight);
                    _list = db.GetInfoListByUser(_userId);
                    _adapter.updateData(_list);

                    //functionality for sending SMS messages to the user when they reach within 10 pounds or pass their goal
                    float goalWeight = db.GetGoalWeight(_userId);
                    if(_list.isEmpty()){
                        return;
                    }
                    currWeight = _list.get(0).getWeight();
                    String emulatorPort = "5554";
                    float weightToGo = 0;
                    AlertType alert = AlertType.Dont_send;
                    if(currWeight > goalWeight){
                        weightToGo = currWeight - goalWeight;
                        if(weightToGo <= 10.0f){
                            alert = AlertType.Close;
                        }
                    }else if(currWeight <= goalWeight){
                        alert = AlertType.Reached_Goal;
                    }
                    if(alert != AlertType.Dont_send){
                        TrySendSMS(alert, emulatorPort, weightToGo);
                    }
                }catch (Exception e){
                    Toast.makeText(getContext(), "Invalid Weight Entry", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //sets up the recycler view so users can scroll through their list of weights
    private void setupRecyclerView(){
        _recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        _adapter = new DatabaseAdapter(getContext(), _list, _userId, new OnItemActionListener() {
            //when clicking the delete button on a card
            @Override
            public void onDeleteClicked(UserInfo info, int position) {
                LoginDatabase db = new LoginDatabase(getContext());
                db.RemoveWeightAt(info.getId(), _userId);

                _adapter.updateData(db.GetInfoListByUser(_userId));
            }

            //when clicking the date button on a card
            @Override
            public void onDateClicked(UserInfo info, int position) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        (view, year, month, dayOfMonth) -> {
                            String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                            LoginDatabase db = new LoginDatabase(getContext());
                            Toast.makeText(getContext(), "date: " + date, Toast.LENGTH_SHORT).show();
                            db.ChangeDateAt(info.getId(), _userId, date);
                            _adapter.updateData(db.GetInfoListByUser(_userId));
                        },
                        calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();
            }
        });
        _recyclerView.setAdapter(_adapter);


        _recyclerView.setHasFixedSize(true);
    }

    //attempts to send an SMS message
    public void TrySendSMS(AlertType type, String phoneNumber, float poundsToGo){
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
            SmsManager manager = getContext().getSystemService(SmsManager.class);
            String message;
            switch (type){
                case Close:
                    message = "You're only " + poundsToGo + " pounds from passing your goal!";
                    manager.sendTextMessage(phoneNumber, null, message, null, null);
                    break;
                case Reached_Goal:
                    message = "You've passed your weight goal!";
                    manager.sendTextMessage(phoneNumber, null, message, null, null);
                    break;
                default:
                    Toast.makeText(getContext(), "Something went horribly wrong", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    //small enum for storing the type of sms alert to send
    private enum AlertType{
        Close,
        Reached_Goal,
        Dont_send
    }

}
