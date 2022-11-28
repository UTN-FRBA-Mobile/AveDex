package com.sophiadiagrams.avedex.lib.models

// FIREBASE NECESITA QUE TENGA UN CONSTRUCTOR VACIO O TODSO DEFAULT VALUES
data class Bird(
    val user: String? = null,
    val name: String = "",
    val discoveryLocation: String? = null,
    val discoveryTime: String? = null
)