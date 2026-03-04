package com.gzvyagintsev.talks.ui.screens.contacts

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gzvyagintsev.talks.ui.theme.AccentColor
import com.gzvyagintsev.talks.ui.theme.BgColor
import com.gzvyagintsev.talks.ui.theme.CardColor
import com.gzvyagintsev.talks.ui.theme.TextPrimary
import com.gzvyagintsev.talks.ui.theme.TextSecondary
import kotlin.random.Random

// ────────────────────────── Challenge ──────────────────────────

private data class Challenge(val question: String, val answer: Int)

private fun createChallenge(): Challenge {
    val a = Random.nextInt(2, 10)
    val b = Random.nextInt(2, 9)
    return if (Random.nextBoolean()) {
        Challenge("$a + $b", a + b)
    } else {
        Challenge("${a + b} - $a", b)
    }
}

// ────────────────────────── Screen ──────────────────────────

@Composable
fun ContactsScreen() {
    val context = LocalContext.current

    // CAPTCHA state
    var captchaOpen by remember { mutableStateOf(false) }
    var captchaInput by remember { mutableStateOf("") }
    var captchaError by remember { mutableStateOf("") }
    var challenge by remember { mutableStateOf(createChallenge()) }
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    fun requestWithCaptcha(action: () -> Unit) {
        pendingAction = action
        challenge = createChallenge()
        captchaInput = ""
        captchaError = ""
        captchaOpen = true
    }

    fun checkAnswer() {
        val trimmed = captchaInput.trim()
        if (trimmed.toIntOrNull() == challenge.answer) {
            captchaOpen = false
            captchaError = ""
            pendingAction?.invoke()
            pendingAction = null
        } else {
            captchaError = "Неверный ответ, попробуйте ещё раз."
            challenge = createChallenge()
            captchaInput = ""
        }
    }

    fun refreshChallenge() {
        challenge = createChallenge()
        captchaInput = ""
        captchaError = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .padding(16.dp)
            .testTag("screen_contacts"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Контакты",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .testTag("text_contacts_title")
        )

        Text(
            text = "Если у вас есть предложение по докладу/проекту/коллаборации — пишите.",
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Telegram — protected by CAPTCHA
        ContactItem(
            title = "Telegram",
            subtitle = "Решите пример, чтобы открыть",
            icon = Icons.Default.Lock,
            testTag = "btn_contact_telegram",
            onClick = { requestWithCaptcha { openUrl("https://t.me/Grigori_Zvyagintcev") } }
        )

        // LinkedIn — open
        ContactItem(
            title = "LinkedIn",
            subtitle = "grigorii-zviagintsev",
            icon = Icons.Default.Share,
            testTag = "btn_contact_linkedin",
            onClick = { openUrl("https://www.linkedin.com/in/grigorii-zviagintsev/") }
        )

        // GitHub — open
        ContactItem(
            title = "GitHub",
            subtitle = "GrigoriyZvygintsev",
            icon = Icons.Default.Share,
            testTag = "btn_contact_github",
            onClick = { openUrl("https://github.com/GrigoriyZvygintsev") }
        )

        // Email — protected by CAPTCHA
        ContactItem(
            title = "Email",
            subtitle = "Решите пример, чтобы открыть",
            icon = Icons.Default.Lock,
            testTag = "btn_contact_email",
            onClick = { requestWithCaptcha { openUrl("mailto:grigory.zvyagintsev@gmail.com") } }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = CardColor),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.testTag("card_contacts_note")
        ) {
            Text(
                text = "Быстрее всего отвечаю в Telegram. Можно сразу присылать ссылку на проект и короткий контекст задачи.",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(16.dp)
            )
        }

        Text(
            text = "Отвечаю обычно в течение 1–2 дней.",
            fontSize = 12.sp,
            color = TextSecondary.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 4.dp)
        )
    }

    // ────────────────────────── CAPTCHA Dialog ──────────────────────────
    if (captchaOpen) {
        AlertDialog(
            onDismissRequest = { captchaOpen = false },
            containerColor = CardColor,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.testTag("dialog_captcha"),
            title = {
                Text(
                    text = "Проверка контакта",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.testTag("text_captcha_title")
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Решите пример:",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = challenge.question,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.testTag("text_captcha_question")
                    )
                    OutlinedTextField(
                        value = captchaInput,
                        onValueChange = { captchaInput = it },
                        placeholder = { Text("Ваш ответ", color = TextSecondary) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AccentColor,
                            unfocusedBorderColor = Color.LightGray,
                            focusedContainerColor = BgColor,
                            unfocusedContainerColor = BgColor
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_captcha_answer")
                    )
                    if (captchaError.isNotEmpty()) {
                        Text(
                            text = captchaError,
                            fontSize = 12.sp,
                            color = Color(0xFFD32F2F),
                            modifier = Modifier.testTag("text_captcha_error")
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { checkAnswer() },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
                    modifier = Modifier.testTag("btn_captcha_submit")
                ) {
                    Text("Показать", color = TextPrimary, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { refreshChallenge() },
                    modifier = Modifier.testTag("btn_captcha_refresh")
                ) {
                    Text("Другой пример", color = TextPrimary)
                }
            }
        )
    }
}

@Composable
private fun ContactItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = CardColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = AccentColor,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
