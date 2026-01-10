package com.snhu.ProjectTwo.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.snhu.ProjectTwo.R

class CoreApp : AppCompatActivity(){

    var _userId: Long = -1
    val _navBar: BottomNavigationView by lazy { findViewById<BottomNavigationView>(R.id.nav_view) }
    var _navController: NavController? = null

    override fun onCreate(bundle: Bundle?){
        super.onCreate(bundle)
        setContentView(R.layout.core_app_frag_holder)
        _userId = intent.getLongExtra("UserId", -1)

        val _navHostFragment = supportFragmentManager.findFragmentById(R.id.frag_frame) as? NavHostFragment
        if(_navHostFragment != null){
            _navController = _navHostFragment.navController
            if(_navController == null){
                throw Exception("Nav Controller Broke")
            }
            NavigationUI.setupWithNavController(_navBar, _navController!!)

            val args = Bundle()
            args.putLong("UserId", _userId)
            _navController!!.setGraph(R.navigation.nav_graph, args)
        }
    }

    public fun getId(): Long{
        return _userId
    }
}