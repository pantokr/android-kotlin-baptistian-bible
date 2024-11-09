package com.panto.bible.presentation.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.panto.bible.R
import com.panto.bible.data.local.BibleConstant.BOOK_CHAPTER_COUNT_LIST
import com.panto.bible.data.model.Verse
import com.panto.bible.presentation.ui.viewmodel.MainViewModel
import com.panto.bible.presentation.ui.viewmodel.SettingsViewModel
import com.panto.bible.ui.ThemedIcon
import com.panto.bible.ui.ThemedIconButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.max

@Composable
fun SearchScreen(
    mainViewModel: MainViewModel,
    settingsViewModel: SettingsViewModel,
    navController: NavHostController
) {
    val verses by mainViewModel.verses.collectAsState()
    val currentBookList by mainViewModel.currentBookList.collectAsState()
    val currentBookShortList by mainViewModel.currentBookShortList.collectAsState()

    val searchQuery by mainViewModel.searchQuery.collectAsState()
    val searchedVerses by mainViewModel.searchedVerses.collectAsState()
    val historyVerses by mainViewModel.historyVerses.collectAsState()
    val historyQueries by mainViewModel.historyQueries.collectAsState()

    val isLeftExpanded = remember { mutableStateOf(true) }

    val findingListState = rememberLazyListState()
    val searchedListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val selectedBook = remember { mutableIntStateOf(verses[0].book) }

    val groupedVerses = searchedVerses.groupBy { it.book }
    val bookKeys = groupedVerses.keys.toList()
    val bookValues = groupedVerses.values.toList()

    LaunchedEffect(Unit) {
        mainViewModel.searchVerses("")
        mainViewModel.getHistories()
        findingListState.scrollToItem(
            max(
                0,
                selectedBook.intValue - findingListState.layoutInfo.visibleItemsInfo.size / 2
            )
        )
    }

    val leftColumnWeight by animateFloatAsState(targetValue = if (isLeftExpanded.value) 0.8f else 0.2f)
    var dragOffsetX by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchField(onSearchChanged = { query -> mainViewModel.searchVerses(query) },
            onMenuClick = {})

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectDragGestures(onDragEnd = {
                        if (dragOffsetX > 100) {
                            isLeftExpanded.value = true
                        } else if (dragOffsetX < -100) {
                            isLeftExpanded.value = false
                        }
                        dragOffsetX = 0f
                    }, onDrag = { _, dragAmount ->
                        dragOffsetX += dragAmount.x
                    })
                }
        ) {
            Box(modifier = Modifier
                .weight(leftColumnWeight)
                .fillMaxHeight()
                .background(
                    MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(8.dp)
                )
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    isLeftExpanded.value = true
                }) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLeftExpanded.value) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Search",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (searchQuery.length < 2) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "검색 시 2자 이상 입력해 주세요",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                            FindingGrid(
                                currentBookList = currentBookList,
                                selectedBook = selectedBook,
                                findingListState = findingListState,
                                onGridClick = { book, chapter ->
                                    navController.navigate(
                                        "VerseScreen"
                                    ) {
                                        mainViewModel.getVersesByBookChapter(book, chapter)
                                    }
                                }
                            )
                        } else {
                            Text(
                                "",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            SearchResult(currentBookList = currentBookList,
                                currentBookShortList = currentBookShortList,
                                searchQuery = searchQuery,
                                bookKeys = bookKeys,
                                bookValues = bookValues,
                                searchedListState = searchedListState,
                                coroutineScope = coroutineScope,
                                onVerseClick = { page, verseIndex, query ->
                                    navController.navigate("VerseScreen") {
                                        mainViewModel.addHistory(
                                            page, verseIndex, query
                                        )
                                        mainViewModel.getVersesByPage(page)
                                        mainViewModel.selectVerse(verseIndex)
                                    }
                                }
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),

                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "S",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Box(modifier = Modifier
                .weight(1f - leftColumnWeight)
                .fillMaxHeight()
                .background(
                    MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp)
                )
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    isLeftExpanded.value = false
                }) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (!isLeftExpanded.value) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Histories",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        LazyColumn(
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            items(historyVerses.size) { index ->
                                HistoryVerseItem(currentBookList = currentBookList,
                                    verse = historyVerses[index],
                                    query = historyQueries[index],
                                    onVerseClick = { page, verse, query ->
                                        navController.navigate(
                                            "VerseScreen"
                                        ) {
                                            mainViewModel.addHistory(
                                                page, verse, query
                                            )
                                            mainViewModel.getVersesByPage(page)
                                            mainViewModel.selectVerse(verse)
                                        }
                                    }
                                )
                            }
                        }

                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "H",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SearchField(onSearchChanged: (String) -> Unit, onMenuClick: () -> Unit) {
    var query by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(60.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .shadow(4.dp, shape = RoundedCornerShape(4.dp))
                    .background(
                        MaterialTheme.colorScheme.background, shape = RoundedCornerShape(4.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BasicTextField(value = query, onValueChange = { newText ->
                        val reduced = newText.replace("\n", "")  // 줄바꿈 제거
                        query = reduced
                        onSearchChanged(reduced)
                    }, decorationBox = { innerTextField ->
                        if (query.isEmpty()) {
                            Text(
                                text = "ex) 요 3장 16절 / 하나님이 세상을",
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .alpha(0.5f)
                            )
                        }
                        innerTextField()
                    }, modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                    )

                    ThemedIcon(
                        iconResLight = R.drawable.search_light,
                        iconResDark = R.drawable.search_dark,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(12.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            ThemedIconButton(
                iconResLight = R.drawable.menu_light,
                iconResDark = R.drawable.menu_dark,
                modifier = Modifier
                    .size(48.dp)
                    .padding(12.dp),
                onClick = onMenuClick
            )
        }
    }
}

@Composable
fun FindingGrid(
    currentBookList: Array<String>,
    selectedBook: MutableIntState,
    findingListState: LazyListState,
    onGridClick: (Int, Int) -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .width(160.dp)
                .padding(4.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {

            LazyColumn(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                state = findingListState
            ) {
                items(currentBookList.size) { index ->
                    Box(modifier = Modifier
                        .width(160.dp)
                        .height(40.dp)
                        .background(if (index == selectedBook.intValue) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                        ) {
                            selectedBook.intValue = index
                        }) {
                        Text(
                            text = currentBookList[index],
                            fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(BOOK_CHAPTER_COUNT_LIST[selectedBook.intValue]) { chapter ->
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clickable(indication = null,
                                interactionSource = remember { MutableInteractionSource() }) {
                                onGridClick(selectedBook.intValue, chapter)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "${chapter + 1}")
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResult(
    currentBookList: Array<String>,
    currentBookShortList: Array<String>,
    searchQuery: String,
    bookKeys: List<Int>,
    bookValues: List<List<Verse>>,
    searchedListState: LazyListState,
    coroutineScope: CoroutineScope,
    onVerseClick: (Int, Int, String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxHeight()
    ) {
        Box(
            modifier = Modifier
                .width(40.dp)
                .padding(4.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier
                    .width(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(bookKeys.size) { index ->
                    Box(modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                        ) {
                            coroutineScope.launch {
                                searchedListState.scrollToItem(index)
                            }
                        }) {
                        Text(
                            text = currentBookShortList[bookKeys[index]],
                            fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f), state = searchedListState
        ) {

            items(
                bookKeys.size,
            ) { bookIndex ->
                Text(
                    text = currentBookList[bookKeys[bookIndex]],
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(
                        vertical = 8.dp, horizontal = 16.dp
                    )
                )
                bookValues[bookIndex].forEachIndexed { verseIndex, verse ->
                    SearchedVerseItem(currentBookList = currentBookList,
                        verse = verse,
                        query = searchQuery,
                        onVerseClick = { page, index, query ->
                            onVerseClick(page, index, query)
                        })
                }
            }
        }
    }
}

@Composable
fun SearchedVerseItem(
    currentBookList: Array<String>,
    verse: Verse,
    query: String,
    onVerseClick: (Int, Int, String) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)
        .clickable(indication = null, interactionSource = remember {
            MutableInteractionSource()
        }) {
            onVerseClick(verse.page, verse.verse, query)
        }) {
        Text(
            text = "${currentBookList[verse.book]} ${verse.chapter + 1}",
            style = MaterialTheme.typography.titleSmall
        )

        val highlightStyle = SpanStyle(
            fontWeight = FontWeight.Bold,
        )

        val annotatedText = buildAnnotatedString {
            var startIndex = 0
            while (startIndex < verse.textRaw.length) {
                val matchIndex = verse.textRaw.indexOf(query, startIndex, ignoreCase = true)
                if (matchIndex == -1) {
                    append(verse.textRaw.substring(startIndex))
                    break
                } else {
                    append(verse.textRaw.substring(startIndex, matchIndex))
                    withStyle(highlightStyle) {
                        append(verse.textRaw.substring(matchIndex, matchIndex + query.length))
                    }
                    startIndex = matchIndex + query.length
                }
            }
        }

        Text(
            text = annotatedText, style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun HistoryVerseItem(
    currentBookList: Array<String>,
    verse: Verse,
    query: String,
    onVerseClick: (Int, Int, String) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)
        .clickable(indication = null, interactionSource = remember {
            MutableInteractionSource()
        }) {
            onVerseClick(verse.page, verse.verse, query)
        }) {
        Text(
            text = "검색어: ${query}", style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "${currentBookList[verse.book]} ${verse.chapter + 1} ${verse.verseNumber}",
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = verse.textRaw, style = MaterialTheme.typography.bodyMedium
        )
    }
}

