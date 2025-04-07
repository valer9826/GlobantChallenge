package com.renatovaler.globantchallenge.utils

import com.renatovaler.globantchallenge.domain.model.Country

object CountryFactory {

    fun peru() = create(
        commonName = "Peru",
        officialName = "Republic of Peru",
        capital = "Lima",
        region = "Americas",
        subregion = "South America",
        flagUrl = "https://flag.png",
        coatOfArmsUrl = null,
        population = 33_000_000L,
        languages = "Spanish",
        currencies = "Peruvian sol",
        carSide = "right"
    )

    fun grenada() = create(
        commonName = "Grenada",
        officialName = "Grenada",
        capital = "St. George's",
        region = "Americas",
        subregion = "Caribbean",
        flagUrl = "https://flagcdn.com/w320/gd.png",
        coatOfArmsUrl = "https://mainfacts.com/media/images/coats_of_arms/gd.png",
        population = 112_519L,
        languages = "English",
        currencies = "Eastern Caribbean dollar",
        carSide = "left"
    )

    fun guyana() = create(
        commonName = "Guyana",
        officialName = "Co-operative Republic of Guyana",
        capital = "Georgetown",
        region = "Americas",
        subregion = "South America",
        flagUrl = "https://flagcdn.com/w320/gy.png",
        coatOfArmsUrl = "https://mainfacts.com/media/images/coats_of_arms/gy.png",
        population = 786_559L,
        languages = "English",
        currencies = "Guyanese dollar",
        carSide = "left"
    )

    fun someCountries() = listOf(peru(), grenada())

    fun countriesWithNameContainingPer() = listOf(peru(), guyana())

    fun create(
        commonName: String = "Default",
        officialName: String = "Official Default",
        capital: String = "Default Capital",
        region: String = "Default Region",
        subregion: String = "Default Subregion",
        flagUrl: String = "https://flag.png",
        coatOfArmsUrl: String? = null,
        population: Long = 0,
        languages: String = "DefaultLang",
        currencies: String = "Dollar",
        carSide: String = "right"
    ): Country {
        return Country(
            commonName = commonName,
            officialName = officialName,
            capital = capital,
            region = region,
            subregion = subregion,
            flagUrl = flagUrl,
            coatOfArmsUrl = coatOfArmsUrl,
            population = population,
            languages = languages,
            currencies = currencies,
            carSide = carSide
        )
    }
}
