package com.gzvyagintsev.talks.ui.screens.home

import android.app.Application
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.lifecycle.ViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gzvyagintsev.talks.data.model.Talk
import com.gzvyagintsev.talks.data.repository.TalksRepository
import com.gzvyagintsev.talks.ui.components.TalkCard
import com.gzvyagintsev.talks.ui.components.ParticlesBackground
import com.gzvyagintsev.talks.ui.theme.AccentColor
import com.gzvyagintsev.talks.ui.theme.BgColor
import com.gzvyagintsev.talks.ui.theme.CardBorder
import com.gzvyagintsev.talks.ui.theme.CardColor
import com.gzvyagintsev.talks.ui.theme.TextPrimary
import com.gzvyagintsev.talks.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ────────────────────────── ViewModel ──────────────────────────

data class HomeUiState(
    val latestTalks: List<Talk> = emptyList(),
    val isLoading: Boolean = true
)

class HomeViewModel : ViewModel() {
    private val repository = com.gzvyagintsev.talks.data.ServiceLocator.talksRepository
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        val talks = repository.getTalks()
            .sortedByDescending { it.date }
            .take(3)
        _uiState.value = HomeUiState(latestTalks = talks, isLoading = false)
    }
}

// ────────────────────────── Screen ──────────────────────────

@Composable
fun HomeScreen(
    onNavigateToTalks: () -> Unit,
    onNavigateToMemes: () -> Unit,
    onNavigateToContacts: () -> Unit,
    onTalkClick: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .testTag("screen_home")
    ) {
        // Animated particle background (like the website)
        ParticlesBackground(
            modifier = Modifier.fillMaxSize()
        )

        LazyColumn(
            contentPadding = PaddingValues(bottom = 24.dp),
            modifier = Modifier.fillMaxSize()
        ) {
        // ─── Hero Section ───
        item {
            AnimatedSection(delayMs = 0) {
                HeroSection(onNavigateToTalks = onNavigateToTalks)
            }
        }

        // ─── Profile Card ───
        item {
            AnimatedSection(delayMs = 100) {
                ProfileCard(onNavigateToContacts = onNavigateToContacts)
            }
        }

        // ─── About Section ───
        item {
            AnimatedSection(delayMs = 200) {
                SectionHeader(title = "Обо мне", testTag = "text_about_header")
                AboutSection()
            }
        }

        // ─── Stack Section ───
        item {
            AnimatedSection(delayMs = 300) {
                SectionHeader(title = "Стек и инструменты", testTag = "text_stack_header")
                StackSection()
            }
        }

        // ─── Topics Section ───
        item {
            AnimatedSection(delayMs = 400) {
                SectionHeader(title = "Темы", testTag = "text_topics_header")
                TopicsSection()
            }
        }

        // ─── Latest Talks ───
        item {
            AnimatedSection(delayMs = 500) {
                SectionHeader(title = "Последние доклады", testTag = "text_latest_talks_header")
            }
        }
        if (uiState.isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentColor)
                }
            }
        } else {
            items(uiState.latestTalks, key = { it.slug }) { talk ->
                AnimatedSection(delayMs = 550) {
                    TalkCard(
                        talk = talk,
                        onClick = { onTalkClick(talk.slug) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
            item {
                AnimatedSection(delayMs = 600) {
                    Button(
                        onClick = onNavigateToTalks,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .testTag("btn_all_talks")
                    ) {
                        Text("Все доклады", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // ─── Contacts preview ───
        item {
            AnimatedSection(delayMs = 650) {
                SectionHeader(title = "Контакты", testTag = "text_contacts_header")
                ContactsPreview(onNavigateToContacts = onNavigateToContacts)
            }
        }
    }
    }
}

// ────────────────────────── Components ──────────────────────────

@Composable
private fun SectionHeader(title: String, testTag: String) {
    Text(
        text = title,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 28.dp, bottom = 12.dp)
            .testTag(testTag)
    )
}

@Composable
private fun HeroSection(onNavigateToTalks: () -> Unit) {
    // Typewriter animation state
    val fullTitle = "QA доклады и материалы"
    val fullSubtitle = "от Григория Звягинцева"
    var titleChars by remember { mutableIntStateOf(0) }
    var subtitleChars by remember { mutableIntStateOf(0) }
    var showSubtitle by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    // Typewriter effect — title first, then subtitle
    LaunchedEffect(Unit) {
        delay(600) // Initial pause for particles to appear
        for (i in 1..fullTitle.length) {
            titleChars = i
            delay(45)
        }
        delay(300)
        showSubtitle = true
        for (i in 1..fullSubtitle.length) {
            subtitleChars = i
            delay(40)
        }
        delay(400)
        showButton = true
    }

    val buttonAlpha by animateFloatAsState(
        targetValue = if (showButton) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "btnAlpha"
    )
    val buttonSlide by animateFloatAsState(
        targetValue = if (showButton) 0f else 30f,
        animationSpec = tween(durationMillis = 600),
        label = "btnSlide"
    )

    // Full-screen hero — no card, particles visible behind
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 420.dp)
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .testTag("card_hero"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Role badge
            Text(
                text = "AUTOMATION QA ENGINEER",
                fontSize = 11.sp,
                color = TextSecondary,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.testTag("text_hero_role")
            )

            // Main typewriter title
            Text(
                text = fullTitle.take(titleChars),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                lineHeight = 38.sp,
                modifier = Modifier.testTag("text_hero_slogan")
            )

            // Subtitle typewriter
            if (showSubtitle) {
                Text(
                    text = fullSubtitle.take(subtitleChars),
                    fontSize = 18.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.testTag("text_hero_subtitle")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description
            if (showSubtitle) {
                Text(
                    text = "Автотесты для API/UI, пайплайны и инфраструктура тестирования — чтобы релизы были предсказуемыми.",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp,
                    modifier = Modifier.testTag("text_hero_description")
                )
            }

            // CTA Button with fade+slide
            Button(
                onClick = onNavigateToTalks,
                colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = buttonAlpha
                        translationY = buttonSlide
                    }
                    .testTag("btn_hero_talks")
            ) {
                Text("Смотреть доклады", color = TextPrimary, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ProfileCard(onNavigateToContacts: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("card_profile")
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Avatar from website
            coil.compose.AsyncImage(
                model = "https://qa-portfolio-beryl.vercel.app/avatar-latest.jpg",
                contentDescription = "Аватар Григория Звягинцева",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(AccentColor.copy(alpha = 0.2f))
                    .testTag("img_avatar")
            )
            Text(
                text = "Григорий Звягинцев",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.testTag("text_profile_name")
            )
            Text(
                text = "Automation QA Engineer",
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.testTag("text_profile_role")
            )
            Text(
                text = "Доклады и материалы по фундаменту инженера тестирования.",
                fontSize = 14.sp,
                color = TextSecondary
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                val tags = listOf("API/UI Automation", "CI/CD", "Linux", "QA")
                items(tags) { tag ->
                    AssistChip(
                        onClick = {},
                        label = { Text(tag, fontSize = 11.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = AccentColor.copy(alpha = 0.15f)
                        )
                    )
                }
            }
            OutlinedButton(
                onClick = onNavigateToContacts,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_profile_contact")
            ) {
                Text("Связаться / предложить тему", color = TextPrimary)
            }
        }
    }
}

@Composable
private fun AboutSection() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("card_about")
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Я Automation QA Engineer и делаю материалы, которые помогают быстрее разбираться в базе и практике тестирования.",
                fontSize = 14.sp, color = TextSecondary
            )
            Text(
                text = "Мой фокус — автотесты для API/UI на Python и инфраструктура тестирования: окружения, пайплайны, отчётность.",
                fontSize = 14.sp, color = TextSecondary
            )
            Text(
                text = "Доклады и конспекты оформляю так, чтобы ими можно было пользоваться как шпаргалками для работы и собеседований.",
                fontSize = 14.sp, color = TextSecondary
            )
        }
    }
}

@Composable
private fun StackSection() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("card_stack")
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Ядро", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                BulletText("Python")
                BulletText("PyTest")
                BulletText("Playwright")
                BulletText("API testing (requests)")
                BulletText("CI/CD (GitLab CI)")
                BulletText("Docker")
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Инструменты", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(
                text = "Pydantic • Allure • Git • Linux • SQL • HTTP",
                fontSize = 14.sp, color = TextSecondary
            )
        }
    }
}

