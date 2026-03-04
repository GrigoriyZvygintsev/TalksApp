package com.gzvyagintsev.talks.data.model

data class Meme(
    val id: String,
    val caption: String,
    val image: String,
    val tags: List<String>,
    val talkSlug: String? = null,
    val date: String
)
