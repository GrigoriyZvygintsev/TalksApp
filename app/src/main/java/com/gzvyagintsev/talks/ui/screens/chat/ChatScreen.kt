package com.gzvyagintsev.talks.ui.screens.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gzvyagintsev.talks.data.repository.ChatRepository
import com.gzvyagintsev.talks.data.repository.ChatSource
import com.gzvyagintsev.talks.ui.theme.AccentColor
import com.gzvyagintsev.talks.ui.theme.BgColor
import com.gzvyagintsev.talks.ui.theme.CardColor
import com.gzvyagintsev.talks.ui.theme.TextPrimary
import com.gzvyagintsev.talks.ui.theme.TextSecondary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ────────────────────────── Chat Message ──────────────────────────

data class ChatMessage(
    val id: String = System.nanoTime().toString(),
    val text: String,
    val isUser: Boolean,
    val sources: List<ChatSource> = emptyList(),
    val isError: Boolean = false,
    val isLoading: Boolean = false
)

// ────────────────────────── ViewModel ──────────────────────────

data class ChatUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            id = "welcome",
            text = "Привет! Я AI-помощник по докладам Григория. Задайте вопрос о тестировании, Python, Docker, CI/CD или Linux — я найду ответ в материалах докладов.",
            isUser = false
        )
    ),
    val inputText: String = "",
    val isSending: Boolean = false
)

class ChatViewModel : ViewModel() {
    private val repository = ChatRepository()
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun setInput(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank() || _uiState.value.isSending) return

        val userMessage = ChatMessage(text = text, isUser = true)
        val loadingMessage = ChatMessage(
            id = "loading",
            text = "Ищу ответ в докладах…",
            isUser = false,
            isLoading = true
        )

        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage + loadingMessage,
            inputText = "",
            isSending = true
        )

        viewModelScope.launch {
            val response = repository.sendMessage(text)

            val botMessage = when (response.status) {
                "ok", "no_data" -> ChatMessage(
                    text = cleanMarkdown(response.answer ?: "Нет ответа"),
                    isUser = false,
                    sources = response.sources ?: emptyList()
                )
                "queued" -> ChatMessage(
                    text = "⏳ Запрос в очереди (позиция: ${response.position ?: "?"}). Повторите через несколько секунд.",
                    isUser = false
                )
                else -> ChatMessage(
                    text = response.error ?: "Произошла ошибка. Попробуйте позже.",
                    isUser = false,
                    isError = true
                )
            }

            _uiState.value = _uiState.value.copy(
                messages = _uiState.value.messages
                    .filter { it.id != "loading" } + botMessage,
                isSending = false
            )
        }
    }

    private fun cleanMarkdown(text: String): String {
        return text.replace("[CONTACT_LINK]", "")
            .trim()
    }
}

// ────────────────────────── Screen ──────────────────────────

@Composable
fun ChatScreen(
    onTalkClick: (String) -> Unit = {},
    viewModel: ChatViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    // Auto-scroll to latest message
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .testTag("screen_chat")
    ) {
        // Header
        Text(
            text = "AI Ассистент",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .testTag("text_chat_title")
        )

        // Messages
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .weight(1f)
                .testTag("list_messages")
        ) {
            items(uiState.messages, key = { it.id }) { message ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically { it / 2 }
                ) {
                    ChatBubble(
                        message = message,
                        onSourceClick = { slug -> onTalkClick(slug) }
                    )
                }
            }
        }

        // Input
        ChatInput(
            text = uiState.inputText,
            onTextChange = { viewModel.setInput(it) },
            onSend = { viewModel.sendMessage() },
            isSending = uiState.isSending
        )
    }
}

// ────────────────────────── Chat Bubble ──────────────────────────

@Composable
private fun ChatBubble(
    message: ChatMessage,
    onSourceClick: (String) -> Unit
) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val bgColor = when {
        message.isUser -> AccentColor.copy(alpha = 0.85f)
        message.isError -> Color(0xFFFFE0E0)
        message.isLoading -> CardColor
        else -> CardColor
    }
    val textColor = if (message.isUser) TextPrimary else TextPrimary
    val shape = if (message.isUser) {
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = bgColor),
            shape = shape,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .widthIn(max = 300.dp)
                .testTag(if (message.isUser) "bubble_user_${message.id}" else "bubble_bot_${message.id}")
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (message.isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = AccentColor,
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = message.text,
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                } else {
                    com.gzvyagintsev.talks.ui.components.MarkdownText(
                        text = message.text,
                        textColor = textColor
                    )
                }

                // Sources
                if (message.sources.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Источники:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    message.sources.forEach { source ->
                        TextButton(
                            onClick = { onSourceClick(source.slug) },
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier
                                .height(28.dp)
                                .testTag("btn_source_${source.slug}")
                        ) {
                            Text(
                                text = "📄 ${source.title}",
                                fontSize = 12.sp,
                                color = Color(0xFF1565C0),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

// ────────────────────────── Chat Input ──────────────────────────

@Composable
private fun ChatInput(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    isSending: Boolean
) {
    Surface(
        color = CardColor,
        shadowElevation = 8.dp,
        modifier = Modifier.testTag("chat_input_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text("Задайте вопрос…", color = TextSecondary, fontSize = 14.sp) },
                singleLine = false,
                maxLines = 3,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentColor,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = BgColor,
                    unfocusedContainerColor = BgColor
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_chat_message")
            )

            IconButton(
                onClick = onSend,
                enabled = text.isNotBlank() && !isSending,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (text.isNotBlank() && !isSending) AccentColor else Color.LightGray,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .testTag("btn_send_message")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Отправить",
                    tint = if (text.isNotBlank() && !isSending) TextPrimary else Color.White
                )
            }
        }
    }
}
