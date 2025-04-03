package com.renatovaler.globantchallenge.presentation.ui.search.screens

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.renatovaler.globantchallenge.presentation.ui.search.SearchViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun SearchRootScreen() {
    val viewModel: SearchViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    SearchScreen(
        state = state,
        onIntent = viewModel::onIntent
    )
}

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@Preview
@Composable
private fun SearchRootPreview() {
    SearchRootScreen()
}
