package com.example.mindbank.api

import com.example.mindbank.api.model.AdviceResultModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("/advice/search/{query}")
    suspend fun fetchAdvice(
        @Path("query") query: String = "destination"
    ): Response<AdviceResultModel>
}