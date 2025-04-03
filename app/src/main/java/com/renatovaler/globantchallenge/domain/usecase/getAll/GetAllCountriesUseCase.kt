package com.renatovaler.globantchallenge.domain.usecase.getAll

import com.renatovaler.globantchallenge.domain.model.Country
import kotlinx.coroutines.flow.Flow

interface GetAllCountriesUseCase {
    operator fun invoke(): Flow<List<Country>>
}