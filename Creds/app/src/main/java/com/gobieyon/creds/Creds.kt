package com.gobieyon.creds

import com.gobieyon.creds.ui.theme.CatItem
import com.google.gson.annotations.SerializedName

data class Creds(
    val authorTextColor: String,
    val bodyColor: String,
    val headerColor: String,
    val headerTextColor: String,
    val items: List<Item>,
    val catList: List<CatItem>,
    val videoNameTextColor: String,

    @SerializedName("baseUrls")
    val baseUrls: BaseUrls
)