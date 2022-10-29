package com.sophiadiagrams.avedex.lib.models

data class User(
    var uid: String = "",
    var email: String  = "",
    var displayName: String  = "",
    var photoUrl: String = "",
    var recognizedBirds: MutableList<Bird> = mutableListOf()
)