package com.example.pomodoroapp.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.pomodoroapp.model.MainTask
import com.example.pomodoroapp.utilities.Constants
import com.google.firebase.Timestamp
import kotlinx.android.synthetic.main.activity_edit_minor_task.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class EditMainTaskViewModel  (application: Application) : BaseViewModel(application) {
    var taskNameOld : String = ""
    var newDate : LocalDate? = null



    fun updateTask(newName: String, newDescription: String) = viewModelScope.launch {
        firestore.collection(Constants.TASKSCOLLECTION).whereEqualTo("name", taskNameOld)
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    firestore.collection(Constants.TASKSCOLLECTION).document(document.id).update("name",newName)
                    firestore.collection(Constants.TASKSCOLLECTION).document(document.id).update("description",newDescription)
                    firestore.collection(Constants.TASKSCOLLECTION).document(document.id).update("date",newDate)
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }

    fun deleteTask(name: String){
        firestore.collection("minorTasks").whereEqualTo("name", name)
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }

}