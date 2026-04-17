package com.mito.kyoto.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "friendships",
    primaryKeys = ["userId", "friendId"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["friendId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FriendshipEntity(
    val userId: String,
    val friendId: String,
    val createdAt: Long = System.currentTimeMillis()
)