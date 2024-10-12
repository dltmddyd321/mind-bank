package com.example.mindbank.api

import com.example.mindbank.api.model.AdviceResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("/advice")
    suspend fun fetchAdvice(): Response<AdviceResponse>
}