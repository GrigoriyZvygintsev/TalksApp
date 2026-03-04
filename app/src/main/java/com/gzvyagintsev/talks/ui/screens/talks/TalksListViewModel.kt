package com.gzvyagintsev.talks.ui.screens.talks

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gzvyagintsev.talks.data.ServiceLocator
import com.gzvyagintsev.talks.data.model.Talk
import com.gzvyagintsev.talks.data.repository.TalksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ────────────────────────── UI State ──────────────────────────

data class TalksUiState(
    val isLoading: Boolean = true,
    val query: String = "",
    val selectedLevel: String? = null,
    val selectedFormat: String? = null,
    val selectedTags: Set<String> = emptySet(),
    val filteredTalks: List<Talk> = emptyList(),
    val availableLevels: List<String?> = listOf(null),
    val availableFormats: List<String?> = listOf(null),
    val availableTags: List<String> = emptyList()
)

// ────────────────────────── ViewModel ──────────────────────────

class TalksListViewModel : ViewModel() {
    private val repository = ServiceLocator.talksRepository

    // Source of truth for all talks (loaded async)
    private val _allTalks = MutableStateFlow<List<Talk>>(emptyList())

    // Individual filter streams
    private val _query = MutableStateFlow("")
    private val _selectedLevel = MutableStateFlow<String?>(null)
    private val _selectedFormat = MutableStateFlow<String?>(null)
    private val _selectedTags = MutableStateFlow<Set<String>>(emptySet())

    // Loading flag
    private val _isLoading = MutableStateFlow(true)

    // Dynamic filter options (computed once on load)
    private val _availableLevels = MutableStateFlow<List<String?>>(listOf(null))
    private val _availableFormats = MutableStateFlow<List<String?>>(listOf(null))
    private val _availableTags = MutableStateFlow<List<String>>(emptyList())

    /**
     * Single combined UiState derived reactively from all filter streams.
     * When ANY filter changes, combine fires ONCE and recomputes the filtered list.
     */
    val uiState: StateFlow<TalksUiState> = combine(
        _allTalks,
        _query,
        _selectedLevel,
        _selectedFormat,
        _selectedTags
    ) { talks, query, level, format, tags ->
        val filtered = filterTalks(talks, query, level, format, tags)

        TalksUiState(
            isLoading = _isLoading.value,
            query = query,
            selectedLevel = level,
            selectedFormat = format,
            selectedTags = tags,
            filteredTalks = filtered,
            availableLevels = _availableLevels.value,
            availableFormats = _availableFormats.value,
            availableTags = _availableTags.value
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TalksUiState()
    )

    init {
        // Non-blocking load in background thread
        viewModelScope.launch {
            val talks = withContext(Dispatchers.IO) {
                repository.getTalks()
            }

            // Compute dynamic filter options from data
            _availableLevels.value = listOf(null) +
                talks.map { it.level }.distinct().sorted()

            _availableFormats.value = listOf(null) +
                talks.flatMap { it.formats }.distinct().sorted()

            val tagPriority = listOf(
                "Python", "Algorithms", "Big-O", "Data Structures",
                "pytest", "Playwright", "CI/CD", "Docker", "Linux",
                "Test Design", "Networks"
            )
            val allDataTags = talks.flatMap { it.tags }.distinct()
            _availableTags.value = tagPriority.filter { it in allDataTags } +
                allDataTags.filter { it !in tagPriority }

            _allTalks.value = talks
            _isLoading.value = false
        }
    }

    // ─── Public actions (just update the stream, combine does the rest) ───

    fun setQuery(query: String) {
        _query.value = query
    }

    fun setLevelFilter(level: String?) {
        _selectedLevel.value = level
    }

    fun setFormatFilter(format: String?) {
        _selectedFormat.value = format
    }

    fun toggleTag(tag: String) {
        _selectedTags.value = _selectedTags.value.let { current ->
            if (tag in current) current - tag else current + tag
        }
    }

    // ─── Pure filtering function (no side effects) ───

    private fun filterTalks(
        talks: List<Talk>,
        query: String,
        level: String?,
        format: String?,
        tags: Set<String>
    ): List<Talk> {
        var result = talks

        if (query.isNotBlank()) {
            val q = query.lowercase()
            result = result.filter {
                it.title.lowercase().contains(q) || it.summary.lowercase().contains(q)
            }
        }

        if (level != null) {
            result = result.filter { it.level == level }
        }

        if (format != null) {
            result = result.filter { it.formats.contains(format) }
        }

        if (tags.isNotEmpty()) {
            result = result.filter { talk ->
                tags.all { tag -> talk.tags.contains(tag) }
            }
        }

        return result.sortedByDescending { it.date }
    }
}
