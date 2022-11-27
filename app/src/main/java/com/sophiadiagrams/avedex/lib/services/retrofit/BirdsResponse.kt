package com.sophiadiagrams.avedex.lib.services.retrofit

import com.google.gson.annotations.SerializedName

data class BirdsResponse(
    @SerializedName("name") var name: String,
    @SerializedName("imageUrl") var url: String?,
    @SerializedName("description") var description: String?,
    @SerializedName("source") var source: String?,
)