package com.panto.bible.presentation.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.panto.bible.R
import com.panto.bible.data.local.BibleConstant
import com.panto.bible.data.model.Verse
import com.panto.bible.presentation.ui.viewmodel.MainViewModel
import com.panto.bible.presentation.ui.viewmodel.SettingsViewModel
import com.panto.bible.ui.ThemedIcon
import com.panto.bible.ui.ThemedIconButton

@Composable
fun SearchScreen(
    mainViewModel: MainViewModel,
    settingsViewModel: SettingsViewModel,
    navController: NavHostController
) {
    val searchQuery by mainViewModel.searchQuery.collectAsState()
    val searchedVerses by mainViewModel.searchedVerses.collectAsState()
    val historyVerses by mainViewModel.historyVerses.collectAsState()
    val historyQueries by mainViewModel.historyQueries.collectAsState()

    val isLeftExpanded = remember { mutableStateOf(true) }

    BackHandler {
        mainViewModel.searchVerses("")
        navController.popBackStack()
    }

    LaunchedEffect(Unit) {
        mainViewModel.getHistories()
    }

    val leftColumnWeight by animateFloatAsState(targetValue = if (isLeftExpanded.value) 0.8f else 0.2f)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchField(mainViewModel = mainViewModel, onMenuClick = {})

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    if (isLeftExpanded.value) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
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
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "2자 이상 입력해 주세요",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                items(searchedVerses.size) { verse ->
                                    SearchedVerseItem(
                                        mainViewModel,
                                        navController,
                                        searchedVerses[verse],
                                        searchQuery
                                    )
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    if (!isLeftExpanded.value) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
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
                                HistoryVerseItem(
                                    mainViewModel,
                                    navController,
                                    historyVerses[index],
                                    historyQueries[index]
                                )
                            }
                        }

                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
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
fun SearchField(mainViewModel: MainViewModel, onMenuClick: () -> Unit) {

    val searchQuery by mainViewModel.searchQuery.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
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
                    BasicTextField(value = searchQuery, onValueChange = { query ->
                        val qry = query.replace("\n", "")
                        mainViewModel.searchVerses(qry)
                    }, decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
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
                        Modifier
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
fun SearchedVerseItem(
    mainViewModel: MainViewModel, navController: NavHostController, verse: Verse, query: String
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)
        .clickable(indication = null, interactionSource = remember {
            MutableInteractionSource()
        }) {

            navController.navigate(
                "VerseScreen"
            ) {
                mainViewModel.addHistory(
                    verse.page, verse.verse, query
                )

                mainViewModel.getVersesByPage(verse.page)
                mainViewModel.searchVerses("")
                mainViewModel.selectVerse(verse.verse)

                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                    saveState = false
                }
                launchSingleTop = true
            }
        }) {
        Text(
            text = "${BibleConstant.BOOK_LIST_KOR[verse.book]} ${verse.chapter + 1}:${verse.verseNumber} - ${verse.textRaw}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun HistoryVerseItem(
    mainViewModel: MainViewModel, navController: NavHostController, verse: Verse, query: String
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)
        .clickable(indication = null, interactionSource = remember {
            MutableInteractionSource()
        }) {

            navController.navigate(
                "VerseScreen"
            ) {
                mainViewModel.addHistory(
                    verse.page, verse.verse, query
                )

                mainViewModel.getVersesByPage(verse.page)
                mainViewModel.searchVerses("")
                mainViewModel.selectVerse(verse.verse)

                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                    saveState = false
                }
                launchSingleTop = true
            }
        }) {
        Text(
            text = query, style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "${BibleConstant.BOOK_LIST_KOR[verse.book]} ${verse.chapter + 1}:${verse.verseNumber} - ${verse.textRaw}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

