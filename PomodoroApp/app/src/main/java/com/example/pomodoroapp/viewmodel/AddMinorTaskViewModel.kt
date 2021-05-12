package com.example.pomodoroapp.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.pomodoroapp.model.MinorTask
import com.example.pomodoroapp.utilities.Constants
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AddMinorTaskViewModel(application: Application) : BaseViewModel(application) {
    var text: String? = null
    fun insert(task: MinorTask) = viewModelScope.launch {
        firestore.collection(Constants.MINORTASKS).add(task).await()
    }
}