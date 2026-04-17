package com.mito.kyoto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mito.kyoto.data.repository.SettingsRepository
import com.mito.kyoto.ui.community.CommunityScreen
import com.mito.kyoto.ui.friends.FriendsScreen
import com.mito.kyoto.ui.home.HomeScreen
import com.mito.kyoto.ui.profile.ProfileScreen
import com.mito.kyoto.ui.theme.MitoKyotoTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    
    private val settingsRepo by lazy { SettingsRepository(this) }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化语言（应用保存的设置）
        lifecycleScope.launch {
            settingsRepo.initLanguage()
        }

        setContent {
            // 使用一个递增的 key 来强制重组整个 Compose 树
            var refreshKey by remember { mutableIntStateOf(0) }
            
            // 监听语言变化，每次语言切换后 refreshKey 加 1，触发完全重组
            LaunchedEffect(Unit) {
                settingsRepo.getLanguageFlow().collectLatest {
                    refreshKey++
                }
            }
            
            // key 变化时，整个 MitoKyotoTheme 和 MainApp 会重新创建，从而刷新所有 stringResource
            key(refreshKey) {
                MitoKyotoTheme {
                    MainApp()
                }
            }
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