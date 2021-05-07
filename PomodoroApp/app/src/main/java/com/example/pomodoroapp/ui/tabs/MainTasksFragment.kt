package com.example.pomodoroapp.ui.tabs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pomodoroapp.R
import com.example.pomodoroapp.adapter.MainTaskListAdapter
import com.example.pomodoroapp.databinding.ActivityMainTasksBinding
import com.example.pomodoroapp.ui.AddMainTaskActivity
import com.example.pomodoroapp.ui.Control
import com.example.pomodoroapp.viewmodel.MainTaskViewModel

class MainTasksFragment : Fragment() {

    private val mainTaskViewModel: MainTaskViewModel by viewModels()

    private var _binding: ActivityMainTasksBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var taskListAdapter: MainTaskListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.activity_main_tasks, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
       binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = MainTaskListAdapter()
        }
        binding.fab.setOnClickListener {
            startActivity(Intent(requireContext(), AddMainTaskActivity::class.java))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //myPoisViewModel = ViewModelProviders.of(this, factory).get(MyPoisViewModel::class.java)
        //taskViewModel = ViewModelProvider(this, factory).get(taskViewModel::class.java)
    }


    //override fun itemClicked(item: Task) {
    //    Snackbar.make(binding.root, "Item id=${item.id} clicked", Snackbar.LENGTH_SHORT).show()
    //}
}
