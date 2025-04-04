package com.renatovaler.globantchallenge.data.mapper

import com.renatovaler.globantchallenge.data.dto.CountryDto
import com.renatovaler.globantchallenge.domain.model.Country

fun CountryDto.toDomain(): Country {
    val capital = capital?.firstOrNull() ?: "No Capital"
    val languages = languages?.values?.joinToString() ?: "No Info"
    val currencies = currencies?.map { (code, data) ->
        "$code (${data.name ?: "Unknown"})"
    }?.joinToString() ?: "No Info"
    val carSide = car.side.replaceFirstChar { it.uppercaseChar() }

    return Country(
        commonName = name.common,
        officialName = name.official,
        capital = capital,
        region = region ?: "No Info",
        subregion = subregion ?: "No Info",
        flagUrl = flags.png,
        coatOfArmsUrl = coatOfArms?.png,
        population = population,
        languages = languages,
        currencies = currencies,
        carSide = carSide
    )
}
