package com.renatovaler.globantchallenge.domain.model

data class Country(
    val commonName: String,
    val officialName: String,
    val capital: String,
    val flagUrl: String,
    val region: String? = "",
    val subregion: String? = "",
    val coatOfArmsUrl: String? = "",
    val population: Long? = 0,
    val languages: String? = "",
    val currencies: String? = "",
    val carSide: String? = ""
)
