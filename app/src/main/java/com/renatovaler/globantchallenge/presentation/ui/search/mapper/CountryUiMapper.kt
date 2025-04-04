package com.renatovaler.globantchallenge.presentation.ui.search.mapper

import com.renatovaler.globantchallenge.domain.model.Country
import com.renatovaler.globantchallenge.presentation.ui.search.model.CountryUiModel

fun Country.toUiModel(): CountryUiModel {
    return CountryUiModel(
        commonName = commonName,
        officialName = officialName,
        capital = capital,
        flagUrl = flagUrl,
        region = region ?: "",
        subregion = subregion,
        coatOfArmsUrl = coatOfArmsUrl,
        population = population.toString(),
        languages = languages,
        currencies = currencies,
        carSide = carSide
    )
}
