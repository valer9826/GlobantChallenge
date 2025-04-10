package com.renatovaler.globantchallenge.data.repository

import com.renatovaler.globantchallenge.core.network.safeApiCall
import com.renatovaler.globantchallenge.data.remote.api.CountriesApi
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

    override fun getAllCountries(): Flow<Result<List<Country>>> = flow {
        val result = safeApiCall {
            api.getAll().map { it.toDomain() }
        }
        emit(result)
    }

    override fun search(query: String): Flow<Result<List<Country>>> = flow {
        val result = safeApiCall {
            api.search(query).map { it.toDomain() }
        }
        emit(result)
    }

}

