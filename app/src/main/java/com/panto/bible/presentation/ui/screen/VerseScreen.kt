package com.panto.bible.presentation.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.panto.bible.R
import com.panto.bible.data.local.BibleConstant.BOOK_LIST_KOR
import com.panto.bible.presentation.ui.viewmodel.MainViewModel
import com.panto.bible.presentation.ui.viewmodel.SettingsViewModel
import com.panto.bible.ui.ThemedIcon
import com.panto.bible.ui.ThemedIconButton
import com.panto.bible.ui.animation.animateHighLightVerse
import com.panto.bible.ui.animation.animateMenuAlpha
import com.panto.bible.ui.animation.animateMenuOffsetY
import com.panto.bible.ui.animation.animatePageAlpha
import com.panto.bible.ui.animation.animatePageOffsetY
import java.util.regex.Pattern

@Composable
fun VerseScreen(
    mainViewModel: MainViewModel, settingsViewModel: SettingsViewModel, navController: NavController
) {
    val verses by mainViewModel.verses.collectAsState()
    val selectedVerse by mainViewModel.selectedVerse.collectAsState()
    val currentPage by mainViewModel.currentPage.collectAsState()

    val fontSize by settingsViewModel.fontSize.collectAsState()
    val paragraphSpacing by settingsViewModel.paragraphSpacing.collectAsState()

    var isMenuVisible by remember { mutableStateOf(true) }
    var isPopupVisible by remember { mutableStateOf(false) }

    val pageChangeAlpha = animatePageAlpha(target = currentPage, startOpacity = 0f)
    val menuAlpha = animateMenuAlpha(target = isMenuVisible, startOpacity = 0f)
    val pageOffsetY = animatePageOffsetY(target = currentPage, startOffset = 20f)
    val appbarOffsetY = animateMenuOffsetY(target = isMenuVisible, startOffset = -50f)
    val buttonsOffsetY = animateMenuOffsetY(target = isMenuVisible, startOffset = 50f)

    val listState = rememberLazyListState()
    var dragOffsetX by remember { mutableStateOf(0f) }
    val scrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val scrollY = available.y
            if (scrollY != 0f) {
                isMenuVisible = false
            }
            return Offset.Zero
        }
    }

    var explanation by remember { mutableStateOf("") }

    Box(modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTapGestures(onTap = {
                if (isPopupVisible) {
                    isPopupVisible = false
                } else {
                    isMenuVisible = !isMenuVisible
                }
            })
        }) {

        LazyColumn(state = listState,
            modifier = Modifier
                .fillMaxSize()
                .alpha(pageChangeAlpha.value)
                .nestedScroll(scrollConnection)
                .pointerInput(Unit) {
                    detectDragGestures(onDragEnd = {
                        if (dragOffsetX > 100 && currentPage > 0) {
                            mainViewModel.getVersesByPage(currentPage - 1)
                            mainViewModel.selectVerse(-1)
                        } else if (dragOffsetX < -100) {
                            mainViewModel.getVersesByPage(currentPage + 1)
                            mainViewModel.selectVerse(-1)
                        }
                        dragOffsetX = 0f
                    }, onDrag = { _, dragAmount ->
                        dragOffsetX += dragAmount.x
                    })
                }) {
            item { Spacer(modifier = Modifier.height(100.dp)) }
            item {
                Text(
                    text = "${BOOK_LIST_KOR[verses[0].book]} ${verses[0].chapter + 1}",
                    fontSize = (fontSize * 1.6f).sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item { Spacer(modifier = Modifier.height(40.dp)) }
            items(verses.size) { index ->
                val isSelected = index == selectedVerse
                val highlightedBorderColor = animateHighLightVerse(target = isSelected)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = pageOffsetY.value.dp)
                        .padding(horizontal = 4.dp)
                ) {
                    verses[index].subTitle?.let {
                        if (it != "") {
                            Text(
                                text = it,
                                fontSize = (fontSize * 1.2f).sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(height = paragraphSpacing.dp))
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = paragraphSpacing.dp)
                            .border(
                                BorderStroke(
                                    4f.dp, highlightedBorderColor.value,
                                )
                            )
                    ) {
                        Text(
                            text = verses[index].verseNumber.replace("-", "\n-\n"),
                            style = TextStyle(
                                fontSize = fontSize.sp,
                                lineHeight = paragraphSpacing.sp,
                                letterSpacing = 0.1f.sp
                            ),
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .width((fontSize * 1.8).dp)
                        )
                        val verseText = verses[index].textOriginal
                        val commentaryText = verses[index].commentary

                        HighlightedText(commentaryText = commentaryText,
                            verseText = verseText,
                            fontSize = fontSize,
                            paragraphSpacing = paragraphSpacing,
                            onClick = { commentary ->
                                explanation = commentary
                                isPopupVisible = true
                                isMenuVisible = false
                            }
                        )
                    }
                }
            }
        }
        CommentaryPopUp(
            isPopupVisible = isPopupVisible,
            explanation = explanation,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            AppBar(mainViewModel = mainViewModel,
                appbarOffsetY = appbarOffsetY,
                menuAlpha = menuAlpha,
                onSearchClick = {
                    navController.navigate("searchScreen")
                    mainViewModel.selectVerse(-1)
                },
                onMenuClick = {})
            Spacer(modifier = Modifier.weight(1f))
            FloatingButtons(
                mainViewModel = mainViewModel,
                buttonsOffsetY = buttonsOffsetY,
                menuAlpha = menuAlpha
            )
        }
    }
    if (selectedVerse != -1) {
        LaunchedEffect(selectedVerse) {
            listState.scrollToItem(selectedVerse)
        }
    } else {
        LaunchedEffect(selectedVerse) {
            listState.scrollToItem(0)
        }
    }
    LaunchedEffect(currentPage) {
        listState.animateScrollToItem(0)
    }
}


