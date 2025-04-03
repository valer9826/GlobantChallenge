package com.renatovaler.globantchallenge.domain.usecase.getAll

import com.renatovaler.globantchallenge.domain.model.Country
import com.renatovaler.globantchallenge.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCountriesUseCaseImpl @Inject constructor(
    private val repository: CountryRepository
) : GetAllCountriesUseCase {

    override operator fun invoke(): Flow<Result<List<Country>>> {
        return repository.getAll()
    }

}
