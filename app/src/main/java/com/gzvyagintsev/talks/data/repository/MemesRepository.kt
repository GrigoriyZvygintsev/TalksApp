package com.gzvyagintsev.talks.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gzvyagintsev.talks.data.model.Meme

class MemesRepository(private val context: Context) {

    private val gson = Gson()
    private val cache: List<Meme> by lazy { loadFromAssets() }

    companion object {
        // Base URL for meme images hosted on Vercel
        const val IMAGE_BASE_URL = "https://qa-portfolio-beryl.vercel.app"
    }

    private fun loadFromAssets(): List<Meme> {
        val json = context.assets.open("memes.json")
            .bufferedReader()
            .use { it.readText() }
        val type = object : TypeToken<List<Meme>>() {}.type
        return gson.fromJson(json, type)
    }

    fun getMemes(): List<Meme> = cache

    fun getMemesByTalkSlug(talkSlug: String): List<Meme> =
        cache.filter { it.talkSlug == talkSlug }

    /**
     * Returns full URL for a meme image.
     * memes.json stores relative paths like "/memes/docker-image-vs-container.jpg"
     */
    fun getImageUrl(meme: Meme): String = "$IMAGE_BASE_URL${meme.image}"
}
