package com.windrr.mindbank.api

import com.windrr.mindbank.api.model.AdviceResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("/advice")
    suspend fun fetchAdvice(): Response<AdviceResponse>
}