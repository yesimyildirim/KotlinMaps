package com.yesimyildirim.kotlinmaps

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception
import java.util.*
import java.util.jar.Manifest

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener : LocationListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(myListener)
        /*
        // Add a marker in Sydney and move the camera
       val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        */

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object :LocationListener{
            override fun onLocationChanged(location: Location?) {

                if(location != null){
                    mMap.clear()
                    val userLocation = LatLng(location.latitude,location.longitude)
                    mMap.addMarker(MarkerOptions().position(userLocation).title("Your Location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f))

                    val geocoder= Geocoder(this@MapsActivity, Locale.getDefault())
                    try {

                        val addressList =geocoder.getFromLocation(location.latitude,location.longitude,1)

                        if(addressList != null && addressList.size >0){

                            println(addressList.get(0).toString())
                        }

                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                TODO("Not yet implemented")
            }

            override fun onProviderEnabled(provider: String?) {
                TODO("Not yet implemented")
            }

            override fun onProviderDisabled(provider: String?) {
                TODO("Not yet implemented")
            }
        }

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
        }else{

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)

            val lastLocation =locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            if(lastLocation!=null){

                val lastKnowLatLng= LatLng(lastLocation.latitude,lastLocation.longitude)
                mMap.addMarker(MarkerOptions().position(lastKnowLatLng).title("Your Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnowLatLng,15f))
            }

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if(requestCode == 1){
            if(grantResults.size > 0){
                if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    val myListener = object :GoogleMap.OnMapLongClickListener{
        override fun onMapLongClick(p0: LatLng?) {
            mMap.clear()
            val geocoder = Geocoder(this@MapsActivity,Locale.getDefault())
            if(p0 !=null) {
                var address = ""
                try {


                    val addressList = geocoder.getFromLocation(p0.latitude, p0.longitude, 1)
                    if(addressList != null && addressList.size>0){
                        if(addressList[0].thoroughfare != null){
                            address += addressList[0].thoroughfare
                            if(addressList[0].subThoroughfare != null){
                                address += addressList[0].subThoroughfare

                            }
                        }
                    }


                }catch (e:Exception){
                    e.printStackTrace()
                }
                mMap.addMarker(MarkerOptions().position(p0).title(address))
            }else{
                Toast.makeText(applicationContext,"Try Again Later",Toast.LENGTH_LONG).show()
            }
        }
    }
}


