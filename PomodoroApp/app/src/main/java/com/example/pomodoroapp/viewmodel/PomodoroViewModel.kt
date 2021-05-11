package com.example.pomodoroapp.viewmodel

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pomodoroapp.model.MainTask
import com.example.pomodoroapp.utilities.minorTasks.Constants
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime

class PomodoroViewModel(application: Application) : BaseViewModel(application)  {
    var counter = MutableLiveData<Int>(1*60)
    var running = MutableLiveData<Boolean>(false)
    var timer = MutableLiveData<CountDownTimer>()
    var timerInfo = MutableLiveData<String>("25:00")
    var progress = MutableLiveData<Int>(0)

    //0 - visible
    //8 - gone
    var addTaskVisibility = MutableLiveData<Int>(8)
    var stopPomodoroVisibility = MutableLiveData<Int>(8)
    var remainingVisibility = MutableLiveData<Int>(8)
    var startPomodoroVisibility = MutableLiveData<Int>(0)

    var _allTasks = MutableLiveData<List<MainTask>>()

    init {
        getAllMainTasks()
    }

    fun getAllMainTasks() = viewModelScope.launch {
        val res = firestore.collection(Constants.TASKSCOLLECTION).whereEqualTo("uid", auth.currentUser?.uid).get().await()
        _allTasks.value = res.toObjects(MainTask::class.java)
    }
    internal var allTasks: MutableLiveData<List<MainTask>>
        get() {return _allTasks}
        set(value) {_allTasks = value}

}