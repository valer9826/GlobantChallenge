package com.renatovaler.globantchallenge.presentation.ui.search.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.renatovaler.globantchallenge.presentation.ui.search.SearchIntent
import com.renatovaler.globantchallenge.presentation.ui.search.SearchViewModel
import com.renatovaler.globantchallenge.presentation.ui.search.model.CountryUiModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@OptIn(ExperimentalMaterial3Api::class)
@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun SearchRootScreen(
    onNavigateToDetail: (CountryUiModel) -> Unit
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier
                    .statusBarsPadding(),
                title = {
                    Text(
                        text = "Countries",
                        fontSize = 30.sp
                    )
                }
            )
        }
    ) { innerPadding ->

        SearchScreen(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            state = state,
            onIntent = { intent ->
                when (intent) {
                    is SearchIntent.OnQueryChanged -> viewModel.onIntent(intent)
                    is SearchIntent.OnCountryClicked -> onNavigateToDetail(intent.countryUiModel)
                }
            }
        )
    }
}