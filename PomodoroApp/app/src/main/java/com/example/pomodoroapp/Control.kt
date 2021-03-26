package com.example.pomodoroapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class Control : AppCompatActivity() {
        lateinit var tabLayout: TabLayout
        lateinit var viewPager: ViewPager

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_control)
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