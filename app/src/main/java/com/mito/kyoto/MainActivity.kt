package com.mito.kyoto

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mito.kyoto.ui.community.CommunityScreen
import com.mito.kyoto.ui.friends.FriendsScreen
import com.mito.kyoto.ui.home.HomeScreen
import com.mito.kyoto.ui.profile.ProfileScreen
import com.mito.kyoto.ui.theme.MitoKyotoTheme

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        // 在最早阶段应用已保存的语言，确保 Configuration 正确
        val prefs = newBase.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val languageCode = prefs.getString("app_language", "ja") ?: "ja"
        val localeList = when (languageCode) {
            "ja" -> LocaleListCompat.forLanguageTags("ja")
            "zh-TW" -> LocaleListCompat.forLanguageTags("zh-TW")
            "en" -> LocaleListCompat.forLanguageTags("en")
            else -> LocaleListCompat.forLanguageTags("ja")
        }
        AppCompatDelegate.setApplicationLocales(localeList)
        super.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MitoKyotoTheme {
                MainApp()
            }
        }
    }

    companion object {
        fun saveLanguageAndRecreate(activity: MainActivity, languageCode: String) {
            val prefs = activity.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            prefs.edit().putString("app_language", languageCode).apply()
            // 立即重启 Activity，由于 attachBaseContext 已处理新语言，重启后即刻生效
            activity.recreate()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                val items = listOf(
                    NavigationItem(stringResource(R.string.nav_home), "home", Icons.Default.Home),
                    NavigationItem(stringResource(R.string.nav_community), "community", Icons.Default.Public),
                    NavigationItem(stringResource(R.string.nav_friends), "friends", Icons.Default.People),
                    NavigationItem(stringResource(R.string.nav_profile), "profile", Icons.Default.Person)
                )
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen() }
            composable("community") { CommunityScreen() }
            composable("friends") { FriendsScreen() }
            composable("profile") { ProfileScreen() }
        }
    }
}

data class NavigationItem(val label: String, val route: String, val icon: ImageVector)