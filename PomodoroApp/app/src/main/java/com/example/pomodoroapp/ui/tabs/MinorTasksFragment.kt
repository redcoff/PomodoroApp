package com.example.pomodoroapp.ui.tabs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.*
import com.example.pomodoroapp.R
import com.example.pomodoroapp.adapter.MainTaskListAdapter
import com.example.pomodoroapp.adapter.MinorTasksAdapter
import com.example.pomodoroapp.databinding.ActivityMinorTasksBinding
import com.example.pomodoroapp.model.MinorTask
import com.example.pomodoroapp.model.Task
import com.example.pomodoroapp.ui.AddMainTaskActivity
import com.example.pomodoroapp.ui.AddMinorTaskActivity
import com.example.pomodoroapp.viewmodel.MinorTaskViewModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import kotlinx.android.synthetic.main.activity_minor_tasks.*
import kotlinx.android.synthetic.main.activity_minor_tasks.view.*

class MinorTasksFragment : Fragment() {

    private val minorTaskViewModel: MinorTaskViewModel by viewModels()
    private var _binding: ActivityMinorTasksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.activity_minor_tasks, container, false)
        val manager = GridLayoutManager(activity, 3)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) = when (position) {
                0 -> 3
                else -> 1
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.neodkladne.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = MinorTasksAdapter(MinorTasksAdapter.MinorTaskListener { name ->
                minorTaskViewModel.onMinorTaskClicked(name)
            }).apply {
                addSubmitList(minorTaskViewModel.neodkladneTasky.value)
            }
        }

        minorTaskViewModel.neodkladneTasky.observe(viewLifecycleOwner) {
            binding.neodkladne.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = MinorTasksAdapter(MinorTasksAdapter.MinorTaskListener { name ->
                    minorTaskViewModel.onMinorTaskClicked(name)
                }).apply {
                    addSubmitList(it)
                }
            }
        }

        binding.odkladne.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = MinorTasksAdapter(MinorTasksAdapter.MinorTaskListener { name ->
                minorTaskViewModel.onMinorTaskClicked(name)
            }).apply {
                addSubmitList(minorTaskViewModel.odkladneTasky.value)
            }
        }



        minorTaskViewModel.odkladneTasky.observe(viewLifecycleOwner) {
            binding.odkladne.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = MinorTasksAdapter(MinorTasksAdapter.MinorTaskListener { name ->
                    minorTaskViewModel.onMinorTaskClicked(name)
                }).apply {
                    addSubmitList(it)
                }
            }
        }

        binding.fab.setOnClickListener {
            startActivity(Intent(requireContext(), AddMinorTaskActivity::class.java))
        }
    }

}