@Composable
fun AppBar(
    mainViewModel: MainViewModel,
    appbarOffsetY: Animatable<Float, *>,
    menuAlpha: Animatable<Float, *>,
    onSearchClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    val verses by mainViewModel.verses.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = appbarOffsetY.value.dp)
            .alpha(menuAlpha.value)
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
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onSearchClick
                    )
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
                    if (verses.isEmpty()) {
                        return
                    }
                    Text(
                        "${BOOK_LIST_KOR[verses[0].book]} ${verses[0].chapter + 1}",
                        fontSize = 16.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .alpha(0.5f)
                            .padding(12.dp),
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
fun FloatingButtons(
    mainViewModel: MainViewModel,
    buttonsOffsetY: Animatable<Float, *>,
    menuAlpha: Animatable<Float, *>,
) {
    Text(
        "하단 버튼",
        Modifier
            .fillMaxWidth()
            .offset(y = buttonsOffsetY.value.dp)
            .alpha(menuAlpha.value),
        fontSize = 20.sp
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HighlightedText(
    verseText: String,
    commentaryText: String?,
    fontSize: Float,
    paragraphSpacing: Float,
    onClick: (String) -> Unit
) {
    val commentaryList = commentaryText?.split('|')
    val pattern = Pattern.compile("[1]?[ㄱ-ㅎ0-9a-z][)]")
    val matcher = pattern.matcher(verseText)

    val commentaryOrderList = mutableListOf<String>()
    val textList = mutableListOf<String>()
    var lastIndex = 0
    while (matcher.find()) {
        textList.add(verseText.substring(lastIndex, matcher.start()))
        textList.add(verseText.substring(matcher.start(), matcher.end()))
        commentaryOrderList.add(verseText.substring(matcher.start(), matcher.end()))
        lastIndex = matcher.end()
    }
    textList.add(verseText.substring(lastIndex))
    val words = textList.flatMap { it.split(" ") }.filter { it.isNotEmpty() }
    FlowRow {
        words.forEach { word ->
            if (pattern.matcher(word).matches()) {
                Text(
                    text = word, style = TextStyle(
                        fontSize = fontSize.sp,
                        lineHeight = paragraphSpacing.sp,
                        letterSpacing = 0.1f.sp
                    ), color = Color.Red, modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            val order = commentaryOrderList.indexOf(word)
                            onClick(commentaryList?.get(order) ?: "")
                        },
                    )
                )
            } else {
                Text(
                    text = "$word ",
                    style = TextStyle(
                        fontSize = fontSize.sp,
                        lineHeight = paragraphSpacing.sp,
                        letterSpacing = 0.1f.sp
                    ),
                )
            }
        }
    }
}

@Composable
fun CommentaryPopUp(isPopupVisible: Boolean, explanation: String, modifier: Modifier) {
    AnimatedVisibility(
        visible = isPopupVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(durationMillis = 500)
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(durationMillis = 500)
        ),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary)
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = explanation, fontSize = 16.sp)
            }
        }
    }
}