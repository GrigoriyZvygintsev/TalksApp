package com.gzvyagintsev.talks.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AccentColor,
    background = BgColor,
    surface = CardColor,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun TalksAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
