package com.example.pomodoroapp.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pomodoroapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register_user.*

class RegisterUser: AppCompatActivity() {
    private var auth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)
        auth = FirebaseAuth.getInstance()
        etPassword.transformationMethod = PasswordTransformationMethod();
        btnSubmit.setOnClickListener{
            println(etPassword.toString().trim())
            createAccount(etEmail.text.toString().trim(),etPassword.text.toString().trim())
        }
    }




    private fun createAccount(email: String, password: String) {
        auth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, "Register user succeeded", Toast.LENGTH_LONG).show()
                    val currentUser = auth!!.currentUser
                    Log.d("Register User:", currentUser.uid)

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.w("Register User:", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(applicationContext, "Register user failed.", Toast.LENGTH_LONG).show()
//                    updateUI(null)
                }
            }
    }
}