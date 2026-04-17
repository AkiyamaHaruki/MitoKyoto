package com.mito.kyoto.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "friend_requests",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["fromUserId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["toUserId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FriendRequestEntity(
    @PrimaryKey(autoGenerate = true)
    val requestId: Long = 0,
    val fromUserId: String,
    val toUserId: String,
    val status: RequestStatus,
    val requestTime: Long = System.currentTimeMillis(),
    val message: String? = null
)

enum class RequestStatus {
    PENDING, ACCEPTED, DECLINED
}