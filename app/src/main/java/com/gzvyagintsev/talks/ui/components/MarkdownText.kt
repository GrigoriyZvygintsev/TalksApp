package com.gzvyagintsev.talks.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Simple Markdown renderer for chat messages.
 * Supports: code blocks (```), **bold**, `inline code`, bullet lists (- or •), paragraphs.
 */
@Composable
fun MarkdownText(
    text: String,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val segments = splitCodeBlocks(text)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        segments.forEach { segment ->
            when (segment) {
                is Segment.Code -> CodeBlock(code = segment.content)
                is Segment.Text -> {
                    val paragraphs = segment.content
                        .split(Regex("\\n{2,}"))
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }

                    paragraphs.forEach { paragraph ->
                        // Check if it's a bullet list
                        val lines = paragraph.split("\n")
                        val isList = lines.all { line ->
                            val trimmed = line.trim()
                            trimmed.startsWith("- ") || trimmed.startsWith("• ") ||
                            trimmed.startsWith("* ") || trimmed.matches(Regex("^\\d+\\.\\s.*"))
                        }

                        if (isList) {
                            lines.forEach { line ->
                                val cleaned = line.trim()
                                    .removePrefix("- ")
                                    .removePrefix("• ")
                                    .removePrefix("* ")
                                    .replace(Regex("^\\d+\\.\\s"), "")
                                Text(
                                    text = renderInlineMarkdown("• $cleaned"),
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
                                    color = textColor,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        } else {
                            Text(
                                text = renderInlineMarkdown(paragraph),
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = textColor
                            )
                        }
                    }
                }
            }
        }
    }
}

// ────────────────────────── Code Block ──────────────────────────

private val terminalBg = Color(0xFF0B0F19)
private val terminalText = Color(0xFFE0E0E0)
private val dotRed = Color(0xFFFF5F57)
private val dotYellow = Color(0xFFFEBC2E)
private val dotGreen = Color(0xFF28C840)
private val keywordColor = Color(0xFF7DD3FC)
private val flagColor = Color(0xFFFBBF24)
private val numberColor = Color(0xFFC4B5FD)

private val keywords = setOf(
    "sudo", "cd", "ls", "pwd", "cat", "less", "head", "tail",
    "grep", "awk", "sed", "curl", "ping", "traceroute", "ssh", "scp",
    "docker", "kubectl", "git", "npm", "python", "pytest", "make",
    "pip", "chmod", "chown", "mkdir", "rm", "cp", "mv", "echo",
    "export", "source", "apt", "yum", "brew", "wget"
)

@Composable
private fun CodeBlock(code: String) {
    // Strip optional language hint from first line (e.g. "bash", "python")
    val lines = code.split("\n")
    val codeLines = if (lines.isNotEmpty() && lines[0].trim().matches(Regex("^[a-zA-Z]+$"))) {
        lines.drop(1)
    } else {
        lines
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(terminalBg)
    ) {
        // Terminal header with dots
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF151B2B))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(dotRed))
            Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(dotYellow))
            Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(dotGreen))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "terminal",
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.6f),
                fontFamily = FontFamily.Monospace
            )
        }

        // Code content with horizontal scroll
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = highlightCode(codeLines),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 18.sp
            )
        }
    }
}

private fun highlightCode(lines: List<String>) = buildAnnotatedString {
    lines.forEachIndexed { index, line ->
        val tokens = line.split(Regex("(\\s+)"))
        tokens.forEach { token ->
            when {
                token.isBlank() -> append(token)
                token.startsWith("$") -> withStyle(SpanStyle(color = Color(0xFFF472B6))) { append(token) }
                token in keywords -> withStyle(SpanStyle(color = keywordColor)) { append(token) }
                token.startsWith("-") && token.length > 1 -> withStyle(SpanStyle(color = flagColor)) { append(token) }
                token.matches(Regex("^\\d+$")) -> withStyle(SpanStyle(color = numberColor)) { append(token) }
                token.startsWith("http") -> withStyle(SpanStyle(color = Color(0xFFA7F3D0))) { append(token) }
                token.startsWith("#") -> withStyle(SpanStyle(color = Color(0xFF6B7280))) { append(token) }
                else -> withStyle(SpanStyle(color = terminalText)) { append(token) }
            }
        }
        if (index < lines.lastIndex) append("\n")
    }
}

// ────────────────────────── Inline Markdown ──────────────────────────

private fun renderInlineMarkdown(text: String) = buildAnnotatedString {
    var cursor = 0
    val pattern = Regex("(\\*\\*[^*]+\\*\\*|`[^`]+`)")
    val matches = pattern.findAll(text)

    matches.forEach { match ->
        // Text before the match
        if (match.range.first > cursor) {
            append(text.substring(cursor, match.range.first))
        }

        val token = match.value
        when {
            token.startsWith("**") && token.endsWith("**") -> {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(token.removeSurrounding("**"))
                }
            }
            token.startsWith("`") && token.endsWith("`") -> {
                withStyle(SpanStyle(
                    fontFamily = FontFamily.Monospace,
                    background = Color(0xFFE8E2DB),
                    fontSize = 13.sp
                )) {
                    append(" ${token.removeSurrounding("`")} ")
                }
            }
        }
        cursor = match.range.last + 1
    }

    // Remaining text
    if (cursor < text.length) {
        append(text.substring(cursor))
    }
}

// ────────────────────────── Segment Parsing ──────────────────────────

private sealed class Segment {
    data class Text(val content: String) : Segment()
    data class Code(val content: String) : Segment()
}

private fun splitCodeBlocks(text: String): List<Segment> {
    val parts = text.split("```")
    return parts.mapIndexed { index, part ->
        if (index % 2 == 1) {
            // Code block — strip leading/trailing newlines
            Segment.Code(part.trim())
        } else {
            Segment.Text(part)
        }
    }.filter { segment ->
        when (segment) {
            is Segment.Text -> segment.content.isNotBlank()
            is Segment.Code -> segment.content.isNotEmpty()
        }
    }
}
