package com.example.pomodoroapp.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
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
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Registrace uživatele"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        auth = FirebaseAuth.getInstance()
        etPassword.transformationMethod = PasswordTransformationMethod();
        etPassword2.transformationMethod = PasswordTransformationMethod();

        btnSubmit.setOnClickListener{
            if(etPassword.text.toString().trim().isEmpty() || etEmail.text.toString().trim().isEmpty() || etPassword2.text.toString().trim().isEmpty()){
                val dlgAlert: AlertDialog.Builder = AlertDialog.Builder(this)
                dlgAlert.setMessage(getString(R.string.bad_login))
                dlgAlert.setTitle(getString(R.string.add_credentials))
                dlgAlert.setPositiveButton(R.string.OK, null)
                dlgAlert.setCancelable(true)
                dlgAlert.create().show()
            }else if(!TextUtils.isEmpty(etEmail.text.toString().trim()) && !android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString().trim()).matches()){
                val dlgAlert: AlertDialog.Builder = AlertDialog.Builder(this)
                dlgAlert.setMessage(getString(R.string.bad_email_format))
                dlgAlert.setTitle(getString(R.string.login_error))
                dlgAlert.setPositiveButton(R.string.OK, null)
                dlgAlert.setCancelable(true)
                dlgAlert.create().show()
            }else if(etPassword.text.toString().trim().length < 6){
                val dlgAlert: AlertDialog.Builder = AlertDialog.Builder(this)
                dlgAlert.setMessage("Příliš krátké heslo")
                dlgAlert.setTitle("Chyba registrace")
                dlgAlert.setPositiveButton(R.string.OK, null)
                dlgAlert.setCancelable(true)
                dlgAlert.create().show()
            }else if(etPassword.text.toString() != etPassword2.text.toString()){
                val dlgAlert: AlertDialog.Builder = AlertDialog.Builder(this)
                dlgAlert.setMessage("Hesla se neshodují")
                dlgAlert.setTitle("Chyba registrace")
                dlgAlert.setPositiveButton(R.string.OK, null)
                dlgAlert.setCancelable(true)
                dlgAlert.create().show()
            }else createAccount(etEmail.text.toString().trim(),etPassword.text.toString().trim())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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
//                    Log.w("Register User:", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(applicationContext, "Register user failed. " + task.exception?.message, Toast.LENGTH_LONG).show()
//                    updateUI(null)
                }
            }
    }
}