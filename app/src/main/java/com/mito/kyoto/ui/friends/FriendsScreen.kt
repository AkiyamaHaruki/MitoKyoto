package com.mito.kyoto.ui.friends

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mito.kyoto.R
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    viewModel: FriendsViewModel = hiltViewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // 捕获初始化过程中的异常
    LaunchedEffect(Unit) {
        try {
            viewModel.initializeDemoData()
        } catch (e: Exception) {
            hasError = true
            errorMessage = e.message ?: "Unknown error"
            logCrash(e)
        }
    }

    if (hasError) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("エラーが発生しました", color = MaterialTheme.colorScheme.error)
                Text(errorMessage, fontSize = androidx.compose.ui.unit.TextUnit.Unspecified)
                Button(onClick = { /* 可尝试重新加载 */ }) {
                    Text("再試行")
                }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(R.string.friends_title)) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    actions = {
                        if (!isSearching) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                            IconButton(onClick = { /* TODO */ }) {
                                Icon(Icons.Default.Add, contentDescription = "Add Friend")
                            }
                        }
                    }
                )

                TabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = selectedTab == FriendsTab.FRIENDS,
                        onClick = { viewModel.selectTab(FriendsTab.FRIENDS) },
                        text = { Text(stringResource(R.string.friends_tab_friends)) }
                    )
                    Tab(
                        selected = selectedTab == FriendsTab.REQUESTS,
                        onClick = { viewModel.selectTab(FriendsTab.REQUESTS) },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(stringResource(R.string.friends_tab_requests))
                                val requestCount = viewModel.friendRequests.value.size
                                if (requestCount > 0) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Badge { Text("$requestCount") }
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                FriendsTab.FRIENDS -> FriendListScreen(viewModel)
                FriendsTab.REQUESTS -> FriendRequestScreen(viewModel)
            }
        }
    }
}

private fun logCrash(throwable: Throwable) {
    try {
        val logFile = File("/data/data/com.mito.kyoto/files/crash_log.txt")
        logFile.parentFile?.mkdirs()
        val fos = FileOutputStream(logFile, true)
        val pw = PrintWriter(fos)
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        pw.println("========== ${sdf.format(Date())} ==========")
        throwable.printStackTrace(pw)
        pw.close()
        Log.e("FriendsScreen", "Crash", throwable)
    } catch (_: Exception) {
    }
}