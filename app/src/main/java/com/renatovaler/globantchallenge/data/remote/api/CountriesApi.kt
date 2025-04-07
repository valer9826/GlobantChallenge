package com.renatovaler.globantchallenge.data.remote.api

import com.renatovaler.globantchallenge.data.dto.CountryDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CountriesApi {
    @GET("all")
    suspend fun getAll(
        @Query("fields") fields: String = DEFAULT_FIELDS
    ): List<CountryDto>

    @GET("name/{name}")
    suspend fun search(@Path("name") query: String): List<CountryDto>

    companion object {
        private const val DEFAULT_FIELDS =
            "name,capital,region,subregion,flags,coatOfArms,population,languages,currencies,car"
    }
}

