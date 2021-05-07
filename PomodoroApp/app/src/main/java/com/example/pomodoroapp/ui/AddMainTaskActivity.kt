package com.example.pomodoroapp.ui

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.pomodoroapp.R
import com.example.pomodoroapp.databinding.ActivityAddMainTaskBinding
import com.example.pomodoroapp.model.MainTask
import com.example.pomodoroapp.viewmodel.AddMainTaskViewModel
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class AddMainTaskActivity : AppCompatActivity() {

    private val addMainTaskViewModel: AddMainTaskViewModel by viewModels()
    private var _binding: ActivityAddMainTaskBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_main_task)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_add_main_task)
        binding.etDate.setOnClickListener {
            showDatePicker()
        }
        binding.etDate.setOnFocusChangeListener { _, focus ->
            if(focus)
                showDatePicker()
        }
        binding.btnSubmit.setOnClickListener{
            addMainTaskViewModel.insert(MainTask(
                binding.etName.text.toString(),
                addMainTaskViewModel.auth.currentUser?.uid?: "",
                0,
                binding.etDescription.text.toString(),
                Timestamp(Date.from(addMainTaskViewModel.date?.atStartOfDay(ZoneId.systemDefault())?.toInstant())),
            ))
        }

    }

    private fun showDatePicker(){
        val default = LocalDate.now()
        DatePickerDialog(
            this
        ).apply {
            this.setOnDateSetListener { _ , year, month, dayOfMonth ->
                addMainTaskViewModel.date = LocalDate.of(year, month, dayOfMonth)
                binding.etDate.setText(addMainTaskViewModel.date.toString())
            }
        }.show()

    }
}