package com.example.pomodoroapp.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.pomodoroapp.model.MainTask
import com.example.pomodoroapp.model.Task
import com.example.pomodoroapp.utilities.minorTasks.Constants
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainTaskViewModel(application: Application) : BaseViewModel(application) {
    val allTasks = MutableLiveData<List<MainTask>>()

    init {
        getAllMainTasks()
    }

    fun insert(task: MainTask) = viewModelScope.launch {
        firestore.collection(Constants.TASKSCOLLECTION).add(task).await()
    }

    fun getAllMainTasks() = viewModelScope.launch {
        val res = firestore.collection(Constants.TASKSCOLLECTION).whereEqualTo("uid", auth.currentUser?.uid).get().await()
        allTasks.value = res.toObjects(MainTask::class.java)
    }

    fun onMainTaskClicked(name: String){
        //nic! :)
    }
}