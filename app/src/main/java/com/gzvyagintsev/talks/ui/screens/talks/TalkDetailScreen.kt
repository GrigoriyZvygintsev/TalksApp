package com.gzvyagintsev.talks.ui.screens.talks

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gzvyagintsev.talks.data.model.Talk
import com.gzvyagintsev.talks.ui.components.LevelBadge
import com.gzvyagintsev.talks.ui.components.TagChip
import com.gzvyagintsev.talks.ui.theme.AccentColor
import com.gzvyagintsev.talks.ui.theme.BgColor
import com.gzvyagintsev.talks.ui.theme.CardColor
import com.gzvyagintsev.talks.ui.theme.TextPrimary
import com.gzvyagintsev.talks.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TalkDetailScreen(
    slug: String,
    onBack: () -> Unit,
    onOpenViewer: (title: String, url: String) -> Unit = { _, _ -> },
    viewModel: TalkDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(slug) { viewModel.loadTalk(slug) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Доклад", modifier = Modifier.testTag("text_detail_header")) },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("btn_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "btn_back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgColor)
            )
        },
        containerColor = BgColor,
        modifier = Modifier.testTag("screen_talk_detail")
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = AccentColor,
                    modifier = Modifier.testTag("progress_detail_loading")
                )
            }
        } else {
            val talk = uiState.talk
            if (talk == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        text = "Доклад не найден",
                        modifier = Modifier.testTag("text_talk_not_found")
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp,
                        top = padding.calculateTopPadding() + 8.dp,
                        bottom = padding.calculateBottomPadding() + 24.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.testTag("scroll_talk_detail")
                ) {
                    // Уровень + длительность
                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            LevelBadge(level = talk.level, slug = talk.slug)
                            Text(
                                text = talk.duration,
                                fontSize = 13.sp,
                                color = TextSecondary,
                                modifier = Modifier.testTag("text_detail_duration")
                            )
                            Text(
                                text = talk.date,
                                fontSize = 13.sp,
                                color = TextSecondary,
                                modifier = Modifier.testTag("text_detail_date")
                            )
                        }
                    }

                    // Заголовок
                    item {
                        Text(
                            text = talk.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.testTag("text_detail_title")
                        )
                    }

                    // Формат
                    item {
                        Text(
                            text = talk.formats.joinToString(" + "),
                            fontSize = 13.sp,
                            color = TextSecondary,
                            modifier = Modifier.testTag("text_detail_formats")
                        )
                    }

                    // Теги
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(talk.tags) { tag -> TagChip(tag = tag) }
                        }
                    }

                    // Summary
                    item {
                        Text(
                            text = talk.summary,
                            fontSize = 16.sp,
                            color = TextSecondary,
                            modifier = Modifier.testTag("text_detail_summary")
                        )
                    }

                    // Описание
                    item {
                        DescriptionSection(talk.description.audience, talk.description.topics, talk.description.takeaway)
                    }

                    // Программа
                    item {
                        Text(
                            text = "Программа",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            modifier = Modifier.testTag("text_outline_header")
                        )
                    }

                    itemsIndexed(talk.outline) { index, item ->
                        OutlineItem(index = index + 1, text = item)
                    }

                    // ─── Action Buttons ───
                    item {
                        ActionButtons(
                            talk = talk,
                            onOpenViewer = onOpenViewer,
                            onOpenExternal = { url ->
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButtons(
    talk: Talk,
    onOpenViewer: (title: String, url: String) -> Unit,
    onOpenExternal: (String) -> Unit
) {
    val primaryUrl = talk.htmlUrl ?: talk.pdfUrl

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.testTag("section_actions")
    ) {
        Text(
            text = "Просмотр доклада",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.testTag("text_viewer_header")
        )

        if (primaryUrl != null) {
            // Открыть в WebView внутри приложения
            Button(
                onClick = { onOpenViewer(talk.title, primaryUrl) },
                colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_open_viewer")
            ) {
                Text(
                    text = "Смотреть доклад",
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // PDF download
        if (talk.pdfUrl != null) {
            OutlinedButton(
                onClick = { onOpenExternal(talk.pdfUrl) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_download_pdf")
            ) {
                Text("Скачать PDF", color = TextPrimary)
            }
        }

        // Open in browser
        if (primaryUrl != null) {
            OutlinedButton(
                onClick = { onOpenExternal(primaryUrl) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_open_external")
            ) {
                Text("Открыть в браузере", color = TextPrimary)
            }
        }

        // Repo link
        if (talk.repoUrl != null) {
            OutlinedButton(
                onClick = { onOpenExternal(talk.repoUrl) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_open_repo")
            ) {
                Text("Репозиторий / материалы", color = TextPrimary)
            }
        }

        if (primaryUrl == null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardColor),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("card_no_materials")
            ) {
                Text(
                    text = "Материалы доклада скоро будут добавлены.",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun DescriptionSection(audience: String, topics: String, takeaway: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.testTag("card_description")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DescriptionRow(label = "Для кого", text = audience, tag = "text_audience")
            DescriptionRow(label = "Темы", text = topics, tag = "text_topics")
            DescriptionRow(label = "Результат", text = takeaway, tag = "text_takeaway")
        }
    }
}

@Composable
private fun DescriptionRow(label: String, text: String, tag: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = AccentColor.copy(alpha = 0.8f)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = TextPrimary,
            modifier = Modifier.testTag(tag)
        )
    }
}

@Composable
private fun OutlineItem(index: Int, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("outline_item_$index"),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .background(AccentColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = index.toString(),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
        Text(
            text = text,
            fontSize = 15.sp,
            color = TextPrimary
        )
    }
}
