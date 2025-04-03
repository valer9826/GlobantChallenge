package com.renatovaler.globantchallenge.data.repository

import com.renatovaler.globantchallenge.data.api.CountriesApi
import com.renatovaler.globantchallenge.data.mapper.toDomain
import com.renatovaler.globantchallenge.domain.model.Country
import com.renatovaler.globantchallenge.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.collections.map

class CountryRepositoryImpl @Inject constructor(
    private val api: CountriesApi
) : CountryRepository {

    override fun getAll(): Flow<List<Country>> = flow {
        val countries = api.getAll().map { it.toDomain() }
        emit(countries)
    }

    override fun search(query: String): Flow<List<Country>> = flow {
        val result = api.search(query).map { it.toDomain() }
        emit(result)
    }
}

