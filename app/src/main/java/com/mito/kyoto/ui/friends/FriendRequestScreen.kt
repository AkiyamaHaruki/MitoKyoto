package com.mito.kyoto.ui.friends

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mito.kyoto.R
import com.mito.kyoto.data.local.entities.FriendRequestWithUser
import com.mito.kyoto.ui.components.FriendRequestItem

@Composable
fun FriendRequestScreen(viewModel: FriendsViewModel) {
    val requests by viewModel.friendRequests.collectAsState()
    
    if (requests.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text(stringResource(R.string.friends_no_requests))
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(requests, key = { it.request.requestId }) { request ->
                FriendRequestItem(
                    request = request,
                    onAccept = { viewModel.acceptFriendRequest(it) },
                    onDecline = { viewModel.declineFriendRequest(it) }
                )
            }
        }
    }
}