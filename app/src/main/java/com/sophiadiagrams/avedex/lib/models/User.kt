package com.sophiadiagrams.avedex.lib.models

data class User(
    var uid: String = "",
    var email: String  = "",
    var displayName: String  = "",
    var photoUrl: String = "",
    val recognizedBirds: List<Int> = emptyList()
)