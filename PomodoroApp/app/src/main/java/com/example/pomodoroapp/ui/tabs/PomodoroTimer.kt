package com.example.pomodoroapp.ui.tabs

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.pomodoroapp.R
import kotlinx.android.synthetic.main.activity_pomodorotimer.view.*

class PomodoroTimer : Fragment() {
    private var counter = 25*60
    private var running = false
    private lateinit var timer: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val view: View = inflater.inflate(R.layout.activity_pomodorotimer, container, false)
        view.stop_pomodoro.visibility = View.GONE

        view.start_pomodoro.setOnClickListener{
            if(!running){
                counter = 25*60
                startTimeCounter(view)
                view.start_pomodoro.text = "test"
                view.stop_pomodoro.visibility = View.VISIBLE
            }else{

            }
        }
        view.stop_pomodoro.setOnClickListener{
            timer.cancel()
            view.stop_pomodoro.visibility = View.GONE
        }
        return view
    }


    private fun startTimeCounter(view: View) {
        val countTime: TextView = view.findViewById(R.id.timerId)
        timer = object : CountDownTimer(25*60*1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countTime.text = (counter/60).toString()+ " minutes \n" + ((counter%60)).toString() +" seconds"
                println(counter)
                counter--
            }
            override fun onFinish() {
                countTime.text = "Finished"
            }
        }.start()
    }









}