package com.renatovaler.globantchallenge.data.dto

data class CountryDto(
    val name: NameDto,
    val capital: List<String>?,
    val region: String?,
    val subregion: String?,
    val flags: FlagsDto,
    val coatOfArms: CoatOfArmsDto?,
    val population: Long,
    val languages: Map<String, String>?,
    val currencies: Map<String, CurrencyDto>?,
    val car: CarDto
)

