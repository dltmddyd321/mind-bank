package com.example.mindbank.api.model

data class AdviceResultModel(
    val query: String,
    val slips: List<Slip>,
    val total_results: String
)