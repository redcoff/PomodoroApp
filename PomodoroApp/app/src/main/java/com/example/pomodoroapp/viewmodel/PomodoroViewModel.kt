package com.example.pomodoroapp.viewmodel

import android.app.Application
import android.location.LocationManager
import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pomodoroapp.model.MainTask
import com.example.pomodoroapp.utilities.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Job
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
    var pomodoroCounter = 1

    //0 - visible
    //8 - gone
    var addTaskVisibility = MutableLiveData<Int>(8)
    var stopPomodoroVisibility = MutableLiveData<Int>(8)
    var remainingVisibility = MutableLiveData<Int>(8)
    var startPomodoroVisibility = MutableLiveData<Int>(0)

    var _allTasks = MutableLiveData<List<MainTask>>()

    //Location
    lateinit var locationManager: LocationManager
    lateinit var fusedLocationClient: FusedLocationProviderClient
    var locationLat: Double = 0.0
    var locationLong: Double = 0.0
    val locationPermissionCode = 2



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
        return when {
            pomodoroCounter % 8 == 0 && pomodoroCounter > 1 -> {
                Constants.FULLBREAKTIME
            }
            pomodoroCounter % 2 == 0 -> {
                Constants.BREAKTIME
            }
            else -> {
                Constants.POMODOROTIME
            }
        }
//        if(pomodoroCounter % 4 == 0 && pomodoroCounter > 1){
//            isBreak = false
//            println("IM HERE")
//            return Constants.FULLBREAKTIME
//        }
//        return if(isBreak){
//            Constants.BREAKTIME
//        } else {
//            Constants.POMODOROTIME
//        }
    }

    fun updateTaskPomodoro(name: String) = viewModelScope.launch {
        firestore.collection(Constants.TASKSCOLLECTION).whereEqualTo("name", name)
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    viewModelScope.launch {
                        var pomodoros = 0
                        val job: Job = viewModelScope.launch {
                            firestore.collection(Constants.TASKSCOLLECTION).document(document.id)
                                .get()
                                .addOnSuccessListener { documentSnapshot ->
                                    val task = documentSnapshot.toObject<MainTask>()
                                    if (task != null) {
                                        pomodoros = task.pomodoros
                                    }
                                }.addOnFailureListener { exception ->
                                    println("Error getting data: $exception")
                                }.await()
                        }
                        job.join()
                        pomodoros++
                        firestore.collection(Constants.TASKSCOLLECTION).document(document.id)
                            .update("pomodoros", pomodoros)
                        firestore.collection(Constants.TASKSCOLLECTION).document(document.id)
                            .update("lat", locationLat)
                        firestore.collection(Constants.TASKSCOLLECTION).document(document.id)
                            .update("long", locationLong)
                    }
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }.await()
    }

}