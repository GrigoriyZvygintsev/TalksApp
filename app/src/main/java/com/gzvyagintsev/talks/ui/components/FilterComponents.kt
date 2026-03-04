package com.gzvyagintsev.talks.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gzvyagintsev.talks.ui.theme.AccentColor
import com.gzvyagintsev.talks.ui.theme.TextPrimary
import com.gzvyagintsev.talks.ui.theme.TextSecondary

/**
 * Reusable search text field.
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Найти…",
    testTag: String = "input_search",
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholder, color = TextSecondary) },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentColor,
            unfocusedBorderColor = Color.LightGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
    )
}

/**
 * Horizontal row of FilterChips.
 * Items can be nullable (null = "show all").
 */
@Composable
fun FilterChipRow(
    items: List<String?>,
    selectedItem: String?,
    onItemSelected: (String?) -> Unit,
    allLabel: String = "Все",
    testTagPrefix: String = "filter",
    modifier: Modifier = Modifier
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items(items) { item ->
            FilterChip(
                selected = selectedItem == item,
                onClick = { onItemSelected(item) },
                label = { Text(item ?: allLabel, fontSize = 12.sp) },
                modifier = Modifier.semantics {
                    contentDescription = "${testTagPrefix}_${item ?: "all"}"
                }
            )
        }
    }
}

/**
 * Horizontal row of tag chips with accent highlight.
 */
@Composable
fun TagChipRow(
    tags: List<String>,
    selectedTags: Set<String>,
    onTagToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("row_tag_filters")
    ) {
        items(tags) { tag ->
            FilterChip(
                selected = tag in selectedTags,
                onClick = { onTagToggle(tag) },
                label = { Text(tag, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AccentColor.copy(alpha = 0.3f),
                    selectedLabelColor = TextPrimary
                ),
                modifier = Modifier.semantics {
                    contentDescription = "filter_tag_$tag"
                }
            )
        }
    }
}
