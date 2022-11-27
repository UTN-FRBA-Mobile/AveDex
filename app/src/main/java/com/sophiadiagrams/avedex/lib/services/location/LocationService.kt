package com.sophiadiagrams.avedex.lib.services.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.runBlocking
import java.util.*

class LocationService(private var fusedLocationClient: FusedLocationProviderClient) {

    @SuppressLint("MissingPermission") // PORQUE SE PIDE EN LA CAMARA LOS PERMISOS DE LOCATION
    fun getFullLocation(context: Context): String {
        var loc = ""
        runBlocking {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                Log.d("LOCATION", "$location")
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses: List<Address> =
                    geocoder.getFromLocation(
                        location!!.latitude,
                        location!!.longitude,
                        1
                    ) as List<Address>
                val city = addresses[0].locality
                val state = addresses[0].adminArea
                val country = addresses[0].countryName
                loc = "$city, $state, $country"
                //ACA actualizar location en doc de firebase
            }
        }
        return loc
    }
}