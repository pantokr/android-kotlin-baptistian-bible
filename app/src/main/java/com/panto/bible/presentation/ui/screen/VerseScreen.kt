package com.panto.bible.presentation.ui.screen

import android.app.Activity
import android.content.ClipData
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.panto.bible.R
import com.panto.bible.data.local.BibleConstant.SAVE_COLORS
import com.panto.bible.data.model.Save
import com.panto.bible.data.model.Verse
import com.panto.bible.presentation.ui.viewmodel.MainViewModel
import com.panto.bible.presentation.ui.viewmodel.SettingsViewModel
import com.panto.bible.ui.ThemedIcon
import com.panto.bible.ui.ThemedIconButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@Composable
fun VerseScreen(
    mainViewModel: MainViewModel, settingsViewModel: SettingsViewModel, navController: NavController
) {
    val verses by mainViewModel.verses.collectAsState() // 현재 페이지 Verse
    val subVerses by mainViewModel.subVerses.collectAsState() // 현재 페이지 SubVerse
    val selectedVerse by mainViewModel.selectedVerse.collectAsState() // 검색 시 Verse
    val saved by mainViewModel.saved.collectAsState() // 현재 페이지 Save
    val currentPage by mainViewModel.currentPage.collectAsState() // 현재 페이지
    val currentBookList by mainViewModel.currentBookList.collectAsState() // 현재 사용하고 있는 BOOK_LIST

    val fontSize by settingsViewModel.fontSize.collectAsState() // 글자 크기
    val paragraphSpacing by settingsViewModel.paragraphSpacing.collectAsState() // 문단 간격
    val saveColor by settingsViewModel.saveColor.collectAsState() // 형광펜 색깔

    var isMenuVisible by remember { mutableStateOf(true) } // AppBar + Floating Button
    var isMenuDrawerVisible by remember { mutableStateOf(false) } // Drawer
    var isPopupVisible by remember { mutableStateOf(false) } // 성경 주석
    var isHighlightMode by remember { mutableStateOf(false) } // 강조 모드
    var isPaletteVisible by remember { mutableStateOf(false) } // 색상 팔레트

    val listState = rememberLazyListState() // LazyColumn 스크롤
    val scrollConnection = object : NestedScrollConnection { //스크롤 시 UI 사라짐
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val scrollY = available.y
            if (scrollY != 0f) {
                isMenuVisible = false
                isPopupVisible = false
                isMenuDrawerVisible = false
            }
            return Offset.Zero
        }
    }

    var dragOffsetX by remember { mutableFloatStateOf(0f) } // 좌우 드래그
    val pageOffsetY = remember { Animatable(0f) } // 페이지 변환 시 페이지 애니메이션 용도
    val pageAlpha = remember { Animatable(1f) } // 페이지 변환 시 페이지 애니메이션 용도

    // coroutineScope launch
    val coroutineScope = rememberCoroutineScope()  // 드래그 시 바로 이벤트 적용하도록
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed) // Drawer

    val basicTapEffect: () -> Unit = { // Lazy Column이 Tap 이벤트 가로채는 것 방지
        if (!isHighlightMode) {
            isMenuVisible = !isMenuVisible
        }
        isPaletteVisible = false
        isPopupVisible = false
        isMenuDrawerVisible = false
    }
    val pageDragEffect: (page: Int) -> Unit = {
        mainViewModel.selectVerse(-1)
        mainViewModel.getVersesByPage(page = it)
        coroutineScope.launch {
            pageAlpha.snapTo(0f)
            pageAlpha.animateTo(1f, animationSpec = tween(durationMillis = 2000))
        }

        coroutineScope.launch {
            pageOffsetY.snapTo(
                100f
            )
            pageOffsetY.animateTo(0f, animationSpec = tween(durationMillis = 1000))
        }
    }
    val navChecker: () -> Unit = {
        isMenuDrawerVisible = false
        mainViewModel.selectVerse(-1)
    }
    val menuDrawerIndication: () -> Unit = {
        coroutineScope.launch {
            drawerState.apply {
                if (isMenuDrawerVisible) open() else close()
            }
        }
    }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    var explanation by remember { mutableStateOf("") } // 성경 주석 내용
    val tappedVerses = remember { mutableStateListOf<Int>() } // Highlight 중인 구절

    LaunchedEffect(isMenuDrawerVisible) {
        menuDrawerIndication()
    }

    LaunchedEffect(currentPage) {
        isPopupVisible = false
        isHighlightMode = false

        if (selectedVerse != -1) {
            listState.scrollToItem(selectedVerse)
        } else {
            listState.scrollToItem(0)
        }
    }

    LaunchedEffect(isHighlightMode) {
        if (!isHighlightMode) {
            tappedVerses.clear()
            isMenuVisible = true
            isPaletteVisible = false
        } else {
            isMenuVisible = false
            isMenuDrawerVisible = false
        }
    }

    BackHandler {
        if (isHighlightMode) {
            if (isPaletteVisible) {
                isPaletteVisible = false
            } else {
                isHighlightMode = false
            }
        } else {
            if (isMenuDrawerVisible) {
                isMenuDrawerVisible = false
            } else {
                (context as? Activity)?.finish()
            }
        }
    }

    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        ModalDrawerSheet(
            modifier = Modifier.width(240.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(MaterialTheme.colorScheme.primary),
            ) {
                Box(
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    ThemedIconButton(iconResLight = R.drawable.close_light,
                        iconResDark = R.drawable.close_dark,
                        modifier = Modifier.size(60.dp),
                        onClick = { isMenuDrawerVisible = !isMenuDrawerVisible })
                }
                Box(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    ThemedIcon(
                        iconResLight = R.drawable.icon,
                        iconResDark = R.drawable.icon,
                        modifier = Modifier.size(120.dp)
                    )
                }
            }
            DrawerItem(label = "개인 설정", onClick = {
                navController.navigate("settingsScreen")
                isMenuDrawerVisible = !isMenuDrawerVisible
            })
            HorizontalDivider()
            DrawerItem(label = "찬송가", onClick = {
                navController.navigate("hymnScreen")
                isMenuDrawerVisible = !isMenuDrawerVisible
            })
            HorizontalDivider()
            DrawerItem(label = "사전", onClick = {
                navController.navigate("dictionaryScreen")
                isMenuDrawerVisible = !isMenuDrawerVisible
            })
        }
    }) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(if (isHighlightMode) Color.Gray.copy(alpha = 0.25f) else MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { basicTapEffect() })
            }) {
            LazyColumn(state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollConnection)
                    .pointerInput(Unit) {
                        detectDragGestures(onDragEnd = {
                            if (dragOffsetX > 100 && currentPage > 0) {
                                pageDragEffect(currentPage - 1)
                            } else if (dragOffsetX < -100 && currentPage < 1188) {
                                pageDragEffect(currentPage + 1)
                            }
                            dragOffsetX = 0f
                        }, onDrag = { _, dragAmount ->
                            dragOffsetX += dragAmount.x
                        })
                    }) {

                item { Spacer(modifier = Modifier.height(120.dp)) }
                item {
                    Text(
                        text = "${currentBookList[verses[0].book]} ${verses[0].chapter + 1}",
                        fontSize = (fontSize * 1.6f).sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item { Spacer(modifier = Modifier.height(40.dp)) }
                items(verses.size) { vIndex ->
                    val isSelected = vIndex == selectedVerse
                    val highlightedBorderColor = remember {
                        Animatable(Color.Transparent)
                    }
                    val toHighlightColor = MaterialTheme.colorScheme.primary

                    LaunchedEffect(isSelected) {
                        if (isSelected) {
                            highlightedBorderColor.animateTo(
                                toHighlightColor,
                                animationSpec = tween(durationMillis = 1000)
                            )
                            highlightedBorderColor.animateTo(
                                Color.Transparent,
                                animationSpec = tween(durationMillis = 1000)
                            )

                            delay(2000)
                            mainViewModel.selectVerse(-1)
                        }
                    }

                    val isTapped = vIndex in tappedVerses
                    val saveData = saved.find { it.verse == vIndex }
                    val verseText =
                        if (isHighlightMode) verses[vIndex].textRaw else verses[vIndex].textOriginal
                    val commentaryText = verses[vIndex].commentary

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                        .offset { IntOffset(0, pageOffsetY.value.toInt()) }
                        .alpha(alpha = pageAlpha.value)) {

                        verses[vIndex].subTitle?.let {
                            if (it != "" && !isHighlightMode) {
                                Text(
                                    text = it,
                                    fontSize = (fontSize * 1.2f).sp,
                                    fontWeight = FontWeight.Bold,
                                )
                                Spacer(modifier = Modifier.height(height = paragraphSpacing.dp))
                            }
                        }

                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isTapped) MaterialTheme.colorScheme.secondary.copy(
                                    alpha = 0.75f
                                ) else Color.Transparent
                            )
                            .border(
                                BorderStroke(
                                    2f.dp,
                                    highlightedBorderColor.value
                                )
                            )
                            .pointerInput(Unit) {
                                detectTapGestures(onLongPress = {
                                    if (!isHighlightMode) {
                                        isHighlightMode = true
                                        if (vIndex !in tappedVerses) {
                                            tappedVerses.add(vIndex)
                                        }
                                    }
                                }, onTap = {
                                    if (isHighlightMode) {
                                        if (vIndex !in tappedVerses) {
                                            tappedVerses.add(vIndex)
                                        } else {
                                            tappedVerses.remove(vIndex)
                                        }
                                    } else {
                                        basicTapEffect()
                                    }
                                })
                            }) {
                            Text(
                                text = verses[vIndex].verseNumber.replace("-", "\n-\n"),
                                style = TextStyle(
                                    fontSize = fontSize.sp,
                                    lineHeight = paragraphSpacing.sp,
                                    letterSpacing = 0.1f.sp
                                ),
                                modifier = Modifier
                                    .padding(horizontal = 2.dp)
                                    .width((fontSize * 1.8).dp)
                            )

                            CommentaryIndexedText(saveData = saveData,
                                commentaryText = commentaryText,
                                verseText = verseText,
                                fontSize = fontSize,
                                paragraphSpacing = paragraphSpacing,
                                onCommentaryClick = { commentary ->
                                    if (explanation == commentary) {
                                        explanation = ""
                                        isPopupVisible = false
                                        isMenuVisible = false
                                    } else {
                                        explanation = commentary
                                        isPopupVisible = true
                                        isMenuVisible = false
                                    }
                                })
                        }

                        if (subVerses.isNotEmpty() && !isHighlightMode) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = (paragraphSpacing * 0.25f).dp)
                                    .pointerInput(Unit) {
                                        detectTapGestures(onLongPress = {
                                            isHighlightMode = true
                                        }, onTap = {
                                            basicTapEffect()
                                        })
                                    },
                            ) {

                                if (vIndex < subVerses.size) {
                                    Text(
                                        text = ">",
                                        style = TextStyle(
                                            fontSize = fontSize.sp,
                                            lineHeight = paragraphSpacing.sp,
                                            letterSpacing = 0.1f.sp
                                        ),
                                        color = Color.Gray,

                                        modifier = Modifier
                                            .padding(horizontal = 2.dp)
                                            .width((fontSize * 1.8).dp),
                                    )

                                    Text(
                                        text = subVerses[vIndex], style = TextStyle(
                                            fontSize = fontSize.sp,
                                            lineHeight = paragraphSpacing.sp,
                                            letterSpacing = 0.1f.sp
                                        ), color = Color.Gray
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(paragraphSpacing.dp))
                    }
                }

                item { Spacer(modifier = Modifier.height(120.dp)) }
            }

            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                CommentaryPopUp(
                    isPopupVisible = isPopupVisible,
                    explanation = explanation,
                )
            }

            Box(modifier = Modifier.align(Alignment.TopCenter)) {
                AppBar(
                    isMenuVisible = isMenuVisible,
                    verses = verses,
                    currentBookList = currentBookList,
                    onSearchClick = {
                        navController.navigate("searchScreen")
                        mainViewModel.selectVerse(-1)
                    },
                    onMenuDrawerClick = {
                        isMenuDrawerVisible = !isMenuDrawerVisible
                    },
                    onVersionClick = { navController.navigate("versionScreen") },
                )
            }

            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                Column {
                    AnimatedVisibility(visible = isPaletteVisible,
                        enter = slideInVertically(animationSpec = tween(durationMillis = 500)) { it } + fadeIn(
                            animationSpec = tween(durationMillis = 500)
                        ),
                        exit = slideOutVertically(animationSpec = tween(durationMillis = 500)) { it } + fadeOut(
                            animationSpec = tween(durationMillis = 500)
                        )) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(24.dp)
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SAVE_COLORS.forEachIndexed { colorIndex, color ->
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .padding(12.dp)
                                        .background(color = color, shape = CircleShape)
                                        .border(
                                            border = BorderStroke(
                                                width = 1.dp,
                                                color = MaterialTheme.colorScheme.onBackground.copy(
                                                    0.5f
                                                )
                                            ), shape = CircleShape
                                        )
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() },
                                        ) {
                                            isPaletteVisible = !isPaletteVisible
                                            settingsViewModel.updateSaveColor(colorIndex)
                                        },
                                )
                            }
                            Box(modifier = Modifier
                                .size(60.dp)
                                .padding(12.dp)
                                .background(color = Color.Transparent, shape = CircleShape)
                                .border(
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.onBackground.copy(
                                            0.5f
                                        )
                                    ), shape = CircleShape
                                )
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() },
                                ) {
                                    isPaletteVisible = !isPaletteVisible
                                    settingsViewModel.updateSaveColor(-1)
                                }) {
                                ThemedIcon(
                                    iconResLight = R.drawable.eraser_light,
                                    iconResDark = R.drawable.eraser_dark,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                        }
                    }


                    FloatingButtons(isMenuVisible = isMenuVisible,
                        isHighlightMode = isHighlightMode,
                        onHighlightModeButtonClick = { isHighlightMode = true },
                        currentColor = if (saveColor != -1) SAVE_COLORS[saveColor] else null,
                        onCopyButtonClick = {
                            if (tappedVerses.isEmpty()) {
                                Toast.makeText(context, "선택된 절이 없습니다.", Toast.LENGTH_SHORT).show()
                                return@FloatingButtons
                            }
                            var content =
                                "${currentBookList[verses[0].book]} ${verses[0].chapter + 1}\n"
                            for (v in tappedVerses.sorted()) {
                                content += "${verses[v].verseNumber}. ${verses[v].textRaw}\n"
                            }

                            val clipData = ClipData.newPlainText("Bible", content)
                            val clipEntry = ClipEntry(clipData)
                            clipboardManager.setClip(clipEntry)
                        },
                        onSelectAllButtonClick = {
                            if (tappedVerses.size != verses.size) {
                                tappedVerses.clear()
                                tappedVerses.addAll(verses.indices)
                            } else {
                                tappedVerses.clear()
                            }
                        },
                        onCloseButtonClick = {
                            isHighlightMode = false
                        },
                        onColorButtonClick = {
                            isPaletteVisible = !isPaletteVisible
                        },
                        onSaveButtonClick = {
                            if (tappedVerses.isEmpty()) {
                                Toast.makeText(context, "선택된 절이 없습니다.", Toast.LENGTH_SHORT).show()
                                return@FloatingButtons
                            }
                            for (v in tappedVerses.sorted()) {
                                mainViewModel.insertSave(verse = verses[v], color = saveColor)
                            }
                        })
                }
            }
        }
    }
}

