package com.sophiadiagrams.avedex.lib.services.retrofit

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitService {
    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://avedex.bepi.tech/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    suspend fun postBirdsData(photo: String): BirdsResponse? {

        val call = getRetrofit().create(APIService::class.java).postBirdsBitmap(Bird(name = photo))
        return if (call.isSuccessful) call.body()
        else null
    }

    data class Bird(@SerializedName("name") var name: String)
}