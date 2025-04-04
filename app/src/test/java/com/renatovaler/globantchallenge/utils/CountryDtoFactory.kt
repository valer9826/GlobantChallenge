package com.renatovaler.globantchallenge.utils

import com.renatovaler.globantchallenge.data.dto.*

object CountryDtoFactory {

    fun peru() = CountryDto(
        name = NameDto("Peru", "Republic of Peru"),
        capital = listOf("Lima"),
        region = "Americas",
        subregion = "South America",
        flags = FlagsDto("https://flag.png"),
        coatOfArms = null,
        population = 33_000_000L,
        languages = mapOf("spa" to "Spanish"),
        currencies = mapOf("PEN" to CurrencyDto("Peruvian sol", "S/")),
        car = CarDto("right")
    )

    fun grenada() = CountryDto(
        name = NameDto("Grenada", "Grenada"),
        capital = listOf("St. George's"),
        region = "Americas",
        subregion = "Caribbean",
        flags = FlagsDto("https://flagcdn.com/w320/gd.png"),
        coatOfArms = CoatOfArmsDto("https://mainfacts.com/media/images/coats_of_arms/gd.png"),
        population = 112_519L,
        languages = mapOf("eng" to "English"),
        currencies = mapOf("XCD" to CurrencyDto("Eastern Caribbean dollar", "$")),
        car = CarDto("left")
    )

    fun guyana() = CountryDto(
        name = NameDto("Guyana", "Co-operative Republic of Guyana"),
        capital = listOf("Georgetown"),
        region = "Americas",
        subregion = "South America",
        flags = FlagsDto("https://flagcdn.com/w320/gy.png"),
        coatOfArms = CoatOfArmsDto("https://mainfacts.com/media/images/coats_of_arms/gy.png"),
        population = 786_559L,
        languages = mapOf("eng" to "English"),
        currencies = mapOf("GYD" to CurrencyDto("Guyanese dollar", "$")),
        car = CarDto("left")
    )

    fun allCountries() = listOf(peru(), grenada())
    fun searchCountries() = listOf(peru(), guyana())
}
