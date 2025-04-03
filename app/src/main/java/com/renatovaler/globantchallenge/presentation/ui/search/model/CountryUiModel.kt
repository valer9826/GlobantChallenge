package com.renatovaler.globantchallenge.presentation.ui.search.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CountryUiModel(
    val commonName: String,
    val officialName: String,
    val capital: String,
    val flagUrl: String,
    val region: String = "",
    val subregion: String? = "",
    val coatOfArmsUrl: String? = "",
    val population: String? = "",
    val languages: String? = "",
    val currencies: String? = "",
    val carSide: String? = ""
) : Parcelable
