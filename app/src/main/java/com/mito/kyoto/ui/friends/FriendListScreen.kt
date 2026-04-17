package com.mito.kyoto.ui.friends

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mito.kyoto.R
import com.mito.kyoto.data.local.entities.UserEntity
import com.mito.kyoto.ui.components.FriendListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendListScreen(viewModel: FriendsViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val friends by viewModel.friends.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching = searchQuery.isNotBlank()
    
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text(stringResource(R.string.friends_search_hint)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { viewModel.clearSearch() }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true
        )
        
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