package com.gobieyon.creds

data class Item(
    val author: String,
    val cat: String,
    val credLink: String,
    val isSameUrl: String = false,
    val link: String,
    val videoId: String,
    val platform: String,
    val videoName: String
)