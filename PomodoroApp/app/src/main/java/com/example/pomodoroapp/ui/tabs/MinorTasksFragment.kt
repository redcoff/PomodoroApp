package com.example.pomodoroapp.ui.tabs

import android.content.ClipData
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.ListAdapter
import com.example.pomodoroapp.R
import com.example.pomodoroapp.model.MinorTask
import com.example.pomodoroapp.model.User
import com.example.pomodoroapp.utilities.minorTasks.MinorTasksAdapter
import kotlinx.android.synthetic.main.activity_minor_tasks.view.*

class MinorTasksFragment : Fragment() {

    private var adapter: MinorTasksAdapter? = MinorTasksAdapter()
    private var list: ArrayList<MinorTask>? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View = inflater.inflate(R.layout.activity_minor_tasks, container, false)
        updateAdapter()
        view.recycleView.adapter = adapter

        return view

    }

    private fun updateAdapter() {
        for(i in 0..2) {
            val item = MinorTask(MinorTask.Type.DEFERRABLE,"How do you dynamically add elements to a ListVie",i)
            list?.add(item)
        }

        adapter?.submitList(list)
        adapter?.let { println(it.itemCount) }
    }
}

