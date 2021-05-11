package com.example.pomodoroapp.ui.tabs

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pomodoroapp.R
import com.example.pomodoroapp.adapter.MainTaskListAdapter
import com.example.pomodoroapp.databinding.ActivityPomodorotimerBinding
import com.example.pomodoroapp.model.MainTask
import com.example.pomodoroapp.ui.AddMinorTaskActivity
import com.example.pomodoroapp.viewmodel.MainTaskViewModel
import com.example.pomodoroapp.viewmodel.PomodoroViewModel
import com.google.android.material.button.MaterialButton
import com.google.type.DateTime
import kotlinx.android.synthetic.main.activity_pomodorotimer.view.*
import java.time.LocalDateTime
import java.util.stream.Collectors

class PomodoroTimerFragment : Fragment() {
    private val pomodoroViewModel: PomodoroViewModel by viewModels()

    private var _binding: ActivityPomodorotimerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = DataBindingUtil.inflate(inflater, R.layout.activity_pomodorotimer, container, false)
        pomodoroViewModel.timer.value = object : CountDownTimer(1*60*1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                "${(pomodoroViewModel.counter.value?.div(60))}:${((pomodoroViewModel.counter.value?.rem(
                    60
                )))}".also { pomodoroViewModel.timerInfo.value = it }
                pomodoroViewModel.counter.value = pomodoroViewModel.counter.value?.minus(1)
                pomodoroViewModel.progress.value = (100 - (millisUntilFinished / 1000)).toInt()
            }
            override fun onFinish() {
                "Finished".also { pomodoroViewModel.timerInfo.value = it }
            }
        }

        binding.startPomodoro.setOnClickListener{
            startPomodoro()
        }
        binding.stopPomodoro.setOnClickListener{
            cancelPomodoro()
        }
        binding.zapsatUkol.setOnClickListener{
            startActivity(Intent(requireContext(), AddMinorTaskActivity::class.java))
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        binding.lifecycleOwner = this
    }

    private fun setBinding(){
        binding.timer = pomodoroViewModel.timerInfo.value
        pomodoroViewModel.timerInfo.observe(viewLifecycleOwner) {
            binding.timer = pomodoroViewModel.timerInfo.value
        }
        binding.addTaskVisibility = pomodoroViewModel.addTaskVisibility.value!!
        pomodoroViewModel.addTaskVisibility.observe(viewLifecycleOwner) {
            binding.addTaskVisibility = pomodoroViewModel.addTaskVisibility.value!!
        }

        binding.stopPomodoroVisibility = pomodoroViewModel.stopPomodoroVisibility.value!!
        pomodoroViewModel.stopPomodoroVisibility.observe(viewLifecycleOwner) {
            binding.stopPomodoroVisibility = pomodoroViewModel.stopPomodoroVisibility.value!!
        }
        binding.remainingVisibility = pomodoroViewModel.remainingVisibility.value!!
        pomodoroViewModel.remainingVisibility.observe(viewLifecycleOwner) {
            binding.remainingVisibility = pomodoroViewModel.remainingVisibility.value!!
        }
        pomodoroViewModel.startPomodoroVisibility.observe(viewLifecycleOwner) {
            binding.startPomodoroVisibility = pomodoroViewModel.startPomodoroVisibility.value!!
        }

        pomodoroViewModel.progress.observe(viewLifecycleOwner) {
            binding.progress = pomodoroViewModel.progress.value!!
        }

        pomodoroViewModel.allTasks.observe(viewLifecycleOwner) {
            if(pomodoroViewModel.allTasks.value != null) {
                val items = pomodoroViewModel.allTasks.value!!
                    .stream()
                    .map { v -> v.name }
                    .collect(Collectors.toList())
                val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
                binding.autoComplete.setAdapter(adapter)
            }
        }
    }


    private fun startTimeCounter() {
        pomodoroViewModel.timer.value?.start()
        pomodoroViewModel.running.value = true;
    }

    private fun startPomodoro() {
        pomodoroViewModel.progress.value = 0
        if (!pomodoroViewModel.running.value!!) {
            pomodoroViewModel.counter.value = 1 * 60 - 1
            startTimeCounter()
            pomodoroViewModel.stopPomodoroVisibility.value = 0
            pomodoroViewModel.addTaskVisibility.value = 0
            pomodoroViewModel.remainingVisibility.value = 0
            pomodoroViewModel.startPomodoroVisibility.value = 8
        }
    }

    private fun cancelPomodoro(){
        pomodoroViewModel.progress.value = 0
        pomodoroViewModel.timer.value?.cancel()
        pomodoroViewModel.running.value = false
        pomodoroViewModel.stopPomodoroVisibility.value = 8
        pomodoroViewModel.addTaskVisibility.value = 8
        pomodoroViewModel.remainingVisibility.value = 8
        pomodoroViewModel.startPomodoroVisibility.value = 0
        pomodoroViewModel.timerInfo.value = "25:00"
    }


}