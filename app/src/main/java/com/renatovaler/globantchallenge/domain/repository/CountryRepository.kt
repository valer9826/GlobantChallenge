package com.renatovaler.globantchallenge.domain.repository

import com.renatovaler.globantchallenge.domain.model.Country
import kotlinx.coroutines.flow.Flow

interface CountryRepository {
    fun getAll(): Flow<List<Country>>
    fun search(query: String): Flow<List<Country>>
}

