package com.renatovaler.globantchallenge.presentation.ui.search.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.renatovaler.globantchallenge.presentation.ui.search.model.CountryUiModel

@Composable
fun CountryItem(
    country: CountryUiModel,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = country.flagUrl),
            contentDescription = null,
            modifier = Modifier.size(65.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(text = country.commonName, style = MaterialTheme.typography.titleMedium)
            Text(text = country.officialName, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = country.capital,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Preview
@Composable
private fun CountryItemPreview() {

    CountryItem(
        country = CountryUiModel(
            officialName = "Peru",
            commonName = "Peru",
            capital = "Lima",
            flagUrl = ""
        )
    ) { }
}