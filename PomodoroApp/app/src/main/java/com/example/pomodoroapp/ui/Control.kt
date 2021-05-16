package com.example.pomodoroapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager.widget.ViewPager
import com.example.pomodoroapp.R
import com.example.pomodoroapp.viewmodel.MainTaskViewModel
import com.example.pomodoroapp.viewmodel.MinorTaskViewModel
import com.example.pomodoroapp.viewmodel.PomodoroViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_control.*


class Control : AppCompatActivity() {
        private lateinit var tabLayout: TabLayout
        lateinit var viewPager: ViewPager
        private lateinit var mGoogleSignInClient: GoogleSignInClient

        private val pomodoroViewModel: PomodoroViewModel by viewModels()
        private val mainTaskViewModel: MainTaskViewModel by viewModels()
        private val minorTaskViewModel: MinorTaskViewModel by viewModels()


    private val auth by lazy {
              FirebaseAuth.getInstance()
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_control)

            val actionbar = supportActionBar
            actionbar!!.title = "Pomodoro timer"

            val navController = Navigation.findNavController(this, R.id.fragNavHost)
            bottomNavView.setupWithNavController(navController)

            val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.myAccountFragment2 -> {
                        navController.navigate(R.id.myAccountFragment2)
                        actionbar.title = "Účet"
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.pomodoroTimerFragment2 -> {
                        navController.navigate(R.id.pomodoroTimerFragment2)
                        actionbar.title = "Pomodoro timer"
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.mainTasksFragment2 -> {
                        navController.navigate(R.id.mainTasksFragment2)
                        actionbar.title = "Hlavní úkoly"
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.minorTasksFragment2 -> {
                        navController.navigate(R.id.minorTasksFragment2)
                        actionbar.title = "Vedlejší úkoly"
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }
            bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        }


}