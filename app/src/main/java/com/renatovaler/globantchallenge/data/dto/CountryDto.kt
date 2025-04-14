package com.renatovaler.globantchallenge.data.dto

data class CountryDto(
    val name: NameDto? = null,
    val capital: List<String>? = null,
    val region: String? = null,
    val subregion: String? = null,
    val flags: FlagsDto? = null,
    val coatOfArms: CoatOfArmsDto? = null,
    val population: Long? = null,
    val languages: Map<String, String>? = null,
    val currencies: Map<String, CurrencyDto>? = null,
    val car: CarDto? = null
)

