package com.example.pomodoroapp.ui

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.lifecycleScope
import com.example.pomodoroapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.acitvity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext


class LoginActivity : AppCompatActivity(), CoroutineScope {
    private var auth: FirebaseAuth? = null
    private var job: Job = Job()
    private var googleClient: GoogleSignInClient?=null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acitvity_login)

        // Firebase Authentication
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("490225071409-au5luc1le77tu2advvcejs6lcggj7po5.apps.googleusercontent.com")
            .requestEmail()
            .build()
        val mGoogleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleClient = mGoogleSignInClient

        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setSize(SignInButton.SIZE_STANDARD)

        signInButton.setOnClickListener{
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, 1)

        }
        addAccount.setOnClickListener{
            val intent = Intent(this, RegisterUser::class.java)
            startActivity(intent)
        }

        // it is a class to notify the user of events that happen.
        // This is how you tell the user that something has happened in the
        // background.
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager




    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 1) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth?.signInWithCredential(credential)?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth?.currentUser
                    val intent = Intent(this, Control::class.java)
                     startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
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

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    // declaring variables
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"



/* MRDKA
    private fun createNotif() {

        val mNotificationManager: NotificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(this.applicationContext, "notify_001")
        val ii = Intent(this.applicationContext, LoginActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, ii, 0)

        val bigText = NotificationCompat.BigTextStyle()
        //bigText.bigText(verseurl)
        bigText.bigText("sadfSDFsf")
        bigText.setBigContentTitle("JDI DO PICI")
        bigText.setSummaryText("KUNDO")

        mBuilder.setContentIntent(pendingIntent)
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round)
        mBuilder.setContentTitle("MRDKA")
        mBuilder.setContentText("SADFGASDGAJKSDFGDF")
        mBuilder.priority = Notification.PRIORITY_MAX
        mBuilder.setStyle(bigText)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "Your_channel_id"
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_HIGH
            )
            mNotificationManager.createNotificationChannel(channel)
            mBuilder.setChannelId(channelId)
        }

        mNotificationManager.notify(0, mBuilder.build())


    }
*/
}