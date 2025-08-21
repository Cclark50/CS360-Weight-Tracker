package com.snhu.ProjectTwo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.snhu.ProjectTwo.R;
import com.snhu.ProjectTwo.fragments.DatabaseGridFragment;
import com.snhu.ProjectTwo.utilities.LoginDatabase;
import com.snhu.ProjectTwo.utilities.UserLogin;

//@Author Christian Clark
//@Date 8-14-25

//This is the launched activity that holds the account creation screen
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceData){
        super.onCreate(savedInstanceData);
        setContentView(R.layout.login);
    }

    public void CreateAccountButton(View view){
        EditText usernameEntry = findViewById(R.id.username_enter);
        String username = usernameEntry.getText().toString();
        EditText passwordEntry = findViewById(R.id.password_enter);
        String password = passwordEntry.getText().toString();

        //Username or password is empty
        if(username.isEmpty() || password.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Missing Username or Password");
            LayoutInflater inflater = getLayoutInflater();
            View failedAlert = inflater.inflate(R.layout.create_account_failed, null);
            builder.setView(failedAlert);
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        //User needs to enter a goal weight when creating their account
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please enter a goal weight");
        LayoutInflater inflater = getLayoutInflater();
        View accountCreationBox = inflater.inflate(R.layout.account_create_input, null);
        builder.setView(accountCreationBox);
        builder.setPositiveButton("OK", (dialog, which) -> {
            EditText weightInput = accountCreationBox.findViewById(R.id.weightEntry);
            String weightStr = weightInput.getText().toString();
            try{
                float realWeight = Float.parseFloat(weightStr);
                Log.d("WEIGHT ENTRY", "weight is: " + realWeight);
                CreateAccount(username, password, realWeight);
            }catch (Exception e){
                Toast.makeText(this, "Invalid Weight Entered", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", ((dialog, which) -> {
            dialog.dismiss();
        }));
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void CreateAccount(String username, String password, float weight){
        try(LoginDatabase db = new LoginDatabase(this)){
            db.AddNewUser(username, password, weight);
        }catch (Exception e){
            Toast.makeText(this, "Something went wrong accessing the database" + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void LoginButton(View view){
        EditText usernameEntry = findViewById(R.id.username_enter);
        String username = usernameEntry.getText().toString();
        EditText passwordEntry = findViewById(R.id.password_enter);
        String password = passwordEntry.getText().toString();

        if(username.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Username or Password is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        try(LoginDatabase db = new LoginDatabase(this)){
            UserLogin login = db.ConfirmLogin(username, password);
            if(login == null){
                Toast.makeText(this, "Invalid Username or Password", Toast.LENGTH_LONG).show();
                return;
            }
            //switch to the database screen
            Intent intent = new Intent(this, CoreApp.class);
            intent.putExtra("UserId", login.getId());
            startActivity(intent);
            finish();
        }catch (Exception e){
            Log.d("LOGINBUTTON", "Login button failed " + e.getMessage());
        }
    }
}