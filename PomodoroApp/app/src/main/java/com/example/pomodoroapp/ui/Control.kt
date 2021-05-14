package com.example.pomodoroapp.ui

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.example.pomodoroapp.ui.tabs.MyAdapter
import com.example.pomodoroapp.R
import com.example.pomodoroapp.model.SavedPreference
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth

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
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            mGoogleSignInClient= GoogleSignIn.getClient(this,gso)

            tabLayout = findViewById(R.id.tabLayout)
            viewPager = findViewById(R.id.viewPager)
            tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.account))
            tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.timer))
            tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.main_tasks))
            tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.minor_tasks))
            tabLayout.tabGravity = TabLayout.GRAVITY_FILL
            val adapter = MyAdapter(this, supportFragmentManager,
                tabLayout.tabCount)
            viewPager.adapter = adapter
            // set default page
            viewPager.setCurrentItem(1, true)
            tabLayout.getTabAt(1)?.select();
            viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewPager.currentItem = tab.position
                }
                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
    }
}