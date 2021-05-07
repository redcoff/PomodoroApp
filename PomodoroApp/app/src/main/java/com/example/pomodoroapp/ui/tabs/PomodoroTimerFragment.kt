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
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.activity_pomodorotimer.view.*

class PomodoroTimerFragment : Fragment() {
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
        view.zapsat_ukol.visibility = View.GONE
        view.zbyva.visibility = View.GONE


        val countTime: TextView = view.findViewById(R.id.timerId)
        timer = object : CountDownTimer(25*60*1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                "${(counter / 60)} minutes \n${((counter % 60))} seconds".also { countTime.text = it }
                counter--
            }
            override fun onFinish() {
                "Finished".also { countTime.text = it }
            }
        }




        view.start_pomodoro.setOnClickListener{
            if(!running){
                counter = 25*60-1
                startTimeCounter(view)
                view.start_pomodoro.visibility = View.GONE
                view.zapsat_ukol.visibility = View.VISIBLE
                view.stop_pomodoro.visibility = View.VISIBLE
                view.zbyva.visibility = View.VISIBLE

            }else{

            }
        }
        view.stop_pomodoro.setOnClickListener{
            timer.cancel()
            running = false
            view.stop_pomodoro.visibility = View.GONE
            view.zapsat_ukol.visibility = View.GONE
            view.zbyva.visibility = View.GONE
            view.start_pomodoro.visibility = View.VISIBLE
            "25 minutes".also { countTime.text = it }
        }
        return view
    }


    private fun startTimeCounter(view: View) {
        timer.start()
        running = true;
    }









}