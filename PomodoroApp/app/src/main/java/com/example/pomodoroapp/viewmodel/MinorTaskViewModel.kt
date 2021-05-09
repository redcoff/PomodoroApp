package com.example.pomodoroapp.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pomodoroapp.model.MainTask
import com.example.pomodoroapp.model.MinorTask
import com.example.pomodoroapp.utilities.minorTasks.Constants
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MinorTaskViewModel (application: Application) : BaseViewModel(application) {
    val allTasks = MutableLiveData<List<MinorTask>>()

    fun insert(task: MainTask) = viewModelScope.launch {
        firestore.collection(Constants.MINORTASKS).add(task).await()
    }

    fun getAllMinorTasks() = viewModelScope.launch {
        val res = firestore.collection(Constants.MINORTASKS).whereEqualTo("uid", auth.currentUser?.uid).get().await()
        allTasks.value = res.toObjects(MinorTask::class.java)
    }
}