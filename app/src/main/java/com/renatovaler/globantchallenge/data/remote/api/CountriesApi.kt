package com.renatovaler.globantchallenge.data.remote.api

import com.renatovaler.globantchallenge.data.dto.CountryDto
import retrofit2.http.GET
import retrofit2.http.Path

interface CountriesApi {
    @GET("all")
    suspend fun getAll(): List<CountryDto>

    @GET("name/{name}")
    suspend fun search(@Path("name") query: String): List<CountryDto>
}
