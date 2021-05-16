package com.example.pomodoroapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pomodoroapp.R
import com.example.pomodoroapp.model.MinorTask
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_edit_minor_task.*


class EditMinorTaskActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_minor_task)
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Editace vedlejšího úkolu"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)

        etText_minor_task.setText(intent.getStringExtra("taskName"))
        var type: MinorTask.Type?

        if (intent.getStringExtra("type").equals(MinorTask.Type.INDELIBLE.toString())){
            minor_task_type_button.isChecked = true
            minor_task_type_button.text = "Neodkladné"
            type = MinorTask.Type.INDELIBLE
        }else{
            minor_task_type_button.isChecked = false
            minor_task_type_button.text = "Odkladné"
            type = MinorTask.Type.DEFERRABLE

        }

        minor_task_type_button.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                buttonView.text = "Neodkladné"
                type = MinorTask.Type.INDELIBLE
            } else {
                buttonView.text = "Odkladné"
                type = MinorTask.Type.DEFERRABLE
            }
        }



        val db = FirebaseFirestore.getInstance()
        changeMinorTask.setOnClickListener{
            db.collection("minorTasks").whereEqualTo("name", intent.getStringExtra("taskName"))
                .get().addOnSuccessListener { documents ->
                    for (document in documents) {
                        db.collection("minorTasks").document(document.id).update("name",etText_minor_task.text.toString())
                        db.collection("minorTasks").document(document.id).update("type",type.toString())
                    }
                }
                .addOnFailureListener { exception ->
                    println("Error getting documents: $exception")
                }
            finish()
        }

        deleteMinorTask.setOnClickListener{
            db.collection("minorTasks").whereEqualTo("name", intent.getStringExtra("taskName"))
                .get().addOnSuccessListener { documents ->
                    for (document in documents) {
                        document.reference.delete()
                    }
                }
                .addOnFailureListener { exception ->
                    println("Error getting documents: $exception")
                }

            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
