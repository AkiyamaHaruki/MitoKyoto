package com.mito.kyoto.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val name: String,
    val avatarUrl: String? = null,
    val statusMessage: String? = null
)