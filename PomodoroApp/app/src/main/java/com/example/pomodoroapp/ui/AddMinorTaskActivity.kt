package com.example.pomodoroapp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.example.pomodoroapp.R
import com.example.pomodoroapp.databinding.ActivityAddMinorTaskBinding
import com.example.pomodoroapp.model.MinorTask
import com.example.pomodoroapp.ui.tabs.MinorTasksFragment
import com.example.pomodoroapp.viewmodel.AddMinorTaskViewModel
import com.google.android.material.internal.ContextUtils.getActivity
import java.util.*


class AddMinorTaskActivity : AppCompatActivity() {
    private val addMinorTaskViewModel: AddMinorTaskViewModel by viewModels()
    private var _binding: ActivityAddMinorTaskBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_minor_task)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_add_minor_task)

        binding.submitMinorTask.setOnClickListener{
            println(binding.etName.text.toString())
            addMinorTaskViewModel.insert(
                MinorTask(
                    MinorTask.Type.DEFERRABLE,
                    binding.etName.text.toString(),
                    addMinorTaskViewModel.auth.currentUser?.uid ?: "",
                )
            )
            finish()
        //            val transaction = this.supportFragmentManager.beginTransaction()
//            transaction.replace(R.id.minor_tasks_fragment, MinorTasksFragment())
//            transaction.disallowAddToBackStack()
//            transaction.commit()

        }

    }

}