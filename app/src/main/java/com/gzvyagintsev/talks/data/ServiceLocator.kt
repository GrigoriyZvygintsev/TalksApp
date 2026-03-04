package com.gzvyagintsev.talks.data

import android.content.Context
import com.gzvyagintsev.talks.data.repository.ChatRepository
import com.gzvyagintsev.talks.data.repository.MemesRepository
import com.gzvyagintsev.talks.data.repository.TalksRepository

/**
 * Simple Service Locator — single source of truth for repository instances.
 *
 * Initialised once in Application.onCreate() or in MainActivity.
 * ViewModels access repositories through this object, making them testable:
 * in tests, just swap implementations before running.
 */
object ServiceLocator {

    private var _talksRepository: TalksRepository? = null
    private var _memesRepository: MemesRepository? = null
    private var _chatRepository: ChatRepository? = null

    val talksRepository: TalksRepository
        get() = _talksRepository ?: error("ServiceLocator not initialized. Call init(context) first.")

    val memesRepository: MemesRepository
        get() = _memesRepository ?: error("ServiceLocator not initialized. Call init(context) first.")

    val chatRepository: ChatRepository
        get() = _chatRepository ?: error("ServiceLocator not initialized. Call init(context) first.")

    fun init(context: Context) {
        val appContext = context.applicationContext
        _talksRepository = TalksRepository(appContext)
        _memesRepository = MemesRepository(appContext)
        _chatRepository = ChatRepository()
    }

    /** For testing — swap with mock repositories */
    fun setForTesting(
        talks: TalksRepository? = null,
        memes: MemesRepository? = null,
        chat: ChatRepository? = null
    ) {
        talks?.let { _talksRepository = it }
        memes?.let { _memesRepository = it }
        chat?.let { _chatRepository = it }
    }
}
