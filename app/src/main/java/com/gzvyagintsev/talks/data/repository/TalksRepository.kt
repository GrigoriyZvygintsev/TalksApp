package com.gzvyagintsev.talks.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gzvyagintsev.talks.data.model.Talk

class TalksRepository(private val context: Context) {

    private val gson = Gson()
    private val cache: List<Talk> by lazy { loadFromAssets() }

    private fun loadFromAssets(): List<Talk> {
        val json = context.assets.open("talks.json")
            .bufferedReader()
            .use { it.readText() }
        val type = object : TypeToken<List<Talk>>() {}.type
        return gson.fromJson(json, type)
    }

    fun getTalks(): List<Talk> = cache

    fun getTalkBySlug(slug: String): Talk? = cache.find { it.slug == slug }
}
