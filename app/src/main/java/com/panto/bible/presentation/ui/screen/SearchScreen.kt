package com.panto.bible.presentation.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.panto.bible.R
import com.panto.bible.data.local.BibleConstant.BOOK_CHAPTER_COUNT_LIST
import com.panto.bible.data.model.Verse
import com.panto.bible.presentation.ui.viewmodel.MainViewModel
import com.panto.bible.ui.ThemedVectorIconButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.max

@Composable
fun SearchScreen(
    mainViewModel: MainViewModel,
    navController: NavHostController
) {
    val verses by mainViewModel.verses.collectAsState()
    val currentBookList by mainViewModel.currentBookList.collectAsState()
    val currentBookShortList by mainViewModel.currentBookShortList.collectAsState()

    val searchedVerses by mainViewModel.searchedVerses.collectAsState()
    val historyVerses by mainViewModel.historyVerses.collectAsState()
    val historyQueries by mainViewModel.historyQueries.collectAsState()

    var isHistoryExpanded by remember { mutableStateOf(false) }

    val findingListState = rememberLazyListState()
    val searchedListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val selectedBook = remember { mutableIntStateOf(verses[0].book) }

    val groupedVerses = searchedVerses.groupBy { it.book }
    val bookKeys = groupedVerses.keys.toList()
    val bookValues = groupedVerses.values.toList()

    val query = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        mainViewModel.searchVerses("")
        mainViewModel.getHistories()
        findingListState.scrollToItem(
            max(
                0, selectedBook.intValue - findingListState.layoutInfo.visibleItemsInfo.size / 2
            )
        )
    }

    LaunchedEffect(query.value) {
        if (query.value.isNotBlank()) {
            isHistoryExpanded = false
        }
    }

    BackHandler {
        if (isHistoryExpanded) {
            isHistoryExpanded = false
        } else {
            navController.navigate("VerseScreen") {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column {
            SearchField(query = query, onBackClick = {
                navController.navigate("VerseScreen") {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            }, onSearchChanged = { query ->
                mainViewModel.searchVerses(query)
            })

            HorizontalDivider(
                modifier = Modifier
                    .height(2.dp)
                    .background(color = Color.Gray.copy(alpha = 0.5f)),
            )

            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(
                            MaterialTheme.colorScheme.background, shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (query.value.length < 2) {
                            FindingGrid(currentBookList = currentBookList,
                                selectedBook = selectedBook,
                                findingListState = findingListState,
                                onGridClick = { book, chapter ->
                                    navController.navigate("VerseScreen") {
                                        popUpTo(navController.graph.id) {
                                            inclusive = true
                                        }
                                        mainViewModel.getVersesByBookAndChapter(book, chapter)
                                    }
                                })
                        } else {
                            if (bookKeys.isEmpty()) {
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

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                            ) {

                                SearchResult(currentBookList = currentBookList,
                                    currentBookShortList = currentBookShortList,
                                    query = query.value,
                                    bookKeys = bookKeys,
                                    bookValues = bookValues,
                                    searchedListState = searchedListState,
                                    coroutineScope = coroutineScope,
                                    onVerseClick = { v, query ->
                                        navController.navigate("VerseScreen") {
                                            popUpTo(navController.graph.id) {
                                                inclusive = true
                                            }
                                            mainViewModel.insertHistory(
                                                v, query
                                            )
                                            mainViewModel.getVersesByPage(v.page)
                                            mainViewModel.selectVerse(v.verse)
                                        }
                                    })
                            }
                        }
                    }
                }

                this@Column.AnimatedVisibility(
                    visible = isHistoryExpanded,
                    enter = slideInVertically(
                        initialOffsetY = { it }, animationSpec = tween(durationMillis = 500)
                    ) + fadeIn(animationSpec = tween(durationMillis = 500)),
                    exit = slideOutVertically(
                        targetOffsetY = { it }, animationSpec = tween(durationMillis = 500)
                    ) + fadeOut(animationSpec = tween(durationMillis = 500))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .background(
                                MaterialTheme.colorScheme.secondary
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding()

                        ) {
                            if (historyVerses.isEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        "검색 기록 결과가 없습니다",
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            } else {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.clickable(indication = null,
                                        interactionSource = remember { MutableInteractionSource() }) {
                                        mainViewModel.deleteAllHistory()
                                    }) {
                                        Text(
                                            "기록",
                                            style = MaterialTheme.typography.titleMedium,
                                        )
                                    }

                                    Box(modifier = Modifier.clickable(indication = null,
                                        interactionSource = remember { MutableInteractionSource() }) {
                                        mainViewModel.deleteAllHistory()
                                    }) {
                                        Text(
                                            "전체삭제",
                                            style = MaterialTheme.typography.bodyMedium,
                                            textDecoration = TextDecoration.Underline
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            LazyColumn(
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                items(historyVerses.size) { index ->
                                    HistoryVerseItem(currentBookList = currentBookList,
                                        verse = historyVerses[index],
                                        query = historyQueries[index],
                                        onVerseClick = { v, query ->
                                            navController.navigate(
                                                "VerseScreen"
                                            ) {
                                                popUpTo(navController.graph.id) {
                                                    inclusive = true
                                                }
                                                mainViewModel.insertHistory(
                                                    v, query
                                                )
                                                mainViewModel.getVersesByPage(v.page)
                                                mainViewModel.selectVerse(v.verse)
                                            }
                                        },
                                        onDeleteVerseClick = { v, query ->
                                            mainViewModel.deleteHistory(v, query)
                                        })
                                }
                            }
                        }
                    }
                }
            }
        }


        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            FloatingHistoryButton(isHistoryExpanded = isHistoryExpanded,
                onHistoryButtonClick = { isHistoryExpanded = !isHistoryExpanded })
        }
    }
}

