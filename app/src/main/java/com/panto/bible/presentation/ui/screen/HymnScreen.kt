package com.panto.bible.presentation.ui.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.panto.bible.R
import com.panto.bible.data.model.Hymn
import com.panto.bible.presentation.ui.viewmodel.MainViewModel
import com.panto.bible.ui.ThemedImage
import com.panto.bible.ui.ThemedVectorIconButton
import java.io.InputStream

@Composable
fun HymnScreen(
    mainViewModel: MainViewModel,
    navController: NavHostController
) {
    val searchedHymns by mainViewModel.searchedHymns.collectAsState()
    val context = LocalContext.current

    var query by remember { mutableStateOf("") }

    var isImageShow by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(Unit) {
        mainViewModel.searchHymns("")
    }

    BackHandler {
        if (isImageShow) {
            isImageShow = false
        } else {
            navController.navigate("VerseScreen") {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            HymnAppBar(onBackClick = {
                navController.navigate("VerseScreen") {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                }
            })

            HymnSearchField(onSearchChanged = { q ->
                query = q
                mainViewModel.searchHymns(query = query)
            })

            HorizontalDivider(
                modifier = Modifier
                    .height(2.dp)
                    .background(Color.Gray.copy(alpha = 0.5f)),
            )

            HymnSearchResult(searchedHymns = searchedHymns, onHymnClick = { h ->
                val bitmap = loadHymnBitmapFromAssets(context, h.file)
                if (bitmap != null) {
                    selectedImage = bitmap
                    isImageShow = true
                }
            })

        }

        if (isImageShow && selectedImage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                ImageDialog(bitmap = selectedImage!!, onDismiss = { isImageShow = false })
            }
        }
    }
}

@Composable
fun HymnAppBar(onBackClick: () -> Unit) {
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
                text = "찬송가", style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

fun loadHymnBitmapFromAssets(context: Context, fileName: String): Bitmap? {
    return try {
        val inputStream: InputStream = context.assets.open(fileName)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun HymnSearchField(
    onSearchChanged: (String) -> Unit
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
            ThemedImage(
                iconResLight = R.drawable.search_light,
                iconResDark = R.drawable.search_dark,
                modifier = Modifier
                    .size(48.dp)
                    .padding(6.dp)
            )
            Spacer(Modifier.width(12.dp))
            BasicTextField(
                modifier = Modifier.fillMaxWidth(),
                value = query,
                onValueChange = { newText ->
                    val reduced = newText.replace("\n", "")
                    query = reduced
                    onSearchChanged(reduced)
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
        }
    }
}


@Composable
fun HymnSearchResult(
    searchedHymns: List<Hymn>, onHymnClick: (Hymn) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        if (searchedHymns.isEmpty()) {
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
            searchedHymns.size,
        ) { hIndex ->
            SearchedHymnItem(hymn = searchedHymns[hIndex], onHymnClick = { h ->
                onHymnClick(h)
            })
        }
    }
}

@Composable
fun SearchedHymnItem(
    hymn: Hymn,
    onHymnClick: (Hymn) -> Unit,
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
                onHymnClick(hymn)
            }) {
            Text(
                text = "찬송가 ${hymn.sae}장 " + (if (hymn.tongil != -1) "(통 ${hymn.tongil}장) " else "") + "<${hymn.theme}>",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = hymn.title, style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
fun ImageDialog(bitmap: Bitmap, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable { onDismiss() }, contentAlignment = Alignment.Center
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.8f),
            contentScale = ContentScale.Fit
        )
    }
}
