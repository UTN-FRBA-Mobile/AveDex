package com.sophiadiagrams.avedex.lib.services.location

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import java.net.URL
import java.util.*

class LocationService(private var fusedLocationClient: FusedLocationProviderClient) {

    @SuppressLint("MissingPermission") // PORQUE SE PIDE EN LA CAMARA LOS PERMISOS DE LOCATION
    fun getFullLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                Log.d("LOCATION", "$location")

                // NO FUNCIONT TODO:
//                val res = Scanner(URL("http://ip-api.com/json").openStream(), "UTF-8").useDelimiter("\\A").next()
//                Log.d("LOCATIN2", res)
            }
    }
}