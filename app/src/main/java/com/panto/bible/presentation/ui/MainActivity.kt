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
import com.panto.bible.data.local.PreferenceManager
import com.panto.bible.data.local.VerseLocalDataSource
import com.panto.bible.presentation.ui.screen.SearchScreen
import com.panto.bible.presentation.ui.screen.SettingsScreen
import com.panto.bible.presentation.ui.screen.SplashScreen
import com.panto.bible.presentation.ui.screen.VerseScreen
import com.panto.bible.presentation.ui.screen.VersionScreen
import com.panto.bible.presentation.ui.viewmodel.MainViewModel
import com.panto.bible.presentation.ui.viewmodel.MainViewModelFactory
import com.panto.bible.presentation.ui.viewmodel.SettingsViewModel
import com.panto.bible.presentation.ui.viewmodel.SettingsViewModelFactory
import com.panto.bible.ui.theme.BibleTheme

class MainActivity : ComponentActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        //  deleteDatabase("han_database.db")
        //  deleteDatabase("han_database.db")
        //  deleteDatabase("default_database.db")
        super.onCreate(savedInstanceState)

        actionBar?.hide()
        // WindowCompat.setDecorFitsSystemWindows(window, false)

        val verseLocalDataSource = VerseLocalDataSource(applicationContext)
        val preferenceManager = PreferenceManager(applicationContext)
        val mainViewModelFactory = MainViewModelFactory(verseLocalDataSource, preferenceManager)
        val settingsViewModelFactory = SettingsViewModelFactory(preferenceManager)

        mainViewModel = viewModels<MainViewModel> { mainViewModelFactory }.value
        settingsViewModel = viewModels<SettingsViewModel> { settingsViewModelFactory }.value

        setContent {
            val themeMode = settingsViewModel.themeMode.collectAsState()
            BibleTheme(themeMode = themeMode.value) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val isLoading by mainViewModel.isLoading.collectAsState()

                    if (isLoading) {
                        SplashScreen(applicationContext)
                    } else {
                        val navController = rememberNavController()
                        NavHost(navController, startDestination = "verseScreen") {
                            composable("verseScreen") {
                                VerseScreen(
                                    mainViewModel,
                                    settingsViewModel,
                                    navController
                                )
                            }
                            composable("settingsScreen") {
                                SettingsScreen(
                                    mainViewModel,
                                    settingsViewModel,
                                    navController
                                )
                            }
                            composable("searchScreen") {
                                SearchScreen(
                                    mainViewModel,
                                    settingsViewModel,
                                    navController
                                )
                            }
                            composable("versionScreen") {
                                VersionScreen(
                                    mainViewModel,
                                    settingsViewModel,
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


