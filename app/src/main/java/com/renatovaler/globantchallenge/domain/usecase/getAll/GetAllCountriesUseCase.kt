package com.renatovaler.globantchallenge.domain.usecase.getAll

import com.renatovaler.globantchallenge.domain.model.Country
import kotlinx.coroutines.flow.Flow

interface GetAllCountriesUseCase {
    suspend operator fun invoke(): Flow<List<Country>>
}