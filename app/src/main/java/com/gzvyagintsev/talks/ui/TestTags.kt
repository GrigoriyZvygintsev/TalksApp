package com.gzvyagintsev.talks.ui

/**
 * Centralized testTag constants for Appium / UI testing.
 *
 * Use these instead of raw strings to avoid typos.
 * Python test side should mirror these constants.
 */
object TestTags {
    // ─── Screens ───
    const val SCREEN_HOME = "screen_home"
    const val SCREEN_TALKS_LIST = "screen_talks_list"
    const val SCREEN_CONTACTS = "screen_contacts"

    // ─── Home Screen ───
    const val CARD_HERO = "card_hero"
    const val TEXT_HERO_ROLE = "text_hero_role"
    const val TEXT_HERO_SLOGAN = "text_hero_slogan"
    const val TEXT_HERO_SUBTITLE = "text_hero_subtitle"
    const val TEXT_HERO_DESCRIPTION = "text_hero_description"
    const val BTN_HERO_TALKS = "btn_hero_talks"
    const val CARD_PROFILE = "card_profile"
    const val IMG_AVATAR = "img_avatar"
    const val BTN_ALL_TALKS = "btn_all_talks"

    // ─── Section Headers ───
    const val TEXT_ABOUT_HEADER = "text_about_header"
    const val TEXT_STACK_HEADER = "text_stack_header"
    const val TEXT_TOPICS_HEADER = "text_topics_header"
    const val TEXT_LATEST_TALKS_HEADER = "text_latest_talks_header"
    const val TEXT_CONTACTS_HEADER = "text_contacts_header"

    // ─── Talks List ───
    const val TEXT_TALKS_TITLE = "text_talks_title"
    const val INPUT_SEARCH_TALKS = "input_search_talks"
    const val ROW_FORMAT_FILTERS = "row_format_filters"
    const val ROW_LEVEL_FILTERS = "row_level_filters"
    const val ROW_TAG_FILTERS = "row_tag_filters"
    const val LIST_TALKS = "list_talks"
    const val PROGRESS_LOADING = "progress_loading"
    const val TEXT_NO_RESULTS = "text_no_results"

    // ─── Talk Detail ───
    fun cardTalk(slug: String) = "card_talk_$slug"
    fun textTalkTitle(slug: String) = "text_talk_title_$slug"
    fun textTalkSummary(slug: String) = "text_talk_summary_$slug"
    fun badgeLevel(slug: String) = "badge_level_$slug"
    fun textDuration(slug: String) = "text_duration_$slug"
    fun textDate(slug: String) = "text_date_$slug"

    // ─── Contacts ───
    const val TEXT_CONTACTS_TITLE = "text_contacts_title"
    const val BTN_CONTACT_TELEGRAM = "btn_contact_telegram"
    const val BTN_CONTACT_LINKEDIN = "btn_contact_linkedin"
    const val BTN_CONTACT_GITHUB = "btn_contact_github"
    const val BTN_CONTACT_EMAIL = "btn_contact_email"
    const val CARD_CONTACTS_NOTE = "card_contacts_note"

    // ─── CAPTCHA ───
    const val DIALOG_CAPTCHA = "dialog_captcha"
    const val TEXT_CAPTCHA_TITLE = "text_captcha_title"
    const val TEXT_CAPTCHA_QUESTION = "text_captcha_question"
    const val INPUT_CAPTCHA_ANSWER = "input_captcha_answer"
    const val BTN_CAPTCHA_SUBMIT = "btn_captcha_submit"
    const val BTN_CAPTCHA_REFRESH = "btn_captcha_refresh"
    const val TEXT_CAPTCHA_ERROR = "text_captcha_error"

    // ─── Navigation ───
    const val NAV_HOME = "nav_home"
    const val NAV_TALKS = "nav_talks"
    const val NAV_MEMES = "nav_memes"
    const val NAV_CHAT = "nav_chat"
    const val NAV_CONTACTS = "nav_contacts"
}
