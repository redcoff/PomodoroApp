package com.example.pomodoroapp.ui.tabs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.pomodoroapp.R
import com.example.pomodoroapp.ui.Control
import com.example.pomodoroapp.ui.LoginActivity
import com.example.pomodoroapp.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
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
        view.textView2.text = auth.currentUser?.displayName
        view.textView3.text = auth.currentUser?.email



//        val imageref = Firebase.storage.reference.child(auth.currentUser?.photoUrl.toString())
//        imageref.downloadUrl.addOnSuccessListener {Uri->
//            val imageURL = Uri.toString()
//            Glide.with(this)
//                .load(imageURL)
//                .into(view.imageView2)
//
//        }


        view.logoutButton.setOnClickListener{
            auth.signOut()
            Firebase.auth.signOut()
            val intent= Intent(activity,LoginActivity::class.java)
            startActivity(intent)
        }
        return view
    }



}