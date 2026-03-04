package com.gzvyagintsev.talks.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gzvyagintsev.talks.ui.theme.AccentColor
import com.gzvyagintsev.talks.ui.theme.TextPrimary

@Composable
fun LevelBadge(level: String, slug: String) {
    val bgColor = when (level) {
        "Junior", "Junior+" -> Color(0xFFD4EDDA)
        "Middle" -> AccentColor.copy(alpha = 0.3f)
        "Senior" -> Color(0xFFF8D7DA)
        else -> Color(0xFFE2E3E5)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .testTag("badge_level_$slug")
            .semantics { contentDescription = "badge_level_$slug" }
    ) {
        Text(
            text = level,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}
