package com.gobieyon.creds

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gobieyon.creds.ui.theme.CredsTheme
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import androidx.core.graphics.toColorInt
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.view.WindowInsetsControllerCompat
import coil.compose.AsyncImage
import com.gobieyon.creds.ui.theme.CatItem
import okhttp3.OkHttpClient
import okhttp3.Request


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CredsTheme {

                var creds by remember { mutableStateOf<Creds?>(null) }

                LaunchedEffect(Unit) {
                    val url = "https://raw.githubusercontent.com/gobieyon/data/main/json"
                    try {
                        val json = withContext(Dispatchers.IO) { URL(url).readText() }
                        creds = Gson().fromJson(json, Creds::class.java)
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, "can't loading data", Toast.LENGTH_SHORT).show()
                    }
                }


                creds?.let { data ->

                    val window = this.window
                    SideEffect {
                        window.statusBarColor = data.headerColor.toColor().toArgb()

                        WindowInsetsControllerCompat(window, window.decorView)
                            .isAppearanceLightStatusBars = false
                    }

                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                    ) { innerPadding ->
                        Page(
                            creds = data,
                            innerPadding = innerPadding
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun Page(
    creds: Creds,
    innerPadding: PaddingValues
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("all") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredItems = if (selectedCategory == "all") {
        creds.items
    } else {
        creds.items.filter { it.cat == selectedCategory }
    }

    val searchedItems = filteredItems.filter { item ->
        item.videoName.contains(searchQuery, ignoreCase = true) ||
        item.videoId.contains(searchQuery, ignoreCase = true) ||
        item.author.contains(searchQuery, ignoreCase = true) ||
        item.cat.contains(searchQuery, ignoreCase = true) ||
        item.platform.contains(searchQuery, ignoreCase = true)
    }

    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
    ) {

        SearchBar(
            creds = creds,
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            modifier = Modifier.padding(0.dp)
        )

        Tabs(
            creds = creds,
            categories = creds.catList,
            selectedCat = selectedCategory,
            onCatSelected = { selectedCategory = it.cat },
            modifier = Modifier
                .fillMaxWidth()
                .background(creds.headerColor.toColor())
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(creds.bodyColor.toColor()),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            itemsIndexed(searchedItems) { _, item ->
                val uriHandler = LocalUriHandler.current

                if (item.author.isNotBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {

                        Text(
                            text = item.platform,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Normal,
                            color = creds.headerTextColor.toColor(),
                        )

                        if (item.platform.contains("youtube")) {
                            AsyncImage(
                                model = "https://img.youtube.com/vi/${item.videoId}/hqdefault.jpg",
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }

                        if (item.platform.contains("instagram")) {
                            AsyncImage(
                                model = "https://www.instagram.com/p/${item.videoId}/media/?size=l",
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .background(creds.bodyColor.toColor()),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            itemsIndexed(searchedItems) { _, item ->

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 8.dp)
                                ) {



                                    if (item.platform.contains("youtube")) {
                                        AsyncImage(
                                            model = "https://img.youtube.com/vi/${item.videoId}/hqdefault.jpg",
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp)
                                        )
                                    }


                                    if (item.platform.contains("instagram")) {
                                        AsyncImage(
                                            model = "https://www.instagram.com/p/${item.videoId}/media/?size=l",
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp)
                                        )
                                    }


                                    if (item.platform.contains("facebook")) {
                                        AsyncImage(
                                            model = "https://graph.facebook.com/${item.videoId}/picture",
                                            contentDescription = null,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp)
                                        )
                                    }
                                }
                            }
                        }


                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "author",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Normal,
                                color = creds.headerTextColor.toColor(),
                            )

                            Text(
                                text = item.author,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = creds.authorTextColor.toColor(),
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "video",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Normal,
                                color = creds.headerTextColor.toColor(),
                            )

                            Text(
                                modifier = Modifier
                                    .clickable {
                                        runCatching {
                                            uriHandler.openUri(buildVideoUrl(item, creds.baseUrls))
                                        }.onFailure { error ->
                                            Toast.makeText(context, "can't open video", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                text = item.videoName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = creds.videoNameTextColor.toColor(),
                                textAlign = TextAlign.Center,
                            )
                        }

                        Spacer(Modifier.height(8.dp))
//
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.End,
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//
//                            Box(
//                                modifier = Modifier
//                                    .width(70.dp)
//                                    .height(30.dp)
//                                    .border(
//                                        width = 1.dp,
//                                        color = creds.headerTextColor.toColor()
//                                    )
//                                    .background(color = creds.headerColor.toColor(), shape = RoundedCornerShape(8.dp))
//                                    .clickable {
//                                        runCatching {
//                                            uriHandler.openUri(buildVideoUrl(item, creds.baseUrls))
//                                        }.onFailure { error ->
//                                            Toast.makeText(context, "can't open video", Toast.LENGTH_SHORT).show()
//                                        }
//                                    },
//                                contentAlignment = Alignment.Center
//                            ) {
//                                Icon(
//                                    imageVector = Icons.Filled.PlayArrow,
//                                    contentDescription = "Play video",
//                                    tint = creds.headerTextColor.toColor(),
//                                )
//                            }

//                        }
                    }

                    HorizontalDivider(
                        color = Color.LightGray,
                        thickness = 1.dp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FacebookThumbnail(
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    var thumbnail by remember(videoUrl) { mutableStateOf<String?>(null) }

    LaunchedEffect(videoUrl) {
        thumbnail = fetchFacebookThumbnail(videoUrl)
    }

    AsyncImage(
        model = thumbnail ?: R.drawable.ic_launcher_foreground,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}


suspend fun fetchFacebookThumbnail(videoUrl: String): String? {
    val client = OkHttpClient()

    val request = Request.Builder()
        .url("https://www.facebook.com/plugins/video/oembed.json?url=$videoUrl")
        .header("User-Agent", "Mozilla/5.0")
        .build()

    return withContext(Dispatchers.IO) {
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return@withContext null

            val body = response.body?.string() ?: return@withContext null
            Regex(""""thumbnail_url":"(.*?)"""")
                .find(body)
                ?.groups
                ?.get(1)
                ?.value
                ?.replace("\\/", "/")
        }
    }
}




fun buildVideoUrl(
    item: Item,
    baseUrls: BaseUrls
): String {
    return when (item.platform.lowercase()) {
        "facebook" ->
            baseUrls.facebook + item.videoId

        "instagram" ->
            baseUrls.instagram + item.videoId

        "tiktok" ->
            baseUrls.tiktok.replace(
                "{username}",
                item.author.replace(" ", "").lowercase()
            ) + item.videoId

        "youtubewatch" ->
            baseUrls.youtubeWatch + item.videoId

        "youtubeshorts"  ->
            baseUrls.youtubeShorts + item.videoId

        "pinterest" ->
            baseUrls.pinterest + item.videoId

        else ->
            item.link
    }
}



@Composable
fun Tabs(
    creds: Creds,
    categories: List<CatItem>,
    selectedCat: String,
    onCatSelected: (CatItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex = categories.indexOfFirst { it.cat == selectedCat }.coerceAtLeast(0)
    val focusManager = LocalFocusManager.current

    ScrollableTabRow(
        containerColor = creds.headerColor.toColor(),
        selectedTabIndex = selectedIndex,
        edgePadding = 0.dp,
        modifier = modifier
    ) {
        categories.forEachIndexed { index, catItem ->
            Tab(
                selected = selectedIndex == index,
                onClick = {
                    focusManager.clearFocus()
                    onCatSelected(catItem)
                },
                text = {

                    Text(
                        text = catItem.label,
                        fontWeight = if (selectedIndex == index) FontWeight.Black else FontWeight.Medium,
                        color = if (selectedIndex == index) Color.White else creds.authorTextColor.toColor()
                    )

                }
            )
        }
    }
}


fun String.toColor(): Color = Color("$this".toColorInt())


@Composable
fun SearchBar(
    creds: Creds,
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    var hasFocus by remember { mutableStateOf(false) }


    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search by video link or name") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { state ->
                val newFocus = state.isFocused
                if (hasFocus && !newFocus) {
                    onQueryChange("") //  focus was lost → clear search text
                }
                hasFocus = newFocus
            },
        shape = RectangleShape,

        colors = TextFieldDefaults.colors(
            focusedContainerColor = creds.headerColor.toColor(),  //Color(0xFF0A4645),     // Background when focused
            unfocusedContainerColor = creds.headerColor.toColor(), // Color(0xFF0A4645),   // Background when not focused

            focusedTextColor = creds.authorTextColor.toColor(),
            unfocusedTextColor = creds.authorTextColor.toColor(),

            focusedPlaceholderColor = creds.authorTextColor.toColor(),
            unfocusedPlaceholderColor = creds.headerTextColor.toColor(),

            focusedLeadingIconColor = creds.authorTextColor.toColor(),
            unfocusedLeadingIconColor = creds.headerTextColor.toColor(),

            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}
