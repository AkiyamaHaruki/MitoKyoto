package com.mito.kyoto.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mito.kyoto.data.local.entities.UserEntity

@Composable
fun FriendListItem(
    user: UserEntity,
    isFriend: Boolean,
    onAddFriend: () -> Unit,
    onDeleteFriend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { /* 跳转到用户详情 */ }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 头像
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.name.take(1),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 20.sp
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 名称和状态
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.name,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
            if (!user.statusMessage.isNullOrEmpty()) {
                Text(
                    text = user.statusMessage,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // 操作按钮
        if (!isFriend) {
            IconButton(onClick = onAddFriend) {
                Icon(
                    Icons.Default.PersonAdd,
                    contentDescription = "Add Friend",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
    Divider(modifier = Modifier.padding(horizontal = 16.dp))
}