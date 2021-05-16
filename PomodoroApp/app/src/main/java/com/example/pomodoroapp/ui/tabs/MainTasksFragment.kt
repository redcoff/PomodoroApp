package com.example.pomodoroapp.ui.tabs

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pomodoroapp.R
import com.example.pomodoroapp.adapter.DataItem
import com.example.pomodoroapp.adapter.MainTaskListAdapter
import com.example.pomodoroapp.databinding.ActivityMainTasksBinding
import com.example.pomodoroapp.ui.AddMainTaskActivity
import com.example.pomodoroapp.ui.Control
import com.example.pomodoroapp.viewmodel.MainTaskViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_control.*

class MainTasksFragment : Fragment() {

    private val mainTaskViewModel: MainTaskViewModel by viewModels()

    private var _binding: ActivityMainTasksBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskListAdapter: MainTaskListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.activity_main_tasks, container, false)

        val manager = GridLayoutManager(activity, 3)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) =  when (position) {
                0 -> 3
                else -> 1
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = MainTaskListAdapter(MainTaskListAdapter.MainTaskListener { name ->
                mainTaskViewModel.onMainTaskClicked(name)
            }).apply {
                addHeaderAndSubmitList(mainTaskViewModel.allTasks.value)
            }
        }
        binding.fab.setOnClickListener {
            startActivity(Intent(requireContext(), AddMainTaskActivity::class.java))
        }
        mainTaskViewModel.allTasks.observe(viewLifecycleOwner) {
            binding.recyclerview.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = MainTaskListAdapter(MainTaskListAdapter.MainTaskListener { name ->
                    mainTaskViewModel.onMainTaskClicked(name)
                }).apply {
                    addHeaderAndSubmitList(it)
                }
            }
        }

    }
}
