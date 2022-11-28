package com.sophiadiagrams.avedex.lib.services.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import java.util.*

class LocationService(private var fusedLocationClient: FusedLocationProviderClient) {

    @SuppressLint("MissingPermission") // PORQUE SE PIDE EN LA CAMARA LOS PERMISOS DE LOCATION
    fun updateLocation(context: Context, document: DocumentReference) {
        fusedLocationClient.lastLocation.addOnSuccessListener {
            Log.d("LOCATION", "$it")
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses: List<Address> =
                geocoder.getFromLocation(
                    it!!.latitude,
                    it.longitude,
                    1
                ) as List<Address>
            val country = if(addresses[0].countryName!=null) addresses[0].countryName else "Location not found"
            val data = hashMapOf(
                "discoveryLocation" to country
            )
            document.set(data, SetOptions.merge())
            Log.d("FB", "Location successfully written in the db!")
        }
    }
}