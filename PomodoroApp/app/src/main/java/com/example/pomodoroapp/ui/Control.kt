package com.example.pomodoroapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager.widget.ViewPager
import com.example.pomodoroapp.R
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_control.*


class Control : AppCompatActivity() {
        private lateinit var tabLayout: TabLayout
        lateinit var viewPager: ViewPager
        private lateinit var mGoogleSignInClient: GoogleSignInClient
        private val auth by lazy {
              FirebaseAuth.getInstance()
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_control)

            val navController = Navigation.findNavController(this, R.id.fragNavHost)
            bottomNavView.setupWithNavController(navController)

            val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.myAccountFragment2 -> {
                        navController.navigate(R.id.myAccountFragment2)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.pomodoroTimerFragment2 -> {
                        navController.navigate(R.id.pomodoroTimerFragment2)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.mainTasksFragment2 -> {
                        navController.navigate(R.id.mainTasksFragment2)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.minorTasksFragment2 -> {
                        navController.navigate(R.id.minorTasksFragment2)
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }
            bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        }



//
//            when (navController.get.getItemId()) {
//                R.id.navigation_home -> {
//                    mTextMessage.setText(R.string.title_home)
//                    switchToFragment1()
//                }
//                R.id.navigation_dashboard -> {
//                    mTextMessage.setText(R.string.title_dashboard)
//                    switchToFragment2()
//                }
//                R.id.navigation_notifications -> {
//                    mTextMessage.setText(R.string.title_notifications)
//                    switchToFragment3()
//                }
//            }

//            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build()
//            mGoogleSignInClient= GoogleSignIn.getClient(this, gso)

//            tabLayout = findViewById(R.id.tabLayout)
//            viewPager = findViewById(R.id.viewPager)
//            tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.account))
//            tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.timer))
//            tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.main_tasks))
//            tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.minor_tasks))
//            tabLayout.tabGravity = TabLayout.GRAVITY_FILL
//            val adapter = MyAdapter(this, supportFragmentManager,
//                tabLayout.tabCount)
//            viewPager.adapter = adapter
//            // set default page
//            viewPager.setCurrentItem(1, true)
//            tabLayout.getTabAt(1)?.select();
//            viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
//            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//                override fun onTabSelected(tab: TabLayout.Tab) {
//                    viewPager.currentItem = tab.position
//                }
//                override fun onTabUnselected(tab: TabLayout.Tab) {}
//                override fun onTabReselected(tab: TabLayout.Tab) {}
//            })

}