package com.panto.bible.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.panto.bible.data.local.LocalDataSource
import com.panto.bible.data.local.PreferenceManager
import com.panto.bible.presentation.ui.screen.DictionaryScreen
import com.panto.bible.presentation.ui.screen.HymnScreen
import com.panto.bible.presentation.ui.screen.SaveScreen
import com.panto.bible.presentation.ui.screen.SearchScreen
import com.panto.bible.presentation.ui.screen.SettingsScreen
import com.panto.bible.presentation.ui.screen.SplashScreen
import com.panto.bible.presentation.ui.screen.VerseScreen
import com.panto.bible.presentation.ui.screen.VersionScreen
import com.panto.bible.presentation.ui.screen.WebViewScreen
import com.panto.bible.presentation.ui.viewmodel.DictionaryViewModel
import com.panto.bible.presentation.ui.viewmodel.MainViewModel
import com.panto.bible.presentation.ui.viewmodel.MainViewModelFactory
import com.panto.bible.presentation.ui.viewmodel.SettingsViewModel
import com.panto.bible.presentation.ui.viewmodel.SettingsViewModelFactory
import com.panto.bible.ui.theme.BibleTheme
import com.panto.bible.ui.theme.DarkThemeProvider
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private val dictionaryViewModel: DictionaryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        //  deleteDatabase("han_database.db")
        //  deleteDatabase("han_database.db")
        //  deleteDatabase("default_database.db")
        super.onCreate(savedInstanceState)

        actionBar?.hide()
        // WindowCompat.setDecorFitsSystemWindows(window, false)

        val verseLocalDataSource = LocalDataSource(applicationContext)
        val preferenceManager = PreferenceManager(applicationContext)
        val mainViewModelFactory = MainViewModelFactory(verseLocalDataSource, preferenceManager)
        val settingsViewModelFactory = SettingsViewModelFactory(preferenceManager)

        mainViewModel = viewModels<MainViewModel> { mainViewModelFactory }.value
        settingsViewModel = viewModels<SettingsViewModel> { settingsViewModelFactory }.value

        setContent {
            val themeMode by settingsViewModel.themeMode.collectAsState()

            DarkThemeProvider(isDarkTheme = themeMode == 1) {

                BibleTheme(themeMode = themeMode) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val isLoading by mainViewModel.isLoading.collectAsState()

                        if (isLoading) {
                            SplashScreen(applicationContext)
                        } else {
                            val navController = rememberNavController()
                            NavHost(navController, startDestination = "VerseScreen") {
                                composable("VerseScreen") {
                                    VerseScreen(
                                        mainViewModel,
                                        settingsViewModel,
                                        navController
                                    )
                                }
                                composable("SettingsScreen") {
                                    SettingsScreen(
                                        settingsViewModel,
                                        navController
                                    )
                                }
                                composable("SearchScreen") {
                                    SearchScreen(
                                        mainViewModel,
                                        navController
                                    )
                                }
                                composable("VersionScreen") {
                                    VersionScreen(
                                        mainViewModel,
                                        navController
                                    )
                                }
                                composable("HymnScreen") {
                                    HymnScreen(
                                        mainViewModel,
                                        navController
                                    )
                                }
                                composable("DictionaryScreen") {
                                    DictionaryScreen(
                                        dictionaryViewModel,
                                        navController
                                    )
                                }
                                composable("webViewScreen/{url}") { backStackEntry ->
                                    val encodedUrl =
                                        backStackEntry.arguments?.getString("url") ?: ""
                                    val decodedUrl =
                                        URLDecoder.decode(
                                            encodedUrl,
                                            StandardCharsets.UTF_8.toString()
                                        )
                                    WebViewScreen(decodedUrl, navController)
                                }
                                composable("SaveScreen") {
                                    SaveScreen(
                                        mainViewModel,
                                        navController
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


