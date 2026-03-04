package com.gzvyagintsev.talks.ui.screens.memes

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.gzvyagintsev.talks.data.model.Meme
import com.gzvyagintsev.talks.data.model.Talk
import com.gzvyagintsev.talks.data.repository.MemesRepository
import com.gzvyagintsev.talks.data.repository.TalksRepository
import com.gzvyagintsev.talks.ui.theme.AccentColor
import com.gzvyagintsev.talks.ui.theme.BgColor
import com.gzvyagintsev.talks.ui.theme.CardColor
import com.gzvyagintsev.talks.ui.theme.TextPrimary
import com.gzvyagintsev.talks.ui.theme.TextSecondary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ────────────────────────── UI State ──────────────────────────

data class MemesUiState(
    val memes: List<Meme> = emptyList(),
    val talks: List<Talk> = emptyList(),
    val isLoading: Boolean = true,
    val query: String = "",
    val selectedTalkSlug: String? = null,
    val selectedTags: Set<String> = emptySet()
)

// ────────────────────────── ViewModel ──────────────────────────

class MemesViewModel : ViewModel() {
    private val memesRepository = com.gzvyagintsev.talks.data.ServiceLocator.memesRepository
    private val talksRepository = com.gzvyagintsev.talks.data.ServiceLocator.talksRepository

    private val _uiState = MutableStateFlow(MemesUiState())
    val uiState: StateFlow<MemesUiState> = _uiState.asStateFlow()

    val availableTags = listOf("Networks", "Linux", "Docker", "CI/CD", "Testing", "Python", "pytest", "Playwright")

    init {
        val memes = memesRepository.getMemes()
        val talks = talksRepository.getTalks()
        _uiState.value = MemesUiState(memes = memes, talks = talks, isLoading = false)
    }

    fun setQuery(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }

    fun setTalkFilter(talkSlug: String?) {
        _uiState.value = _uiState.value.copy(selectedTalkSlug = talkSlug)
    }

    fun toggleTag(tag: String) {
        val current = _uiState.value.selectedTags
        val updated = if (tag in current) current - tag else current + tag
        _uiState.value = _uiState.value.copy(selectedTags = updated)
    }

    fun filteredMemes(): List<Meme> {
        val state = _uiState.value
        var result = state.memes

        if (state.query.isNotBlank()) {
            val q = state.query.lowercase()
            result = result.filter { it.caption.lowercase().contains(q) }
        }

        if (state.selectedTalkSlug != null) {
            result = result.filter { it.talkSlug == state.selectedTalkSlug }
        }

        if (state.selectedTags.isNotEmpty()) {
            result = result.filter { meme ->
                state.selectedTags.all { tag -> meme.tags.contains(tag) }
            }
        }

        return result.sortedByDescending { it.date }
    }

    fun getImageUrl(meme: Meme): String = MemesRepository.IMAGE_BASE_URL + meme.image
}

// ────────────────────────── Screen ──────────────────────────

@Composable
fun MemesScreen(
    viewModel: MemesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val memes = viewModel.filteredMemes()
    var selectedMeme by remember { mutableStateOf<Meme?>(null) }

    // Fullscreen viewer dialog
    selectedMeme?.let { meme ->
        MemeViewerDialog(
            meme = meme,
            imageUrl = viewModel.getImageUrl(meme),
            onDismiss = { selectedMeme = null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .testTag("screen_memes")
    ) {
        Text(
            text = "Мемы",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .testTag("text_memes_title")
        )

        // ─── Search ───
        OutlinedTextField(
            value = uiState.query,
            onValueChange = { viewModel.setQuery(it) },
            placeholder = { Text("Найти мем…", color = TextSecondary) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentColor,
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("input_search_memes")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ─── Talk filter ───
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.testTag("row_talk_filters")
        ) {
            item {
                FilterChip(
                    selected = uiState.selectedTalkSlug == null,
                    onClick = { viewModel.setTalkFilter(null) },
                    label = { Text("Все доклады", fontSize = 12.sp) }
                )
            }
            items(uiState.talks) { talk ->
                FilterChip(
                    selected = uiState.selectedTalkSlug == talk.slug,
                    onClick = { viewModel.setTalkFilter(talk.slug) },
                    label = {
                        Text(
                            text = talk.title,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    modifier = Modifier
                        .widthIn(max = 200.dp)
                        .semantics { contentDescription = "filter_talk_${talk.slug}" }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // ─── Tag chips ───
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.testTag("row_meme_tag_filters")
        ) {
            items(viewModel.availableTags) { tag ->
                val selected = tag in uiState.selectedTags
                FilterChip(
                    selected = selected,
                    onClick = { viewModel.toggleTag(tag) },
                    label = { Text(tag, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AccentColor.copy(alpha = 0.3f),
                        selectedLabelColor = TextPrimary
                    ),
                    modifier = Modifier.semantics {
                        contentDescription = "filter_meme_tag_$tag"
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ─── Grid ───
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = AccentColor,
                    modifier = Modifier.testTag("progress_memes_loading")
                )
            }
        } else if (memes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ничего не найдено. Попробуйте изменить фильтры.",
                    color = TextSecondary,
                    modifier = Modifier.testTag("text_memes_no_results")
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.testTag("grid_memes")
            ) {
                items(memes, key = { it.id }) { meme ->
                    MemeCard(
                        meme = meme,
                        imageUrl = viewModel.getImageUrl(meme),
                        modifier = Modifier.animateItem(),
                        onClick = { selectedMeme = meme }
                    )
                }
            }
        }
    }
}

// ────────────────────────── Meme Card ──────────────────────────

@Composable
private fun MemeCard(
    meme: Meme,
    imageUrl: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("card_meme_${meme.id}")
    ) {
        Column {
            AsyncImage(
                model = imageUrl,
                contentDescription = meme.caption,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .testTag("img_meme_${meme.id}")
            )
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = meme.caption,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("text_meme_caption_${meme.id}")
                )
                if (meme.talkSlug != null) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Доклад", fontSize = 10.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = AccentColor.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
        }
    }
}

// ────────────────────────── Fullscreen Viewer ──────────────────────────

@Composable
private fun MemeViewerDialog(
    meme: Meme,
    imageUrl: String,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f))
                .clickable(onClick = onDismiss)
                .testTag("dialog_meme_viewer")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = meme.caption,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("img_meme_fullscreen_${meme.id}")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = meme.caption,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.testTag("text_meme_fullscreen_caption")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    meme.tags.forEach { tag ->
                        AssistChip(
                            onClick = {},
                            label = { Text(tag, fontSize = 11.sp, color = Color.White) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color.White.copy(alpha = 0.15f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
                    modifier = Modifier.testTag("btn_close_meme_viewer")
                ) {
                    Text("Закрыть", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
