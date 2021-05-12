package com.example.pomodoroapp.viewmodel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.example.pomodoroapp.model.MainTask
import com.example.pomodoroapp.utilities.Constants
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainTaskViewModel(application: Application) : BaseViewModel(application) {
    var _allTasks = MutableLiveData<List<MainTask>>()

    init {
        getAllMainTasks()
        listenToMainTasks()
    }

    fun insert(task: MainTask) = viewModelScope.launch {
        firestore.collection(Constants.TASKSCOLLECTION).add(task).await()
    }

    fun getAllMainTasks() = viewModelScope.launch {
        val res = firestore.collection(Constants.TASKSCOLLECTION).whereEqualTo("uid", auth.currentUser?.uid).get().await()
        _allTasks.value = res.toObjects(MainTask::class.java)
    }

    private fun listenToMainTasks(){
        firestore.collection(Constants.TASKSCOLLECTION).addSnapshotListener{
            snapshot, e ->
            //exception, skip
            if( e != null) {
                Log.w(TAG, "Listen Failed", e)
                return@addSnapshotListener
            }
            if(snapshot != null){
                getAllMainTasks()
            }
        }
    }

    internal var allTasks: MutableLiveData<List<MainTask>>
        get() {return _allTasks}
        set(value) {_allTasks = value}

    fun onMainTaskClicked(name: String){
        //nic! :)
    }
}