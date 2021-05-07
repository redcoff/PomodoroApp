package com.example.pomodoroapp.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pomodoroapp.model.MainTask
import com.example.pomodoroapp.utilities.minorTasks.Constants
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class AddMainTaskViewModel (application: Application) : BaseViewModel(application) {

    var date : LocalDate? = null

    fun insert(task: MainTask) = viewModelScope.launch {
        firestore.collection(Constants.TASKSCOLLECTION).add(task).await()
    }
}