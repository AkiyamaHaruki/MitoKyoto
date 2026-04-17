package com.mito.kyoto.ui.friends

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.mito.kyoto.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    viewModel: FriendsViewModel = hiltViewModel()
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.initializeDemoData()
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
                            IconButton(onClick = { /* TODO: 添加好友 */ }) {
                                Icon(Icons.Default.Add, contentDescription = "Add Friend")
                            }
                        }
                    }
                )
                
                // Tab 切换
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
                            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
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