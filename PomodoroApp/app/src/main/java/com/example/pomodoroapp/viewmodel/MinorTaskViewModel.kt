package com.example.pomodoroapp.viewmodel

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pomodoroapp.model.MinorTask
import com.example.pomodoroapp.utilities.Constants
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MinorTaskViewModel (application: Application) : BaseViewModel(application) {
    private var _odkladneTasky = MutableLiveData<List<MinorTask>>()
    private var _neodkladneTasky = MutableLiveData<List<MinorTask>>()

    init {
        getAllIndelibleTasks()
        getAllDeferrableTasks()

        listenToDefferableTasks()
        listenToIndelibleMinorTasks()

    }

    fun insert(task: MinorTask) = viewModelScope.launch {
        firestore.collection(Constants.MINORTASKS).add(task).await()
    }

    private fun getAllDeferrableTasks() = viewModelScope.launch {
        val res = firestore.collection(Constants.MINORTASKS).whereEqualTo("uid", auth.currentUser?.uid).whereEqualTo("type","DEFERRABLE").get().await()
        _odkladneTasky.value = res.toObjects(MinorTask::class.java)
    }
    private fun getAllIndelibleTasks() = viewModelScope.launch {
        val res = firestore.collection(Constants.MINORTASKS).whereEqualTo("uid", auth.currentUser?.uid).whereEqualTo("type","INDELIBLE").get().await()
        _neodkladneTasky.value = res.toObjects(MinorTask::class.java)
    }




    private fun listenToIndelibleMinorTasks(){
        firestore.collection(Constants.MINORTASKS).whereEqualTo("type","INDELIBLE").addSnapshotListener{
                snapshot, e ->
            if( e != null) {
                Log.w(ContentValues.TAG, "Listen Failed", e)
                return@addSnapshotListener
            }
            if(snapshot != null){
                getAllIndelibleTasks()
            }
        }
    }
    private fun listenToDefferableTasks(){
        firestore.collection(Constants.MINORTASKS).whereEqualTo("type","DEFERRABLE").addSnapshotListener{
                snapshot, e ->
            if( e != null) {
                Log.w(ContentValues.TAG, "Listen Failed", e)
                return@addSnapshotListener
            }
            if(snapshot != null){
                getAllDeferrableTasks()
            }
        }
    }
    internal var odkladneTasky: MutableLiveData<List<MinorTask>>
        get() {return _odkladneTasky}
        set(value) {_odkladneTasky = value}

    internal var neodkladneTasky: MutableLiveData<List<MinorTask>>
        get() {return _neodkladneTasky}
        set(value) {_neodkladneTasky = value}

    fun onMinorTaskClicked(name: String){
    }
}