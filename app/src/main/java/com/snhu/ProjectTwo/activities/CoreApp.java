package com.snhu.ProjectTwo.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.internal.NavContext;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.snhu.ProjectTwo.R;
import com.snhu.ProjectTwo.fragments.DatabaseGridFragment;

//@Author Christian Clark
//@Date 8-14-25

//The core portion of the application that holds fragments
public class CoreApp extends AppCompatActivity {

    long _userId;
    DatabaseGridFragment _gridFragment;
    BottomNavigationView _navbar;
    NavController _navController;
    NavHostFragment _navHostFragment;


    @Override
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.core_app_frag_holder);
        _userId = getIntent().getLongExtra("UserId", -1);
        _navbar = findViewById(R.id.nav_view);

        //setting up the fragment navigation and starts the database grid fragment
        _navHostFragment = (NavHostFragment)getSupportFragmentManager().findFragmentById(R.id.frag_frame);
        if(_navHostFragment != null){
            _navController = _navHostFragment.getNavController();
            NavigationUI.setupWithNavController(_navbar, _navController);

            Bundle args = new Bundle();
            args.putLong("UserId", _userId);
            _navController.setGraph(R.navigation.nav_graph, args);
        }
    }

    public long getId(){
        return _userId;
    }
}
