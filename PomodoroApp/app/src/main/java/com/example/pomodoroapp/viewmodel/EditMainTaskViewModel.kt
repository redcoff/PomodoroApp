package com.example.pomodoroapp.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pomodoroapp.model.MainTask
import com.example.pomodoroapp.utilities.Constants
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.activity_edit_minor_task.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.util.*

class EditMainTaskViewModel  (application: Application) : BaseViewModel(application) {
    var taskNameOld : String = ""
    var description = MutableLiveData<String>("")
    var date = MutableLiveData<Date>(null)
    var pomodoros = MutableLiveData<String>("")



    fun updateTask(newName: String, newDescription: String) = viewModelScope.launch {
        firestore.collection(Constants.TASKSCOLLECTION).whereEqualTo("name", taskNameOld)
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    firestore.collection(Constants.TASKSCOLLECTION).document(document.id).update("name",newName)
                    firestore.collection(Constants.TASKSCOLLECTION).document(document.id).update("description",newDescription)
                    if(date.value != null)
                        firestore.collection(Constants.TASKSCOLLECTION).document(document.id).update("date", Timestamp(
                            date.value!!
                        ))
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }

    fun deleteTask(name: String){
        firestore.collection(Constants.TASKSCOLLECTION).whereEqualTo("name", name)
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }

    fun getTaskInfo(name: String) = viewModelScope.launch  {
        firestore.collection(Constants.TASKSCOLLECTION).whereEqualTo("name", name)
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    firestore.collection(Constants.TASKSCOLLECTION).document(document.id).get().addOnSuccessListener { documentSnapshot ->
                        val task = documentSnapshot.toObject<MainTask>()
                        if (task != null) {
                            description.value = task.description
                        }
                        if (task != null) {
                            date.value = task.date?.toDate()
                        }
                        if (task != null) {
                            pomodoros.value = task.pomodoroFormat()
                        }
                    }

                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }.await()
    }

}