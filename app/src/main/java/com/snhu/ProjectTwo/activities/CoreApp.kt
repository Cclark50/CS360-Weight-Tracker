package com.snhu.ProjectTwo.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.snhu.ProjectTwo.R


//@Author Christian Clark
//@Date 1-9-26

/* The main activity that is a fragment holder and holds the navigation bar
*/

class CoreApp : AppCompatActivity(){

    var _userId: Long = -1

    // Using lazy loading here because view binding adds more bloat than just one single UI field
    val _navBar: BottomNavigationView by lazy { findViewById<BottomNavigationView>(R.id.nav_view) }
    var _navController: NavController? = null

    override fun onCreate(savedInstanceData: Bundle?){
        super.onCreate(savedInstanceData)
        setContentView(R.layout.core_app_frag_holder)
        _userId = intent.getLongExtra("UserId", -1)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.frag_frame) as? NavHostFragment
        if(navHostFragment != null){
            _navController = navHostFragment.navController
            if(_navController == null){
                throw Exception("Nav Controller Broke")
            }
            NavigationUI.setupWithNavController(_navBar, _navController!!)

            val args = Bundle()
            args.putLong("UserId", _userId)
            _navController!!.setGraph(R.navigation.nav_graph, args)
        }
    }

    // allows fragments to get the current id of the user that's logged in
    public fun getId(): Long{
        return _userId
    }
}