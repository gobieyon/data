package com.gobieyon.creds

import com.google.gson.annotations.SerializedName


data class BaseUrls(
    @SerializedName("facebook")
    val facebook: String,
    @SerializedName("instagram")
    val instagram: String,
    @SerializedName("tiktok")
    val tiktok: String,
    @SerializedName("youtubeShorts")
    val youtubeShorts: String,
    @SerializedName("youtubeWatch")
    val youtubeWatch: String,
    @SerializedName("pinterest")
    val pinterest: String
)