package com.example.pomodoroapp.ui.tabs

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.pomodoroapp.R
import com.example.pomodoroapp.databinding.ActivityPomodorotimerBinding
import com.example.pomodoroapp.ui.AddMinorTaskActivity
import com.example.pomodoroapp.ui.LoginActivity
import com.example.pomodoroapp.utilities.Constants.BREAKTIME
import com.example.pomodoroapp.utilities.Constants.POMODOROTIME
import com.example.pomodoroapp.viewmodel.PomodoroViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_pomodorotimer.*
import java.util.stream.Collectors

class PomodoroTimerFragment : Fragment() {
    private val pomodoroViewModel: PomodoroViewModel by viewModels()

    private var _binding: ActivityPomodorotimerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = DataBindingUtil.inflate(inflater, R.layout.activity_pomodorotimer, container, false)
        pomodoroViewModel.timer.value = object : CountDownTimer((pomodoroViewModel.getCurrentStateTime() * 1000).toLong(), 1000) {

            override fun onTick(millisUntilFinished: Long) {

                if(pomodoroViewModel.counter.value?.div(60)!! >=10 ) {
                    if(pomodoroViewModel.counter.value?.rem(60)!! < 10){
                        "${(pomodoroViewModel.counter.value?.div(60))}:0${((pomodoroViewModel.counter.value?.rem(
                            60
                        )))}".also { pomodoroViewModel.timerInfo.value = it }
                    }else{
                        "${(pomodoroViewModel.counter.value?.div(60))}:${((pomodoroViewModel.counter.value?.rem(
                            60
                        )))}".also { pomodoroViewModel.timerInfo.value = it }
                    }
                }else{
                    if(pomodoroViewModel.counter.value?.rem(60)!! < 10){
                        "0${(pomodoroViewModel.counter.value?.div(60))}:0${((pomodoroViewModel.counter.value?.rem(
                            60
                        )))}".also { pomodoroViewModel.timerInfo.value = it }
                    }else{
                        "0${(pomodoroViewModel.counter.value?.div(60))}:${((pomodoroViewModel.counter.value?.rem(
                            60
                        )))}".also { pomodoroViewModel.timerInfo.value = it }
                    }
                }
                pomodoroViewModel.counter.value = pomodoroViewModel.counter.value?.minus(1)
                pomodoroViewModel.progress.value = 100 - ((millisUntilFinished/(10 * pomodoroViewModel.getCurrentStateTime()))).toInt()
            }
            override fun onFinish() {
                "00:00".also { pomodoroViewModel.timerInfo.value = it }
                pomodoroViewModel.pomodoroCounter++

                if(!pomodoroViewModel.isBreak){
                    println( "ascascsacasc" + pomodoroViewModel.progress.value)
                    createNotification()
                    pomodoroViewModel.updateTaskPomodoro(pomodoroViewModel.currenttask)
                }
                pomodoroViewModel.isBreak = !pomodoroViewModel.isBreak
            }
        }


        binding.startPomodoro.setOnClickListener {
            if (pomodoroViewModel.currenttask == "") {
                val t =
                    Toast.makeText(requireContext(), "Nebyl vybrán hlavní úkol", Toast.LENGTH_LONG)
                t.show()
            } else
                startPomodoro()
        }
        binding.stopPomodoro.setOnClickListener {
            cancelPomodoro()
        }
        binding.zapsatUkol.setOnClickListener {
            startActivity(Intent(requireContext(), AddMinorTaskActivity::class.java))
        }
        pomodoroViewModel.counter.value = pomodoroViewModel.getCurrentStateTime()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        binding.lifecycleOwner = this
    }


    private fun setBinding(){
        binding.timer = pomodoroViewModel.timerInfo.value
        pomodoroViewModel.timerInfo.observe(viewLifecycleOwner) {
            binding.timer = pomodoroViewModel.timerInfo.value
        }
        binding.addTaskVisibility = pomodoroViewModel.addTaskVisibility.value!!
        pomodoroViewModel.addTaskVisibility.observe(viewLifecycleOwner) {
            binding.addTaskVisibility = pomodoroViewModel.addTaskVisibility.value!!
        }

        binding.stopPomodoroVisibility = pomodoroViewModel.stopPomodoroVisibility.value!!
        pomodoroViewModel.stopPomodoroVisibility.observe(viewLifecycleOwner) {
            binding.stopPomodoroVisibility = pomodoroViewModel.stopPomodoroVisibility.value!!
        }
        binding.remainingVisibility = pomodoroViewModel.remainingVisibility.value!!
        pomodoroViewModel.remainingVisibility.observe(viewLifecycleOwner) {
            binding.remainingVisibility = pomodoroViewModel.remainingVisibility.value!!
        }
        pomodoroViewModel.startPomodoroVisibility.observe(viewLifecycleOwner) {
            binding.startPomodoroVisibility = pomodoroViewModel.startPomodoroVisibility.value!!
        }

        pomodoroViewModel.progress.observe(viewLifecycleOwner) {
            binding.progress = pomodoroViewModel.progress.value!!
        }

        pomodoroViewModel.allTasks.observe(viewLifecycleOwner) {
            pomodoroViewModel.getAllMainTasks()
            if(pomodoroViewModel.allTasks.value != null) {
                val items = pomodoroViewModel.allTasks.value!!
                    .stream()
                    .map { v -> v.name }
                    .collect(Collectors.toList())
                val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
                binding.autoComplete.setAdapter(adapter)
                binding.autoComplete.setOnItemClickListener { _, _, position, _ ->
                    val value = adapter.getItem(position) ?: ""
                    pomodoroViewModel.currenttask = value
                }
            }
        }

    }


    private fun startTimeCounter() {
        pomodoroViewModel.timer.value = object : CountDownTimer((pomodoroViewModel.getCurrentStateTime() * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                setTimer()
                pomodoroViewModel.counter.value = pomodoroViewModel.counter.value?.minus(1)
                pomodoroViewModel.progress.value = 100 - ((millisUntilFinished/(10 * pomodoroViewModel.getCurrentStateTime()))).toInt()

                println("milis="+ millisUntilFinished + "progress="+pomodoroViewModel.progress.value + "MAX="+pomodoroViewModel.getCurrentStateTime() )
            }
            override fun onFinish() {
                "00:00".also { pomodoroViewModel.timerInfo.value = it }
                pomodoroViewModel.pomodoroCounter++
                pomodoroViewModel.progress.value=100

                if(!pomodoroViewModel.isBreak){
                    createNotification()
                    pomodoroViewModel.updateTaskPomodoro(pomodoroViewModel.currenttask)
                }
                pomodoroViewModel.isBreak = !pomodoroViewModel.isBreak
                pomodoroViewModel.counter.value = pomodoroViewModel.getCurrentStateTime()
                when {
                    pomodoroViewModel.getCurrentStateTime() == POMODOROTIME -> {
                        faze.text = "Práce"
                    }
                    pomodoroViewModel.getCurrentStateTime() == BREAKTIME -> {
                        faze.text = "Přestávka"
                    }
                    else -> {
                        faze.text = "Dlouhá Přestávka"
                    }
                }
                faze.visibility = View.VISIBLE
                val t = Toast.makeText(requireContext(),"Pomodoro dokončeno, čas na přestávku.", Toast.LENGTH_LONG)
                t.show()
            }
        }

        pomodoroViewModel.timer.value?.start()
        pomodoroViewModel.running.value = true;
    }

    private fun startPomodoro() {
        pomodoroViewModel.progress.value = 0
        if (!pomodoroViewModel.running.value!!) {
            pomodoroViewModel.counter.value = pomodoroViewModel.getCurrentStateTime()
            startTimeCounter()
            pomodoroViewModel.stopPomodoroVisibility.value = 0
            pomodoroViewModel.addTaskVisibility.value = 0
            pomodoroViewModel.remainingVisibility.value = 0
            pomodoroViewModel.startPomodoroVisibility.value = 8
            when {
                pomodoroViewModel.getCurrentStateTime() == POMODOROTIME -> {
                    faze.text = "Práce"
                }
                pomodoroViewModel.getCurrentStateTime() == BREAKTIME -> {
                    faze.text = "Přestávka"
                }
                else -> {
                    faze.text = "Dlouhá Přestávka"
                }
            }
            faze.visibility = View.VISIBLE

        }
    }

    private fun cancelPomodoro(){
        pomodoroViewModel.progress.value = 0
        pomodoroViewModel.timer.value?.cancel()
        pomodoroViewModel.running.value = false
        pomodoroViewModel.stopPomodoroVisibility.value = 8
        pomodoroViewModel.addTaskVisibility.value = 8
        pomodoroViewModel.remainingVisibility.value = 8
        pomodoroViewModel.startPomodoroVisibility.value = 0

        pomodoroViewModel.counter.value = pomodoroViewModel.getCurrentStateTime()
        setTimer()
//        pomodoroViewModel.timerInfo.value = "${(pomodoroViewModel.counter.value?.div(60))}:${((pomodoroViewModel.counter.value?.rem(60)))}"
    }

    private fun createNotification(){
        val context = requireContext()
        val mNotificationManager: NotificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(context.applicationContext, "notify_001")
        val ii = Intent(context.applicationContext, LoginActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, ii, 0)

        val bigText = NotificationCompat.BigTextStyle()
        bigText.bigText("Čas na přestávku :)")
        bigText.setBigContentTitle("Konec Pomodora!")
        bigText.setSummaryText("Pomodoro ukončeno")

        mBuilder.setContentIntent(pendingIntent)
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round)
        mBuilder.setContentTitle("Konec Pomodora!")
        mBuilder.setContentText("Čas na přestávku :)")
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

    private fun setTimer(){
        if(pomodoroViewModel.counter.value?.div(60)!! >=10 ) {
            if(pomodoroViewModel.counter.value?.rem(60)!! < 10){
                "${(pomodoroViewModel.counter.value?.div(60))}:0${((pomodoroViewModel.counter.value?.rem(
                    60
                )))}".also { pomodoroViewModel.timerInfo.value = it }
            }else{
                "${(pomodoroViewModel.counter.value?.div(60))}:${((pomodoroViewModel.counter.value?.rem(
                    60
                )))}".also { pomodoroViewModel.timerInfo.value = it }
            }
        }else{
            if(pomodoroViewModel.counter.value?.rem(60)!! < 10){
                "0${(pomodoroViewModel.counter.value?.div(60))}:0${((pomodoroViewModel.counter.value?.rem(
                    60
                )))}".also { pomodoroViewModel.timerInfo.value = it }
            }else{
                "0${(pomodoroViewModel.counter.value?.div(60))}:${((pomodoroViewModel.counter.value?.rem(
                    60
                )))}".also { pomodoroViewModel.timerInfo.value = it }
            }
        }
    }
    fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(requireContext(), "Nemáme přístup k GPS. :(", Toast.LENGTH_SHORT).show()
            return
        }
        pomodoroViewModel.fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    pomodoroViewModel.locationLat = location.latitude
                    pomodoroViewModel.locationLong = location.longitude
                }

            }
    }


}