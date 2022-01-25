/*
 *    Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
 *
 *     This file is part of Menza.
 *
 *     Menza is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Menza is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Menza.  If not, see <https://www.gnu.org/licenses/>.
 */

package cz.lastaapps.menza.ui.theme

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val LightThemeScheme = lightColorScheme(

    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
)
private val DarkThemeScheme = darkColorScheme(

    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    useCustomTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !useCustomTheme
    val colorScheme = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> DarkThemeScheme
        else -> LightThemeScheme
    }.animated()

    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = colorScheme.primary,
            darkIcons = darkTheme,
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
    ) {
        androidx.compose.material.MaterialTheme(
            colors = colorScheme.toLegacy(darkTheme),
            content = content
        )
    }
}

@Composable
private fun ColorScheme.toLegacy(isDark: Boolean): Colors = Colors(
    primary = primary, primaryVariant = primaryContainer,
    secondary = tertiary, secondaryVariant = tertiaryContainer,
    background = background, surface = surface, error = error,
    onPrimary = onPrimary, onSecondary = onSecondary,
    onBackground = onBackground, onSurface = onSurface, onError = onError,
    isLight = !isDark,
)

@Composable
private fun ColorScheme.animated(): ColorScheme {
    return ColorScheme(
        background = animateColorAsState(background).value,
        error = animateColorAsState(error).value,
        errorContainer = animateColorAsState(errorContainer).value,
        inverseOnSurface = animateColorAsState(inverseOnSurface).value,
        inversePrimary = animateColorAsState(inversePrimary).value,
        inverseSurface = animateColorAsState(inverseSurface).value,
        onBackground = animateColorAsState(onBackground).value,
        onError = animateColorAsState(onError).value,
        onErrorContainer = animateColorAsState(onErrorContainer).value,
        onPrimary = animateColorAsState(onPrimary).value,
        onPrimaryContainer = animateColorAsState(onPrimaryContainer).value,
        onSecondary = animateColorAsState(onSecondary).value,
        onSecondaryContainer = animateColorAsState(onSecondaryContainer).value,
        onSurface = animateColorAsState(onSurface).value,
        onSurfaceVariant = animateColorAsState(onSurfaceVariant).value,
        onTertiary = animateColorAsState(onTertiary).value,
        onTertiaryContainer = animateColorAsState(onTertiaryContainer).value,
        outline = animateColorAsState(outline).value,
        primary = animateColorAsState(primary).value,
        primaryContainer = animateColorAsState(primaryContainer).value,
        secondary = animateColorAsState(secondary).value,
        secondaryContainer = animateColorAsState(secondaryContainer).value,
        surface = animateColorAsState(surface).value,
        surfaceVariant = animateColorAsState(surfaceVariant).value,
        tertiary = animateColorAsState(tertiary).value,
        tertiaryContainer = animateColorAsState(tertiaryContainer).value,
    )
}