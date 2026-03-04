package com.gzvyagintsev.talks.ui.screens.talks

import androidx.lifecycle.ViewModel
import com.gzvyagintsev.talks.data.ServiceLocator
import com.gzvyagintsev.talks.data.model.Talk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class TalkDetailUiState(
    val talk: Talk? = null,
    val isLoading: Boolean = true
)

class TalkDetailViewModel : ViewModel() {

    private val repository = ServiceLocator.talksRepository

    private val _uiState = MutableStateFlow(TalkDetailUiState())
    val uiState: StateFlow<TalkDetailUiState> = _uiState.asStateFlow()

    fun loadTalk(slug: String) {
        val talk = repository.getTalkBySlug(slug)
        _uiState.value = TalkDetailUiState(talk = talk, isLoading = false)
    }
}
