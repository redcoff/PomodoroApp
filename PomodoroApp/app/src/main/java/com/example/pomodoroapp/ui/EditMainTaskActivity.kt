package com.example.pomodoroapp.ui

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.pomodoroapp.R
import com.example.pomodoroapp.databinding.ActivityEditMainTaskBinding
import com.example.pomodoroapp.viewmodel.EditMainTaskViewModel
import java.time.LocalDate

class EditMainTaskActivity: AppCompatActivity() {

    private val editMainTaskViewModel: EditMainTaskViewModel by viewModels()

    private var _binding: ActivityEditMainTaskBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_main_task)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_main_task)
        binding.etDate.setOnClickListener {
            showDatePicker()
        }
        binding.etDate.setOnFocusChangeListener { _, focus ->
            if(focus)
                showDatePicker()
        }
        editMainTaskViewModel.taskNameOld = intent.getStringExtra("taskName").toString()
        binding.taskName = editMainTaskViewModel.taskNameOld
        binding.taskDescription = intent.getStringExtra("description").toString()
        //editMainTaskViewModel.newDate = editMainTaskViewModel.newDate
        binding.taskDate = intent.getStringExtra("date").toString()
        binding.taskPomodoros = intent.getStringExtra("pomodoros").toString()

        binding.changeTask.setOnClickListener{
            editMainTaskViewModel.updateTask(binding.etNameEdit.text.toString(), binding.etDescriptionEdit.text.toString())
            finish()
        }

        binding.deleteMinorTask.setOnClickListener{
            editMainTaskViewModel.deleteTask(editMainTaskViewModel.taskNameOld)
            finish()
        }
    }

    private fun showDatePicker(){
        DatePickerDialog(
            this
        ).apply {
            this.setOnDateSetListener { _ , year, month, dayOfMonth ->
                editMainTaskViewModel.newDate = LocalDate.of(year, month, dayOfMonth)
                binding.etDateEdit.setText(editMainTaskViewModel.newDate.toString())
                binding.etDateEdit.clearFocus()
            }
        }.show()

    }
}