package com.sophiadiagrams.avedex.lib.services.location

data class Location(
        val status: String,
        val country: String,
        val countryCode: String,
        val region: String,
        val regionName: String,
        val city: String,
        val zip: String,
        val lat: Double,
        val lon: Double,
        val timezone: String,
        val isp: String,
        val org: String,
        val query: String
    ) {

}