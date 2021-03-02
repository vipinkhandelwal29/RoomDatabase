package com.example.roomdatabase

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.roomdatabase.R.layout.activity_map
import com.example.roomdatabase.databinding.ActivityMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*


class MapActivity : BaseActivity<ActivityMapBinding>(), OnMapReadyCallback{

    override fun getLayoutId() = activity_map

    private lateinit var currentLocation: Location
    private var mMap: GoogleMap? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101

    override fun initControl() {

        setSupportActionBar(binding.iToolbar.toolbar)
        setTitle("Select Location")
        checkConnectivity()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()





    }


    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode
            )
            return
        }
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener {
            if (it != null) {
                currentLocation = it
                Toast.makeText(
                    applicationContext, currentLocation.latitude.toString() + "" +
                            currentLocation.longitude, Toast.LENGTH_SHORT
                ).show()


                val geocoder: Geocoder
                val addresses: List<Address?>
                geocoder = Geocoder(this, Locale.getDefault())
                addresses = geocoder.getFromLocation(
                    currentLocation.latitude,
                    currentLocation.longitude,
                    1
                )

                val address = addresses[0]
                    ?.getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                val city = addresses[0]!!.locality
                val state = addresses[0]!!.adminArea
                val country = addresses[0]!!.countryName
                val postalCode = addresses[0]!!.postalCode
                val knownName = addresses[0]!!.featureName

                binding.etMap.text = address

                binding.btnSubmit.setOnClickListener {
                    val intent = Intent()
                    val flatno = binding.etHouse.text.toString()
                    if (flatno.isBlank()) {
                        messageShow("please enter House No./Flat")
                    }else
                    {
                        intent.putExtra("latitude", currentLocation.latitude)
                        intent.putExtra("longitude", currentLocation.longitude)
                        intent.putExtra("flatno", "$flatno ,$address")

                        Log.d("==>", "fetchLocation: $flatno,$address")
                        Log.d("==>latitude", "${currentLocation.latitude}")
                        Log.d("==>latitude", "${currentLocation.longitude}")
                        setResult(500, intent)
                        finish()
                    }
                }

                val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.map) as
                        SupportMapFragment?)!!
                supportMapFragment.getMapAsync(this)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        /*if (isPermissionGiven()){
            googleMap?.isMyLocationEnabled = true
            googleMap?.uiSettings.isMyLocationButtonEnabled = true
            googleMap?.uiSettings.isZoomControlsEnabled = true
            getCurrentLocation()
        } else {
            givePermission()
        }
        googleMap?.setOnMarkerDragListener(this)*/
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val markerOptions = MarkerOptions().position(latLng).title("Vipin is here!")
        googleMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5f))
        googleMap?.addMarker(markerOptions)
        val sydney = LatLng(-33.852, 151.211)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            onBackPressed()
            return true
            /*  Toast.makeText(this, "ActionClicked", Toast.LENGTH_LONG).show()
              Log.d("btn", "menuBtnAdd  ")*/
        } else {

            return super.onOptionsItemSelected(item)
        }
    }

}



