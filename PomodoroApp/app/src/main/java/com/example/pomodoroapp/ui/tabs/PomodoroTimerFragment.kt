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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.pomodoroapp.R
import com.example.pomodoroapp.databinding.ActivityPomodorotimerBinding
import com.example.pomodoroapp.ui.AddMinorTaskActivity
import com.example.pomodoroapp.ui.LoginActivity
import com.example.pomodoroapp.viewmodel.PomodoroViewModel
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
                "${(pomodoroViewModel.counter.value?.div(60))}:${((pomodoroViewModel.counter.value?.rem(
                    60
                )))}".also { pomodoroViewModel.timerInfo.value = it }
                pomodoroViewModel.counter.value = pomodoroViewModel.counter.value?.minus(1)
                pomodoroViewModel.progress.value = 100 - ((millisUntilFinished/(10 * pomodoroViewModel.getCurrentStateTime()))).toInt()

            }
            override fun onFinish() {
                "00:00".also { pomodoroViewModel.timerInfo.value = it }
                pomodoroViewModel.pomodoroCounter++
                if(!pomodoroViewModel.isBreak){
                    createNotification()
                    pomodoroViewModel.updateTaskPomodoro(pomodoroViewModel.currenttask)
                }
                pomodoroViewModel.isBreak = !pomodoroViewModel.isBreak

            }
        }


        binding.startPomodoro.setOnClickListener{
            if(pomodoroViewModel.currenttask == ""){
                val t = Toast.makeText(requireContext(),"Nebyl vybrán hlavní úkol", Toast.LENGTH_LONG)
                t.show()
            }
            else
                startPomodoro()
        }
        binding.stopPomodoro.setOnClickListener{
            cancelPomodoro()
        }
        binding.zapsatUkol.setOnClickListener{
            startActivity(Intent(requireContext(), AddMinorTaskActivity::class.java))
        }

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
        pomodoroViewModel.timerInfo.value = "${(pomodoroViewModel.counter.value?.div(60))}:${((pomodoroViewModel.counter.value?.rem(60)))}"
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


}