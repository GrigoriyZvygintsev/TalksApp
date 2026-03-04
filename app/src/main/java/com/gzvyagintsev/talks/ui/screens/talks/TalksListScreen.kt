package com.gzvyagintsev.talks.ui.screens.talks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gzvyagintsev.talks.ui.components.FilterChipRow
import com.gzvyagintsev.talks.ui.components.SearchBar
import com.gzvyagintsev.talks.ui.components.TagChipRow
import com.gzvyagintsev.talks.ui.components.TalkCard
import com.gzvyagintsev.talks.ui.theme.AccentColor
import com.gzvyagintsev.talks.ui.theme.BgColor
import com.gzvyagintsev.talks.ui.theme.TextPrimary
import com.gzvyagintsev.talks.ui.theme.TextSecondary

@Composable
fun TalksListScreen(
    onTalkClick: (String) -> Unit,
    viewModel: TalksListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .testTag("screen_talks_list")
    ) {
        Text(
            text = "Доклады",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .testTag("text_talks_title")
        )

        // ─── Search ───
        SearchBar(
            query = uiState.query,
            onQueryChange = { viewModel.setQuery(it) },
            placeholder = "Найти доклад…",
            testTag = "input_search_talks",
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ─── Format filter (dynamic from data) ───
        FilterChipRow(
            items = uiState.availableFormats,
            selectedItem = uiState.selectedFormat,
            onItemSelected = { viewModel.setFormatFilter(it) },
            allLabel = "Формат",
            testTagPrefix = "filter_format",
            modifier = Modifier.testTag("row_format_filters")
        )

        Spacer(modifier = Modifier.height(4.dp))

        // ─── Level filter (dynamic from data) ───
        FilterChipRow(
            items = uiState.availableLevels,
            selectedItem = uiState.selectedLevel,
            onItemSelected = { viewModel.setLevelFilter(it) },
            allLabel = "Все уровни",
            testTagPrefix = "filter_level",
            modifier = Modifier.testTag("row_level_filters")
        )

        Spacer(modifier = Modifier.height(4.dp))

        // ─── Tag chips ───
        TagChipRow(
            tags = uiState.availableTags,
            selectedTags = uiState.selectedTags,
            onTagToggle = { viewModel.toggleTag(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ─── Results ───
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = AccentColor,
                        modifier = Modifier.testTag("progress_loading")
                    )
                }
            }
            uiState.filteredTalks.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ничего не найдено. Попробуйте изменить фильтры.",
                        color = TextSecondary,
                        modifier = Modifier.testTag("text_no_results")
                    )
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.testTag("list_talks")
                ) {
                    items(uiState.filteredTalks, key = { it.slug }) { talk ->
                        TalkCard(
                            talk = talk,
                            onClick = { onTalkClick(talk.slug) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}