@Composable
fun SearchField(
    query: MutableState<String>, onBackClick: () -> Unit, onSearchChanged: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)

    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ThemedVectorIconButton(iconRes = R.drawable.back_light,
                modifier = Modifier.size(48.dp),
                onClick = { onBackClick() })
            Spacer(Modifier.width(12.dp))
            Box(
                modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = query.value,
                    onValueChange = { newText ->
                        val reduced = newText.replace("\n", "")
                        query.value = reduced
                        onSearchChanged(reduced)
                    },
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp
                    ),
                    cursorBrush = SolidColor(value = MaterialTheme.colorScheme.onSurface),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (query.value.isEmpty()) {
                                Text(
                                    text = "검색 시 2자 이상 입력해 주세요",
                                    style = TextStyle(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        fontSize = 16.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            innerTextField()
                        }
                    },
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
fun FloatingHistoryButton(isHistoryExpanded: Boolean, onHistoryButtonClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isHistoryExpanded) {
            ThemedVectorIconButton(iconRes = R.drawable.history_light,
                modifier = Modifier
                    .size(60.dp)
                    .shadow(4.dp, shape = CircleShape)
                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                onClick = { onHistoryButtonClick() })
        } else {
            ThemedVectorIconButton(iconRes = R.drawable.close_light,
                modifier = Modifier
                    .size(60.dp)
                    .shadow(4.dp, shape = CircleShape)
                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                onClick = { onHistoryButtonClick() })
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
            modifier = Modifier.width(160.dp), contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                state = findingListState
            ) {
                items(currentBookList.size) { index ->
                    Box(modifier = Modifier
                        .width(160.dp)
                        .height(60.dp)
                        .background(
                            if (index == selectedBook.intValue) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                        )
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                        ) {
                            selectedBook.intValue = index
                        }) {
                        Text(
                            text = currentBookList[index],
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.Center),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)

        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(BOOK_CHAPTER_COUNT_LIST[selectedBook.intValue]) { chapter ->
                    Box(modifier = Modifier
                        .size(60.dp)
                        .clickable(indication = null,
                            interactionSource = remember { MutableInteractionSource() }) {
                            onGridClick(selectedBook.intValue, chapter)
                        }, contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${chapter + 1}",
                            textDecoration = TextDecoration.Underline,
                            style = MaterialTheme.typography.bodyMedium
                        )
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
    query: String,
    bookKeys: List<Int>,
    bookValues: List<List<Verse>>,
    searchedListState: LazyListState,
    coroutineScope: CoroutineScope,
    onVerseClick: (Verse, String) -> Unit
) {
    val fs = LocalDensity.current.fontScale
    val absoluteFontSizeInPx = 12f
    val fontSizeInDp = absoluteFontSizeInPx / fs

    Row(
        modifier = Modifier.fillMaxHeight()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f), state = searchedListState
        ) {

            items(
                bookKeys.size,
            ) { bookIndex ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        ), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentBookList[bookKeys[bookIndex]],
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(
                            vertical = 8.dp, horizontal = 16.dp
                        )
                    )
                }
                bookValues[bookIndex].forEach { verse ->
                    SearchedVerseItem(verse = verse,
                        query = query,
                        currentBookList = currentBookList,
                        onVerseClick = { v, query ->
                            onVerseClick(v, query)
                        })
                }
            }
        }

        Box(
            modifier = Modifier
                .width(48.dp)
                .padding(8.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier.width(40.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(bookKeys.size) { index ->
                    Box(modifier = Modifier
                        .size(48.dp)
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
                            fontSize = fontSizeInDp.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
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
    onVerseClick: (Verse, String) -> Unit,
) {

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
            .background(
                MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(4.dp)
            )
            .clickable(indication = null, interactionSource = remember {
                MutableInteractionSource()
            }) {
                onVerseClick(verse, query)
            }) {
            Text(
                text = "${currentBookList[verse.book]} ${verse.chapter + 1}:${verse.verseNumber}",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))

            val highlightStyle = SpanStyle(fontWeight = FontWeight.Bold)
            val queryWords = query.split(" ").filter { it.isNotEmpty() }

            val annotatedText = buildAnnotatedString {
                var startIndex = 0
                while (startIndex < verse.textRaw.length) {
                    val match = queryWords.mapNotNull { word ->
                        val index = verse.textRaw.indexOf(word, startIndex, ignoreCase = true)
                        if (index != -1) index to word else null
                    }.minByOrNull { it.first }

                    if (match == null) {
                        append(verse.textRaw.substring(startIndex))
                        break
                    } else {
                        val (matchIndex, matchedWord) = match
                        append(verse.textRaw.substring(startIndex, matchIndex))
                        withStyle(highlightStyle) {
                            append(
                                verse.textRaw.substring(
                                    matchIndex, matchIndex + matchedWord.length
                                )
                            )
                        }
                        startIndex = matchIndex + matchedWord.length
                    }
                }
            }

            Text(
                text = annotatedText, style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun HistoryVerseItem(
    verse: Verse,
    query: String,
    currentBookList: Array<String>,
    onVerseClick: (Verse, String) -> Unit,
    onDeleteVerseClick: (Verse, String) -> Unit
) {
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
                onVerseClick(verse, query)
            }) {
            Text(
                text = "검색어: ${query}",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${currentBookList[verse.book]} ${verse.chapter + 1}:${verse.verseNumber}",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = verse.textRaw, style = MaterialTheme.typography.bodyMedium
            )
        }

        Box(
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            ThemedVectorIconButton(iconRes = R.drawable.delete_light,
                modifier = Modifier.size(48.dp),
                onClick = { onDeleteVerseClick(verse, query) })
        }
    }
}

