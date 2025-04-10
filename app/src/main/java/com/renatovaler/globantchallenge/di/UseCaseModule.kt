package com.renatovaler.globantchallenge.di

import com.renatovaler.globantchallenge.domain.repository.CountryRepository
import com.renatovaler.globantchallenge.domain.usecase.search.SearchCountriesUseCase
import com.renatovaler.globantchallenge.domain.usecase.search.SearchCountriesUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideSearchCountriesUseCase(
        repository: CountryRepository
    ): SearchCountriesUseCase {
        return SearchCountriesUseCaseImpl(repository)
    }
}
