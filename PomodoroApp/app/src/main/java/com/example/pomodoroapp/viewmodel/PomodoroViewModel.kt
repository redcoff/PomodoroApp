package com.example.pomodoroapp.viewmodel

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pomodoroapp.model.MainTask
import com.example.pomodoroapp.utilities.Constants
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PomodoroViewModel(application: Application) : BaseViewModel(application)  {
    var counter = MutableLiveData<Int>(getCurrentStateTime())
    var running = MutableLiveData<Boolean>(false)
    var timer = MutableLiveData<CountDownTimer>()
    var timerInfo:MutableLiveData<String>
    var progress = MutableLiveData<Int>(0)

    var currenttask : String = ""
    var isBreak = false
    var pomodoroCounter = 0

    //0 - visible
    //8 - gone
    var addTaskVisibility = MutableLiveData<Int>(8)
    var stopPomodoroVisibility = MutableLiveData<Int>(8)
    var remainingVisibility = MutableLiveData<Int>(8)
    var startPomodoroVisibility = MutableLiveData<Int>(0)

    var _allTasks = MutableLiveData<List<MainTask>>()



    init {
        getAllMainTasks()
        timerInfo = if(counter.value?.div(60)!! >=10 ) {
            MutableLiveData<String>("${(counter.value?.div(60))}:00")
        }else{
            MutableLiveData<String>("0${(counter.value?.div(60))}:00")
        }
    }

    fun getAllMainTasks() = viewModelScope.launch {
        val res = firestore.collection(Constants.TASKSCOLLECTION).whereEqualTo("uid", auth.currentUser?.uid).get().await()
        _allTasks.value = res.toObjects(MainTask::class.java)
    }
    internal var allTasks: MutableLiveData<List<MainTask>>
        get() {return _allTasks}
        set(value) {_allTasks = value}

    // full pomodoro time (25 min) or break (5 min) or full break (30 min)
    fun getCurrentStateTime(): Int {
        if(isBreak && pomodoroCounter == 4){
            return Constants.FULLBREAKTIME
        }
        if(isBreak){
            return Constants.BREAKTIME
        }
        else {
            return Constants.POMODOROTIME
        }
    }

    fun updateTaskPomodoro(name: String){
        firestore.collection(Constants.TASKSCOLLECTION).whereEqualTo("name", name)
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    var pomodoros = 0
                    firestore.collection(Constants.TASKSCOLLECTION).document(document.id).get().addOnSuccessListener { documentSnapshot ->
                        val task = documentSnapshot.toObject<MainTask>()
                        if (task != null) {
                            pomodoros = task.pomodoros
                        }
                    }
                    firestore.collection(Constants.TASKSCOLLECTION).document(document.id).update("pomodoros",pomodoros+1)
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }

}