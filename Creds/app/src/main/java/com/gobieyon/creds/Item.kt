package com.gobieyon.creds

data class Item(
    val author: String,
    val cat: String,
    val credLink: String = "",
    val tikTokLink: String = "",
    val isSameUrl: Boolean = false,
    val link: String,
    val videoId: String,
    val platform: String,
    val videoName: String
)