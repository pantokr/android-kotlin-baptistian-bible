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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.panto.bible.R
import com.panto.bible.data.model.Save
import com.panto.bible.presentation.ui.viewmodel.MainViewModel
import com.panto.bible.ui.ThemedVectorIconButton

@Composable
fun SaveScreen(
    mainViewModel: MainViewModel, navController: NavHostController
) {
    val allSaved by mainViewModel.allSaved.collectAsState()

    LaunchedEffect(Unit) {
        mainViewModel.getAllSaves()
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
            SaveAppBar(onBackClick = {
                navController.navigate("VerseScreen") {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            })

            HorizontalDivider(
                modifier = Modifier
                    .height(2.dp)
                    .background(Color.Gray.copy(alpha = 0.5f)),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .clickable(indication = null,
                        interactionSource = remember { MutableInteractionSource() }) {
                        mainViewModel.deleteAllSaves()
                    }, contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        "전체삭제",
                        style = MaterialTheme.typography.bodyMedium,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }

            if (allSaved.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxHeight()
                ) {
                    items(
                        allSaved.size
                    ) { sIndex ->
                        SaveItem(saves = allSaved[sIndex], onSaveClick = { s ->
                            mainViewModel.getVersesByPage(s.page)
                            mainViewModel.selectVerse(s.verse)
                            navController.navigate("VerseScreen") {
                                popUpTo(navController.graph.id) {
                                    inclusive = true
                                }
                            }
                        }, onDeleteSaveClick = { s ->
                            mainViewModel.deleteSaves(saves = s)
                        })
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
                        "저장된 말씀이 없습니다",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
fun SaveAppBar(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp), contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            ThemedVectorIconButton(iconRes = R.drawable.back_light,
                modifier = Modifier.size(48.dp),
                onClick = { onBackClick() })
        }
        Box(modifier = Modifier.align(Alignment.Center)) {
            Text(
                text = "저장된 말씀", style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
fun SaveItem(
    saves: List<Save>, onSaveClick: (Save) -> Unit, onDeleteSaveClick: (List<Save>) -> Unit
) {
    val t = saves[0].time.substringAfter(".")

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
                onSaveClick(saves[0])
            }) {

            Text(
                text = t,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = saves[0].title, style = MaterialTheme.typography.titleSmall
            )
        }

        Box(
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            ThemedVectorIconButton(iconRes = R.drawable.delete_light,
                modifier = Modifier.size(48.dp),
                onClick = { onDeleteSaveClick(saves) })
        }
    }
}
