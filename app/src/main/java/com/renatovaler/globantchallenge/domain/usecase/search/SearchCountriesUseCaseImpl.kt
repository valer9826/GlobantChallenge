package com.renatovaler.globantchallenge.domain.usecase.search

import com.renatovaler.globantchallenge.domain.model.Country
import com.renatovaler.globantchallenge.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class SearchCountriesUseCaseImpl @Inject constructor(
    private val repository: CountryRepository
) : SearchCountriesUseCase {

    override fun invoke(query: String): Flow<Result<List<Country>>> {
        return if (query.length >= 2) {
            repository.search(query)
        } else {
            flowOf(Result.success(emptyList()))
        }
    }
}

