package com.example.pomodoroapp.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pomodoroapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.acitvity_login.*


class LoginActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitvity_login)

        // Firebase Authentication
        auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth!!.currentUser
        if(currentUser != null){
            val intent = Intent(this, Control::class.java)
            startActivity(intent)
        }
    }

    /**
     * Login event.
     */
    fun buLoginEvent(view: View) {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        if (email.isEmpty() || password.isEmpty()){
            val dlgAlert: AlertDialog.Builder = AlertDialog.Builder(this)
            dlgAlert.setMessage("Špatné přihlašovací údaje")
            dlgAlert.setTitle("Vyplňte email i heslo")
            dlgAlert.setPositiveButton("OK", null)
            dlgAlert.setCancelable(true)
            dlgAlert.create().show()
        }else if(!TextUtils.isEmpty(email) && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            val dlgAlert: AlertDialog.Builder = AlertDialog.Builder(this)
            dlgAlert.setMessage("Špatný formát emailu")
            dlgAlert.setTitle("Chyba přihlášení")
            dlgAlert.setPositiveButton("OK", null)
            dlgAlert.setCancelable(true)
            dlgAlert.create().show()
        }else{
            signIn(email, password)
        }
    }

    private fun createAccount(email: String, password: String) {
        auth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext, "Register user succeeded", Toast.LENGTH_LONG).show()
                    val currentUser = auth!!.currentUser
                    Log.d("Register User:", currentUser.uid)
                    //updateUI(user)
                } else {
                    Log.w("Register User:", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(applicationContext, "Register user failed.", Toast.LENGTH_LONG).show()
                    updateUI(null)
                }
            }
    }

    private fun signIn(email: String, password: String) {
        auth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(
                        applicationContext,
                        "Authentication succeeded",
                        Toast.LENGTH_LONG
                    ).show()
                    val currentUser = auth!!.currentUser
                    Log.d("Login:", currentUser.uid)
                    val intent = Intent(this, Control::class.java).apply {
                        putExtra(EXTRA_MESSAGE, currentUser)
                    }
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(applicationContext, "Authentication failed.", Toast.LENGTH_LONG).show()
                    //updateUI(null)
                }
            }
    }
    private fun sendEmailVerification() {
        val user = auth!!.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                // Email Verification sent
            }
    }

    private fun updateUI(user: FirebaseUser?) {

    }

    private fun reload() {

    }

    companion object {
        private const val TAG = "EmailPassword"
    }

}