package com.luckyboy.jetpacklearn.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.luckyboy.jetpacklearn.R

class HostActivity :AppCompatActivity(){

    lateinit var bottomNavigationView:BottomNavigationView
    lateinit var mToolBar: Toolbar
    lateinit var mIvLogo:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
        val host:NavHostFragment = supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment
        val navController = host.navController

        initWidget()
        initBottomNavigationView(bottomNavigationView, navController)
    }

    private fun initWidget(){
        bottomNavigationView = findViewById(R.id.btn_view)
        mToolBar = findViewById(R.id.toolbar)
        mIvLogo = findViewById(R.id.iv_logo)
    }


    private fun initBottomNavigationView(bottomNavigationView: BottomNavigationView, navController: NavController){
       bottomNavigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when(destination.id){
                R.id.meFragment-> mIvLogo.visibility = View.VISIBLE
                else -> mIvLogo.visibility = View.GONE
            }
        }
    }


}

























