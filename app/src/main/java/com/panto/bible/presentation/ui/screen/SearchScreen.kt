package com.panto.bible.presentation.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
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
    val searchedVerses by mainViewModel.searchedVerses.collectAsState()

    BackHandler {
        mainViewModel.searchVerses("")
        navController.popBackStack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchField(mainViewModel = mainViewModel, onMenuClick = {})

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(searchedVerses.size) { verse ->
                SearchedVerseItem(mainViewModel, navController, searchedVerses[verse])
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
    mainViewModel: MainViewModel, navController: NavHostController, verse: Verse
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)
        .clickable {

            navController.navigate(
                "VerseScreen"
            ) {
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

