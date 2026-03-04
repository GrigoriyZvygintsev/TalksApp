package com.gzvyagintsev.talks.data.repository

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

// ────────────────────────── Data classes ──────────────────────────

data class ChatRequest(
    val message: String,
    val ticketId: String? = null
)

data class ChatSource(
    val slug: String,
    val title: String
)

data class ChatResponse(
    val status: String,          // "ok" | "no_data" | "error" | "queued"
    val answer: String? = null,
    val sources: List<ChatSource>? = null,
    val contactLink: Boolean? = null,
    val error: String? = null,
    val ticketId: String? = null,
    val position: Int? = null,
    val cached: Boolean? = null
)

// ────────────────────────── Repository ──────────────────────────

class ChatRepository {

    companion object {
        private const val BASE_URL = "https://qa-portfolio-beryl.vercel.app"
        private const val CHAT_URL = "$BASE_URL/api/chat"
        private const val TIMEOUT_MS = 30_000
    }

    private val gson = Gson()

    suspend fun sendMessage(message: String, ticketId: String? = null): ChatResponse {
        return withContext(Dispatchers.IO) {
            val url = URL(CHAT_URL)
            val connection = url.openConnection() as HttpURLConnection
            try {
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Accept", "application/json")
                connection.connectTimeout = TIMEOUT_MS
                connection.readTimeout = TIMEOUT_MS
                connection.doOutput = true

                val body = gson.toJson(ChatRequest(message, ticketId))
                OutputStreamWriter(connection.outputStream, "UTF-8").use { writer ->
                    writer.write(body)
                    writer.flush()
                }

                val responseCode = connection.responseCode
                val stream = if (responseCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream ?: connection.inputStream
                }

                val responseText = stream.bufferedReader().use { it.readText() }
                gson.fromJson(responseText, ChatResponse::class.java)
            } catch (e: Exception) {
                ChatResponse(
                    status = "error",
                    error = "Не удалось подключиться: ${e.localizedMessage ?: "Unknown error"}"
                )
            } finally {
                connection.disconnect()
            }
        }
    }
}
