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
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.lifecycleScope
import com.example.pomodoroapp.R
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.acitvity_login.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


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
            dlgAlert.setMessage(getString(R.string.bad_login))
            dlgAlert.setTitle(getString(R.string.add_credentials))
            dlgAlert.setPositiveButton(R.string.OK, null)
            dlgAlert.setCancelable(true)
            dlgAlert.create().show()
        }else if(!TextUtils.isEmpty(email) && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            val dlgAlert: AlertDialog.Builder = AlertDialog.Builder(this)
            dlgAlert.setMessage(getString(R.string.bad_email_format))
            dlgAlert.setTitle(getString(R.string.login_error))
            dlgAlert.setPositiveButton(R.string.OK, null)
            dlgAlert.setCancelable(true)
            dlgAlert.create().show()
        }else{
            lifecycleScope.launch {
                signIn(email, password)
            }
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


    private suspend fun signIn(email: String, password: String) : AuthResult? {
        return try {
            val data = auth
                ?.signInWithEmailAndPassword(email, password)
                ?.await()
            Toast.makeText(
                applicationContext,
                "Authentication succeeded",
                Toast.LENGTH_LONG
            ).show()
            val currentUser = auth!!.currentUser
            Log.d("Login:", currentUser.uid)
            val intent = Intent(this, Control::class.java).apply {
                putExtra(EXTRA_MESSAGE, currentUser)}
            startActivity(intent)
            return data

        }catch (e: Exception){
            Log.w(TAG, "signInWithEmail:failure", e)
            Toast.makeText(applicationContext, "Authentication failed.", Toast.LENGTH_LONG).show()
            null
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