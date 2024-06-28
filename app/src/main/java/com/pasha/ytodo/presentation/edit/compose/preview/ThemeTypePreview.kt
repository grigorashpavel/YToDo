package com.pasha.ytodo.presentation.edit.compose.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.pasha.ytodo.presentation.edit.compose.theme.TodoTheme


@Preview(showBackground = true)
@Composable
private fun ThemeTypePreview() {
    TodoTheme {
        Column {
            Text(
                text = "DisplayLarge - 32/38",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = "DisplayMedium - 20/32",
                style = MaterialTheme.typography.displayMedium
            )
            Text(
                text = "LabelLarge - 14/24",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = "BodyLarge - 16/20",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "TitleSmall - 14/20",
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}