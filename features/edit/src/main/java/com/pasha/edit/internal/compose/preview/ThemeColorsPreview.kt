package com.pasha.edit.internal.compose.preview


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pasha.edit.internal.compose.theme.TodoTheme


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ThemeColorsPreview() {
    // Изменяйте аргументы для просмотра палитры
    TodoTheme(
        useDarkTheme = true,
        useDynamicColors = false
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            val scheme: ColorScheme = colorScheme
            scheme::class.java.declaredFields.forEach { field ->
                field.isAccessible = true

                val colorName = field.name
                val property = field.get(scheme)

                if (property != null) {
                    val colorValue = (property as? Long) ?: property as Int
                    val color = Color(colorValue.toLong().toULong())

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color)
                    ) {
                        Text(
                            text = colorName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Blue,
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}