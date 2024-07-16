package com.pasha.edit.internal.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.pasha.core_ui.R


@Composable
fun getColorScheme(isDarkTheme: Boolean): ColorScheme {
    return if (isDarkTheme) {
        darkColorScheme(
            primary = colorResource(id = R.color.label_primary),
            onPrimary = Color.Black,
            primaryContainer = colorResource(id = R.color.back_secondary),
            onPrimaryContainer = colorResource(id = R.color.color_white),
            secondary = colorResource(id = R.color.label_secondary),
            onSecondary = Color.Black,
            secondaryContainer = colorResource(id = R.color.back_secondary_elevated),
            onSecondaryContainer = colorResource(id = R.color.color_white),
            tertiary = colorResource(id = R.color.label_tertiary),
            onTertiary = Color.Black,
            tertiaryContainer = colorResource(id = R.color.back_secondary),
            onTertiaryContainer = colorResource(id = R.color.color_white),
            background = colorResource(id = R.color.back_primary),
            onBackground = colorResource(id = R.color.label_primary),
            surface = colorResource(id = R.color.back_secondary),
            onSurface = colorResource(id = R.color.label_primary),
            error = colorResource(id = R.color.color_red),
            onError = colorResource(id = R.color.color_white),
            outline = colorResource(id = R.color.support_separator)
        )
    } else {
        lightColorScheme(
            primary = colorResource(id = R.color.label_primary),
            onPrimary = Color.White,
            primaryContainer = colorResource(id = R.color.back_secondary),
            onPrimaryContainer = Color.Black,
            secondary = colorResource(id = R.color.label_secondary),
            onSecondary = Color.White,
            secondaryContainer = colorResource(id = R.color.back_secondary_elevated),
            onSecondaryContainer = Color.Black,
            tertiary = colorResource(id = R.color.label_tertiary),
            onTertiary = Color.White,
            tertiaryContainer = colorResource(id = R.color.back_secondary),
            onTertiaryContainer = Color.Black,
            background = colorResource(id = R.color.back_primary),
            onBackground = colorResource(id = R.color.label_primary),
            surface = colorResource(id = R.color.back_secondary),
            onSurface = colorResource(id = R.color.label_primary),
            error = colorResource(id = R.color.color_red),
            onError = colorResource(id = R.color.color_white),
            outline = colorResource(id = R.color.support_separator)
        )
    }
}