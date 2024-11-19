package com.panto.bible.presentation.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.panto.bible.R
import com.panto.bible.data.model.api.Dictionary
import com.panto.bible.presentation.ui.viewmodel.DictionaryViewModel
import com.panto.bible.ui.ThemedIcon
import com.panto.bible.ui.ThemedIconButton
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun DictionaryScreen(
    dictionaryViewModel: DictionaryViewModel, navController: NavHostController
) {
    val searchedItems by dictionaryViewModel.searchedItems.collectAsState()
    var query by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        dictionaryViewModel.searchDictionary("")
    }

    BackHandler {

        navController.navigate("VerseScreen") {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            DictionaryAppBar(onBackClick = {
                navController.navigate("VerseScreen") {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            })

            DictionarySearchField(onSearchChanged = { q ->
                query = q
            }, onSearchClick = {
                dictionaryViewModel.searchDictionary(query = query)
            })

            HorizontalDivider(
                modifier = Modifier
                    .height(2.dp)
                    .background(Color.Gray.copy(alpha = 0.5f)),
            )

            if (searchedItems.isNotEmpty()) {
                DictionarySearchResult(items = searchedItems, onItemClick = { i ->
                    val encodedUrl = URLEncoder.encode(i.link, StandardCharsets.UTF_8.toString())
                    navController.navigate("webViewScreen/$encodedUrl")
                })
            }
        }
    }
}

@Composable
fun DictionaryAppBar(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp), contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            ThemedIconButton(iconResLight = R.drawable.back_light,
                iconResDark = R.drawable.back_dark,
                modifier = Modifier.size(48.dp),
                onClick = { onBackClick() })
        }
        Box(modifier = Modifier.align(Alignment.Center)) {
            Text(
                text = "사전", style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
fun DictionarySearchField(
    onSearchChanged: (String) -> Unit, onSearchClick: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth(1f)
            .height(60.dp), contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ThemedIcon(
                iconResLight = R.drawable.search_light,
                iconResDark = R.drawable.search_dark,
                modifier = Modifier
                    .size(48.dp)
                    .padding(6.dp)
            )
            Spacer(Modifier.width(12.dp))
            BasicTextField(
                modifier = Modifier.weight(1f),
                value = query,
                onValueChange = { newText ->
                    val reduced = newText.replace("\n", "")
                    query = reduced
                    onSearchChanged(query)
                },
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                cursorBrush = SolidColor(value = MaterialTheme.colorScheme.onSurface),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            text = "검색 시 2자 이상 입력해 주세요", fontSize = 16.sp,
                            style = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.5f
                                )
                            ),
                            maxLines = 1, overflow = TextOverflow.Ellipsis,
                        )
                    }
                    innerTextField()
                },
                maxLines = 1,
            )
            Box(
                modifier = Modifier
                    .height(48.dp)
                    .width(100.dp)
                    .padding(horizontal = 20.dp)
                    .background(
                        MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(8.dp)
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                    ) {
                        onSearchClick()
                    }, contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "검색",
                    color = MaterialTheme.colorScheme.onSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}


@Composable
fun DictionarySearchResult(
    items: List<Dictionary.Items>, onItemClick: (Dictionary.Items) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxHeight()
    ) {
        if (items.isEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "검색 결과가 없습니다",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
        items(
            items.size
        ) { iIndex ->
            SearchedDictionaryItem(item = items[iIndex], onItemClick = { i -> onItemClick(i) })
        }
    }
}

@Composable
fun SearchedDictionaryItem(
    item: Dictionary.Items,
    onItemClick: (Dictionary.Items) -> Unit,
) {
    val t = item.title.replace("<b>", "").replace("</b>", "")
    val d = item.description.replace("<b>", "").replace("</b>", "")

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .padding(4.dp)
            .shadow(4.dp, shape = RoundedCornerShape(4.dp))
            .background(
                MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(4.dp)
            )
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .padding(vertical = 8.dp)
            .clickable(indication = null, interactionSource = remember {
                MutableInteractionSource()
            }) {
                onItemClick(item)
            }) {
            Text(
                text = t, style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = d, style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
