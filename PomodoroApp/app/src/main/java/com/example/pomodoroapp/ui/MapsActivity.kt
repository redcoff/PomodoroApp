package com.example.pomodoroapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pomodoroapp.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        //set back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val lat = intent.getDoubleExtra("lat", 0.0)
        val long = intent.getDoubleExtra("long", 0.0)

        val point = LatLng(lat, long)
        mMap.addMarker(MarkerOptions().position(point).title("Místo splnění pomodora"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(point))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}