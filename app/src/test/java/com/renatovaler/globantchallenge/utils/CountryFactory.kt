package com.renatovaler.globantchallenge.utils

import com.renatovaler.globantchallenge.domain.model.Country

object CountryFactory {
    fun peru() = Country(
        commonName = "Peru",
        officialName = "Republic of Peru",
        capital = "Lima",
        flagUrl = "https://flag.png",
        region = "Americas",
        subregion = "South America",
        coatOfArmsUrl = null,
        population = 33000000L,
        languages = "Spanish",
        currencies = "Peruvian sol",
        carSide = "right"
    )

    fun grenada() = Country(
        commonName = "Grenada",
        officialName = "Grenada",
        capital = "St. George's",
        flagUrl = "https://flagcdn.com/w320/gd.png",
        region = "Americas",
        subregion = "Caribbean",
        coatOfArmsUrl = "https://mainfacts.com/media/images/coats_of_arms/gd.png",
        population = 112519L,
        languages = "English",
        currencies = "Eastern Caribbean dollar",
        carSide = "left"
    )

    fun someCountries() = listOf(peru(), grenada())
}
