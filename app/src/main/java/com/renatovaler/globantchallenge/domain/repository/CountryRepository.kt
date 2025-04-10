package com.renatovaler.globantchallenge.domain.repository

import com.renatovaler.globantchallenge.domain.model.Country
import kotlinx.coroutines.flow.Flow

interface CountryRepository {
    fun getAllCountries(): Flow<Result<List<Country>>>
    fun search(query: String): Flow<Result<List<Country>>>
}

