package com.example.pomodoroapp.ui

import android.app.Activity
import android.os.Bundle
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.pomodoroapp.R
import com.example.pomodoroapp.databinding.ActivityAddMinorTaskBinding
import com.example.pomodoroapp.model.MinorTask
import com.example.pomodoroapp.viewmodel.AddMinorTaskViewModel
import java.util.*


class AddMinorTaskActivity : AppCompatActivity() {
    private val addMinorTaskViewModel: AddMinorTaskViewModel by viewModels()
    private var _binding: ActivityAddMinorTaskBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_minor_task)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_add_minor_task)
        binding.minorTaskTypeButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                buttonView.text = "Neodkladné"
            } else buttonView.text = "Odkladné"
            // do something, the isChecked will be
            // true if the switch is in the On position
        }

        binding.submitMinorTask.setOnClickListener{
            val type:MinorTask.Type = if(binding.minorTaskTypeButton.isChecked) MinorTask.Type.INDELIBLE
            else MinorTask.Type.DEFERRABLE
            addMinorTaskViewModel.insert(
                MinorTask(
                    type,
                    binding.etTextMinorTask.text.toString(),
                    addMinorTaskViewModel.auth.currentUser?.uid ?: "",
                )
            )
            super.onBackPressed();
        }

        setResult(Activity.RESULT_OK)
    }
}