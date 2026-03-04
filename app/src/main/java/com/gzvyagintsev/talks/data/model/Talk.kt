package com.gzvyagintsev.talks.data.model

import com.google.gson.annotations.SerializedName

data class Talk(
    val slug: String,
    val title: String,
    val date: String,
    val duration: String,
    val level: String,
    val formats: List<String>,
    val tags: List<String>,
    val summary: String,
    val description: TalkDescription,
    val outline: List<String>,
    @SerializedName("htmlUrl") val htmlUrl: String? = null,
    @SerializedName("pdfUrl") val pdfUrl: String? = null,
    @SerializedName("repoUrl") val repoUrl: String? = null
)

data class TalkDescription(
    val audience: String,
    val topics: String,
    val takeaway: String
)
