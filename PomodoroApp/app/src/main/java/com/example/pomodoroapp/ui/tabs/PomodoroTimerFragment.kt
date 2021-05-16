package com.example.pomodoroapp.ui.tabs

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.content.ContentValues.TAG
import android.content.Context.NOTIFICATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.pomodoroapp.BuildConfig
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
import android.provider.Settings
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.pomodoroapp.service.LocationService
import com.example.pomodoroapp.utilities.SharedPreferenceUtil
import com.example.pomodoroapp.utilities.toText
import com.google.android.material.snackbar.Snackbar

class PomodoroTimerFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    private val pomodoroViewModel: PomodoroViewModel by viewModels()

    private var _binding: ActivityPomodorotimerBinding? = null
    private val binding get() = _binding!!

    private var foregroundOnlyLocationServiceBound = false
    private val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34

    // Provides location updates for while-in-use feature.
    private var foregroundOnlyLocationService: LocationService? = null

    // Listens for location broadcasts from ForegroundOnlyLocationService.
    private lateinit var foregroundOnlyBroadcastReceiver: ForegroundOnlyBroadcastReceiver

    private lateinit var sharedPreferences: SharedPreferences

    private val foregroundOnlyServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationService.LocalBinder
            foregroundOnlyLocationService = binder.service
            foregroundOnlyLocationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            foregroundOnlyLocationService = null
            foregroundOnlyLocationServiceBound = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.activity_pomodorotimer, container, false)
        pomodoroViewModel.fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        //getLocation()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        binding.lifecycleOwner = this

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
        pomodoroViewModel.counter.value = POMODOROTIME
    }


    private fun setBinding() {
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
            if (pomodoroViewModel.allTasks.value != null) {
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

    override fun onStart() {
        super.onStart()
        sharedPreferences.getBoolean(SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        val serviceIntent = Intent(requireContext(), LocationService::class.java)
        context?.bindService(serviceIntent, foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        foregroundOnlyBroadcastReceiver = ForegroundOnlyBroadcastReceiver()
        sharedPreferences =
            context?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)!!
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            foregroundOnlyBroadcastReceiver,
            IntentFilter(
                LocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST
            )
        )
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(
            foregroundOnlyBroadcastReceiver
        )
        super.onPause()
    }

    override fun onStop() {
        if (foregroundOnlyLocationServiceBound) {
            context?.unbindService(foregroundOnlyServiceConnection)
            foregroundOnlyLocationServiceBound = false
        }
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)

        super.onStop()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        pomodoroViewModel.fusedLocationClient.lastLocation
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful && task.result != null) {

                    pomodoroViewModel.locationLat = task.result.latitude
                    pomodoroViewModel.locationLong = task.result.longitude


                } else {
                    Log.w(TAG, "getLastLocation:exception", task.exception)
                    showMessage("Nemame pristup k poloze. Exception: " + task.exception)
                }
            }
    }

    private fun showSnackbar(
        mainTextStringId: Int, actionStringId: Int,
        listener: View.OnClickListener
    ) {

        Toast.makeText(requireContext(), getString(mainTextStringId), Toast.LENGTH_LONG).show()
    }

    private fun showMessage(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
    }


    private fun startTimeCounter() {
        pomodoroViewModel.timer.value = object :
            CountDownTimer((pomodoroViewModel.getCurrentStateTime() * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                setTimer()
                pomodoroViewModel.counter.value = pomodoroViewModel.counter.value?.minus(1)
                pomodoroViewModel.progress.value =
                    100 - ((millisUntilFinished / (10 * pomodoroViewModel.getCurrentStateTime()))).toInt()
            }

            override fun onFinish() {
                "00:00".also { pomodoroViewModel.timerInfo.value = it }

                pomodoroViewModel.progress.value = 100
                pomodoroViewModel.pomodoroCounter++

                if (!pomodoroViewModel.isBreak) {
                    val enabled = sharedPreferences.getBoolean(
                        SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false)

                    if (enabled) {
                        foregroundOnlyLocationService?.unsubscribeToLocationUpdates()
                    } else {

                        // TODO: Step 1.0, Review Permissions: Checks and requests if needed.
                        if (foregroundPermissionApproved()) {
                            foregroundOnlyLocationService?.subscribeToLocationUpdates()
                                ?: Log.d(TAG, "Service Not Bound")
                        } else {
                            requestForegroundPermissions()
                        }
                    }
                    getLastLocation()
                    createNotification()
                    pomodoroViewModel.updateTaskPomodoro(pomodoroViewModel.currenttask)
                }
                pomodoroViewModel.isBreak = !pomodoroViewModel.isBreak
                pomodoroViewModel.counter.value = pomodoroViewModel.getCurrentStateTime()
                when {
                    pomodoroViewModel.getCurrentStateTime() == POMODOROTIME -> {
                        faze?.let {
                            it.text = "Práce"
                        }
                    }
                    pomodoroViewModel.getCurrentStateTime() == BREAKTIME -> {
                        faze?.let {
                            it.text = "Přestávka"
                        }
                    }
                    else -> {
                        faze?.let {
                            it.text = "Dlouhá přestávka"
                        }
                    }
                }

                faze?.let {
                    it.visibility = View.VISIBLE
                }
                val t = Toast.makeText(
                    requireContext(),
                    "Pomodoro dokončeno, čas na přestávku.",
                    Toast.LENGTH_LONG
                )
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
                    faze?.let {
                        it.text = "Práce"
                    }
                }
                pomodoroViewModel.getCurrentStateTime() == BREAKTIME -> {
                    faze?.let {
                        it.text = "Přestávka"
                    }
                }
                else -> {
                    faze?.let {
                        it.text = "Dlouhá Přestávka"
                    }
                }
            }

            faze?.let {
                it.visibility = View.VISIBLE
            }

        }
    }

    private fun cancelPomodoro() {
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

    private fun createNotification() {
        val context = requireContext()
        val mNotificationManager: NotificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
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

    private fun setTimer() {
        if (pomodoroViewModel.counter.value?.div(60)!! >= 10) {
            if (pomodoroViewModel.counter.value?.rem(60)!! < 10) {
                "${(pomodoroViewModel.counter.value?.div(60))}:0${
                    ((pomodoroViewModel.counter.value?.rem(
                        60
                    )))
                }".also { pomodoroViewModel.timerInfo.value = it }
            } else {
                "${(pomodoroViewModel.counter.value?.div(60))}:${
                    ((pomodoroViewModel.counter.value?.rem(
                        60
                    )))
                }".also { pomodoroViewModel.timerInfo.value = it }
            }
        } else {
            if (pomodoroViewModel.counter.value?.rem(60)!! < 10) {
                "0${(pomodoroViewModel.counter.value?.div(60))}:0${
                    ((pomodoroViewModel.counter.value?.rem(
                        60
                    )))
                }".also { pomodoroViewModel.timerInfo.value = it }
            } else {
                "0${(pomodoroViewModel.counter.value?.div(60))}:${
                    ((pomodoroViewModel.counter.value?.rem(
                        60
                    )))
                }".also { pomodoroViewModel.timerInfo.value = it }
            }
        }
    }

    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            pomodoroViewModel.locationPermissionCode
        )
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")

            showSnackbar(
                R.string.permission_rationale, android.R.string.ok
            ) {
                // Request permission
                startLocationPermissionRequest()
            }

        } else {
            Log.i(TAG, "Requesting permission")
            startLocationPermissionRequest()
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == pomodoroViewModel.locationPermissionCode) {
            if (grantResults.isEmpty()) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.")
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation()
            } else {
                // Permission denied.
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                    View.OnClickListener {
                        // Build intent that displays the App settings screen.
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts(
                            "package",
                            BuildConfig.APPLICATION_ID, null
                        )
                        intent.data = uri
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    })
            }
        }
    }

    fun getLocation() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            pomodoroViewModel.locationPermissionCode
        )
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                requireContext(),
                "Nemáme přístup k poloze. Ukončené pomodora nebudou ukazovat polohu. :(",
                Toast.LENGTH_SHORT
            ).show()
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

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        // Updates button states if new while in use location is added to SharedPreferences.
        if (key == SharedPreferenceUtil.KEY_FOREGROUND_ENABLED) {
            sharedPreferences?.getBoolean(
                SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false

            )
        }
    }

    private fun foregroundPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun requestForegroundPermissions() {
        val provideRationale = foregroundPermissionApproved()

        // If the user denied a previous request, but didn't check "Don't ask again", provide
        // additional rationale.
        if (provideRationale) {
            Snackbar.make(
                requireView().findViewById(R.id.pomodoroTimerFragment2),
                R.string.permission_rationale,
                Snackbar.LENGTH_LONG
            )
                .setAction(R.string.OK) {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
                    )
                }
                .show()
        } else {
            Log.d(TAG, "Request foreground only permission")
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
        }
    }


    private inner class ForegroundOnlyBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(
                LocationService.EXTRA_LOCATION
            )

            if (location != null) {
                Toast.makeText(requireContext(), "Foreground location: ${location.toText()}", Toast.LENGTH_LONG).show()
            }
        }
    }



}