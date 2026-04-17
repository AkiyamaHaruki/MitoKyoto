package com.mito.kyoto.ui.friends

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mito.kyoto.R
import com.mito.kyoto.data.local.entities.UserEntity
import com.mito.kyoto.ui.components.FriendListItem

@Composable
fun FriendListScreen(viewModel: FriendsViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val friends by viewModel.friends.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching = searchQuery.isNotBlank()
    
    Column(modifier = Modifier.fillMaxSize()) {
        // 搜索栏
        SearchBar(
            query = searchQuery,
            onQueryChange = viewModel::onSearchQueryChange,
            onSearch = { /* 已通过onQueryChange实时搜索 */ },
            active = isSearching,
            onActiveChange = { if (!it) viewModel.clearSearch() },
            placeholder = { Text(stringResource(R.string.friends_search_hint)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (isSearching) {
                    IconButton(onClick = { viewModel.clearSearch() }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            // 搜索建议（可留空）
        }
        
        // 好友列表
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            if (isSearching) {
                items(searchResults, key = { it.userId }) { user ->
                    FriendListItem(
                        user = user,
                        isFriend = friends.any { it.userId == user.userId },
                        onAddFriend = { viewModel.sendFriendRequest(user.userId) },
                        onDeleteFriend = { viewModel.deleteFriend(user.userId) }
                    )
                }
            } else {
                // 可以添加分组，例如"好友"、"群组"等，此处简化为单一列表
                items(friends, key = { it.userId }) { user ->
                    FriendListItem(
                        user = user,
                        isFriend = true,
                        onAddFriend = { },
                        onDeleteFriend = { viewModel.deleteFriend(user.userId) }
                    )
                }
            }
        }
    }
}