package com.sophiadiagrams.avedex.lib.services.retrofit

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface APIService {
    @POST("ai")
    suspend fun postBirdsBitmap(@Body requestBody: RetrofitService.Bird): Response<BirdsResponse>
}
