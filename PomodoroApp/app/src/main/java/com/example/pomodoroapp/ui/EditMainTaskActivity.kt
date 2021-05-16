package com.example.pomodoroapp.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import com.example.pomodoroapp.R
import com.example.pomodoroapp.databinding.ActivityEditMainTaskBinding
import com.example.pomodoroapp.viewmodel.EditMainTaskViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class EditMainTaskActivity: AppCompatActivity() {

    private val editMainTaskViewModel: EditMainTaskViewModel by viewModels()

    private var _binding: ActivityEditMainTaskBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_main_task)
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Editace hlavního úkolu"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_main_task)
        binding.etDateEdit.setOnClickListener {
            showDatePicker()
        }
        binding.etDateEdit.setOnFocusChangeListener { _, focus ->
            if(focus)
                showDatePicker()
        }
        binding.lifecycleOwner = this
        editMainTaskViewModel.taskNameOld = intent.getStringExtra("taskName").toString()
        binding.etNameEdit.setText(intent.getStringExtra("taskName").toString())
        editMainTaskViewModel.getTaskInfo(editMainTaskViewModel.taskNameOld)
        editMainTaskViewModel.description.observe(this) {
            binding.etDescriptionEdit.setText(editMainTaskViewModel.description.value)
        }
        editMainTaskViewModel.date.observe(this) {
            binding.etDateEdit.setText(
                editMainTaskViewModel.date.value?.toString()?.format(
                    DateTimeFormatter.ofPattern("dd-MMMM-yyyy"))
            )
        }
        editMainTaskViewModel.pomodoros.observe(this) {
            binding.etPomodorosEdit.setText(editMainTaskViewModel.pomodoros.value)
        }

        binding.changeTask.setOnClickListener{
            editMainTaskViewModel.updateTask(
                binding.etNameEdit.text.toString(),
                binding.etDescriptionEdit.text.toString()
            )
            finish()
        }

        binding.deleteMinorTask.setOnClickListener{
            editMainTaskViewModel.deleteTask(editMainTaskViewModel.taskNameOld)
            finish()
        }

        editMainTaskViewModel.lat.observe(this) {
            if(editMainTaskViewModel.lat.value != 0.0 && editMainTaskViewModel.lat.value != 0.0 && editMainTaskViewModel.pomodoros.value != "") {
                binding.showTaskMap.text = "Zobrazit na mapě"
                binding.showTaskMap.isEnabled = true
                binding.showTaskMap.isClickable = true

                binding.showTaskMap.setOnClickListener {
                    val intent = Intent(this, MapsActivity::class.java)
                    intent.putExtra("lat", editMainTaskViewModel.lat.value)
                    intent.putExtra("long", editMainTaskViewModel.long.value)
                    startActivity(intent)
                }
            }
            else if(editMainTaskViewModel.pomodoros.value != "") {
                binding.showTaskMap.text = "Není k dispozici poloha pro poslední pomodoro."
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showDatePicker(){
        DatePickerDialog(
            this
        ).apply {
            this.setOnDateSetListener { _, year, month, dayOfMonth ->
                editMainTaskViewModel.date.value = Date(year, month, dayOfMonth)
                binding.etDateEdit.setText(editMainTaskViewModel.date.value.toString())
                binding.etDateEdit.clearFocus()
            }
        }.show()

    }
}