@Composable
fun DrawerItem(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(20.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            ) { onClick() }, contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
        )
    }
}

@Composable
fun AppBar(
    isMenuVisible: Boolean,
    verses: List<Verse>,
    currentBookList: Array<String>,
    onSearchClick: () -> Unit,
    onMenuDrawerClick: () -> Unit,
    onVersionClick: () -> Unit,
) {
    Column {
        AnimatedVisibility(
            visible = isMenuVisible, enter = slideInVertically(
                animationSpec = tween(durationMillis = 500)
            ) + fadeIn(animationSpec = tween(durationMillis = 500)), exit = slideOutVertically(
                animationSpec = tween(durationMillis = 500)
            ) + fadeOut(animationSpec = tween(durationMillis = 500))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ThemedIconButton(iconResLight = R.drawable.menu_light,
                    iconResDark = R.drawable.menu_dark,
                    modifier = Modifier.size(48.dp),
                    onClick = { onMenuDrawerClick() })
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(4.dp, shape = RoundedCornerShape(24.dp), clip = false)
                        .background(
                            MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(24.dp)
                        )
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                        ) { onSearchClick() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    ThemedIcon(
                        iconResLight = R.drawable.search_light,
                        iconResDark = R.drawable.search_dark,
                        modifier = Modifier
                            .size(48.dp)
                            .padding(12.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "${currentBookList[verses[0].book]} ${verses[0].chapter + 1}",
                        fontSize = 16.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .alpha(0.5f)
                    )
                }
                ThemedIconButton(iconResLight = R.drawable.translate_light,
                    iconResDark = R.drawable.translate_dark,
                    modifier = Modifier.size(48.dp),
                    onClick = { onVersionClick() })
            }
        }
    }
}


