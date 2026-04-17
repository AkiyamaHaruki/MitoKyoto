package com.mito.kyoto.data.local.dao

import androidx.room.*
import com.mito.kyoto.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // 用户操作
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE name LIKE '%' || :query || '%' OR userId LIKE '%' || :query || '%'")
    fun searchUsers(query: String): Flow<List<UserEntity>>

    // 好友关系操作
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFriend(friendship: FriendshipEntity)
    
    @Delete
    suspend fun removeFriend(friendship: FriendshipEntity)
    
    @Transaction
    @Query("SELECT * FROM users WHERE userId IN (SELECT friendId FROM friendships WHERE userId = :currentUserId)")
    fun getFriends(currentUserId: String): Flow<List<UserEntity>>
    
    @Query("SELECT EXISTS(SELECT 1 FROM friendships WHERE userId = :userId AND friendId = :friendId)")
    suspend fun isFriend(userId: String, friendId: String): Boolean

    // 好友申请操作
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendRequest(request: FriendRequestEntity)
    
    @Update
    suspend fun updateFriendRequest(request: FriendRequestEntity)
    
    @Transaction
    @Query("""
        SELECT * FROM friend_requests 
        WHERE toUserId = :userId AND status = 'PENDING'
        ORDER BY requestTime DESC
    """)
    fun getPendingRequestsForUser(userId: String): Flow<List<FriendRequestWithUser>>
    
    @Query("SELECT * FROM friend_requests WHERE requestId = :requestId")
    suspend fun getRequestById(requestId: Long): FriendRequestEntity?
}

// 用于获取带用户信息的好友申请
data class FriendRequestWithUser(
    @Embedded val request: FriendRequestEntity,
    @Relation(
        parentColumn = "fromUserId",
        entityColumn = "userId"
    )
    val fromUser: UserEntity
)