package com.example.pomodoroapp.ui.tabs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pomodoroapp.R
import com.example.pomodoroapp.ui.Control
import com.example.pomodoroapp.ui.LoginActivity
import com.example.pomodoroapp.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_my_account.*
import kotlinx.android.synthetic.main.activity_my_account.view.*
import kotlin.math.log

class MyAccountFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        super.onCreateView(inflater, container, savedInstanceState)
        auth = FirebaseAuth.getInstance();

        val view: View = inflater.inflate(R.layout.activity_my_account, container, false)

        view.logoutButton.setOnClickListener{
            auth.signOut()
            val intent= Intent(activity,LoginActivity::class.java)
            startActivity(intent)
        }
        return view
    }



}