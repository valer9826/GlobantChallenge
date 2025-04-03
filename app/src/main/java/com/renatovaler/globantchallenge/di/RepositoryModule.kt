package com.renatovaler.globantchallenge.di

import com.renatovaler.globantchallenge.data.remote.api.CountriesApi
import com.renatovaler.globantchallenge.data.repository.CountryRepositoryImpl
import com.renatovaler.globantchallenge.domain.repository.CountryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideCountryRepository(
        api: CountriesApi
    ): CountryRepository {
        return CountryRepositoryImpl(api)
    }
}