@Composable
private fun TopicsSection() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("card_topics")
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BulletText("Сети: TCP/IP, диагностика, типовые вопросы собеседований")
            BulletText("Linux: команды и практические сценарии")
            BulletText("Docker/Kubernetes: окружения и базовые подходы")
            BulletText("CI/CD: запуск тестов стабильно и повторяемо")
            BulletText("Теория тестирования: база + собес-часть")
            BulletText("Python automation: API/UI тесты и структура фреймворка")
        }
    }
}

@Composable
private fun ContactsPreview(onNavigateToContacts: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("card_contacts_preview")
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Если у вас есть предложение по докладу/проекту/коллаборации — пишите.",
                fontSize = 14.sp, color = TextSecondary
            )
            Button(
                onClick = onNavigateToContacts,
                colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_contacts_open")
            ) {
                Text("Открыть контакты", color = TextPrimary, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun BulletText(text: String) {
    Text(
        text = "• $text",
        fontSize = 14.sp,
        color = TextSecondary
    )
}

@Composable
private fun AnimatedSection(
    delayMs: Int = 0,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (delayMs > 0) delay(delayMs.toLong())
        visible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "sectionAlpha"
    )
    val translationY by animateFloatAsState(
        targetValue = if (visible) 0f else 40f,
        animationSpec = tween(durationMillis = 500),
        label = "sectionSlide"
    )

    Column(
        modifier = Modifier.graphicsLayer {
            this.alpha = alpha
            this.translationY = translationY
        }
    ) {
        content()
    }
}