@Composable
fun FloatingButtons(
    isMenuVisible: Boolean,
    isHighlightMode: Boolean,
    currentColor: Color?,
    onHighlightModeButtonClick: () -> Unit,
    onCopyButtonClick: () -> Unit,
    onSelectAllButtonClick: () -> Unit,
    onCloseButtonClick: () -> Unit,
    onColorButtonClick: () -> Unit,
    onSaveButtonClick: () -> Unit
) {
    Box {
        AnimatedVisibility(
            visible = isMenuVisible, enter = slideInVertically(
                initialOffsetY = { it }, animationSpec = tween(durationMillis = 500)
            ) + fadeIn(animationSpec = tween(durationMillis = 500)), exit = slideOutVertically(
                targetOffsetY = { it }, animationSpec = tween(durationMillis = 500)
            ) + fadeOut(animationSpec = tween(durationMillis = 500))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ThemedIconButton(iconResLight = R.drawable.highlight_light,
                    iconResDark = R.drawable.highlight_dark,
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(4.dp, shape = CircleShape)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                    onClick = { onHighlightModeButtonClick() })
            }
        }

        AnimatedVisibility(
            visible = isHighlightMode,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(durationMillis = 500, delayMillis = 50)
            ) + fadeIn(animationSpec = tween(durationMillis = 500, delayMillis = 50)),
            exit = slideOutVertically(
                targetOffsetY = { it }, animationSpec = tween(durationMillis = 500)
            ) + fadeOut(animationSpec = tween(durationMillis = 500))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(horizontal = 16.dp),

                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ThemedIconButton(iconResLight = R.drawable.copy_light,
                    iconResDark = R.drawable.copy_dark,
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(4.dp, shape = CircleShape)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                    onClick = { onCopyButtonClick() })

                ThemedIconButton(iconResLight = R.drawable.select_all_light,
                    iconResDark = R.drawable.select_all_dark,
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(4.dp, shape = CircleShape)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                    onClick = { onSelectAllButtonClick() })

                ThemedIconButton(iconResLight = R.drawable.close_light,
                    iconResDark = R.drawable.close_dark,
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(4.dp, shape = CircleShape)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                    onClick = { onCloseButtonClick() })

                Box(modifier = Modifier
                    .size(60.dp)
                    .shadow(4.dp, shape = CircleShape)
                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                    .clickable(indication = null,
                        interactionSource = remember { MutableInteractionSource() }) {
                        onColorButtonClick()
                    }) {
                    if (currentColor != null) {
                        Icon(
                            painter = painterResource(id = R.drawable.save_color),
                            contentDescription = "Themed Icon",
                            modifier = Modifier.padding(12.dp),
                            tint = currentColor
                        )
                    } else {
                        ThemedIcon(
                            iconResLight = R.drawable.eraser_light,
                            iconResDark = R.drawable.eraser_dark,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                ThemedIconButton(iconResLight = R.drawable.save_light,
                    iconResDark = R.drawable.save_dark,
                    modifier = Modifier
                        .size(60.dp)
                        .shadow(4.dp, shape = CircleShape)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                    onClick = { onSaveButtonClick() })

            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CommentaryIndexedText(
    saveData: Save?,
    verseText: String,
    commentaryText: String?,
    fontSize: Float,
    paragraphSpacing: Float,
    onCommentaryClick: (String) -> Unit
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
    FlowRow(modifier = Modifier) {
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
                            onCommentaryClick(commentaryList?.get(order) ?: "")
                        },
                    )
                )
            } else {
                Text(text = "$word ", style = TextStyle(
                    fontSize = fontSize.sp,
                    lineHeight = paragraphSpacing.sp,
                    letterSpacing = 0.1f.sp,
                ), modifier = Modifier.drawBehind {
                    val textWidth = size.width
                    val textHeight = size.height
                    if (saveData != null) {
                        val c = saveData.color
                        drawLine(
                            color = SAVE_COLORS[c].copy(alpha = 0.5f),
                            start = Offset(0f, textHeight * 0.75f),
                            end = Offset(textWidth, textHeight * 0.75f),
                            strokeWidth = fontSize * 1.5f
                        )
                    }
                })
            }
        }
    }
}

@Composable
fun CommentaryPopUp(isPopupVisible: Boolean, explanation: String) {
    AnimatedVisibility(
        visible = isPopupVisible, enter = slideInVertically(
            initialOffsetY = { it }, animationSpec = tween(durationMillis = 500)
        ) + fadeIn(animationSpec = tween(durationMillis = 500)), exit = slideOutVertically(
            targetOffsetY = { it }, animationSpec = tween(durationMillis = 500)
        ) + fadeOut(animationSpec = tween(durationMillis = 500))
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