package com.renatovaler.globantchallenge.presentation.ui.search.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.renatovaler.globantchallenge.presentation.ui.search.SearchIntent
import com.renatovaler.globantchallenge.presentation.ui.search.SearchState
import com.renatovaler.globantchallenge.presentation.ui.search.components.CountryItem
import com.renatovaler.globantchallenge.presentation.ui.search.model.CountryUiModel

@Composable
fun SearchScreen(
    state: SearchState,
    onIntent: (SearchIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = state.query,
            onValueChange = { onIntent(SearchIntent.OnQueryChanged(it)) },
            label = { Text("Buscar país") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(
                        items = state.results,
                        key = { it.commonName }
                    ) { country ->
                        CountryItem(
                            country = country,
                            onClick = {
                                onIntent(SearchIntent.OnCountryClicked(country))
                            }
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewSearchScreen() {
    val fakeState = SearchState(
        query = "pe",
        results = listOf(
            CountryUiModel(
                officialName = "Republic of Peru",
                commonName = "Peru",
                capital = "Lima",
                flagUrl = "https://flagcdn.com/w320/pe.png"
            )
        )
    )
    SearchScreen(state = fakeState, onIntent = {}, modifier = Modifier)
}
