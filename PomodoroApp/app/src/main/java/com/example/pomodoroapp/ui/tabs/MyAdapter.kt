package com.example.pomodoroapp.ui.tabs

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.pomodoroapp.ui.tabs.MainTasks
import com.example.pomodoroapp.ui.tabs.MinorTasks
import com.example.pomodoroapp.ui.tabs.MyAccount
import com.example.pomodoroapp.ui.tabs.PomodoroTimer

@Suppress("DEPRECATION")
internal class MyAdapter(
    var context: Context,
    fm: FragmentManager,
    private var totalTabs: Int
) :
    FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                MyAccount()
            }
            1 -> {
               PomodoroTimer()
            }
            2 -> {
                MainTasks()
            }
            3 -> {
                MinorTasks()
            }
            else -> getItem(position)
        }
    }
    override fun getCount(): Int {
        return totalTabs
    }
}