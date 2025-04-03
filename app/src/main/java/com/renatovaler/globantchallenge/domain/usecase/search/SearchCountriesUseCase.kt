package com.renatovaler.globantchallenge.domain.usecase.search
import com.renatovaler.globantchallenge.domain.model.Country
import kotlinx.coroutines.flow.Flow

interface SearchCountriesUseCase {
    operator fun invoke(query: String): Flow<Result<List<Country>>>
}

