package com.mito.kyoto.data.repository

import com.mito.kyoto.data.local.dao.UserDao
import com.mito.kyoto.data.local.dao.FriendRequestWithUser
import com.mito.kyoto.data.local.entities.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendRepository @Inject constructor(
    private val userDao: UserDao
) {
    private val currentUserId = "user_001"
    
    fun searchUsers(query: String): Flow<List<UserEntity>> = 
        userDao.searchUsers(query)
    
    fun getFriends(): Flow<List<UserEntity>> = 
        userDao.getFriends(currentUserId)
    
    fun getFriendRequests(): Flow<List<FriendRequestWithUser>> = 
        userDao.getPendingRequestsForUser(currentUserId)
    
    suspend fun sendFriendRequest(targetUserId: String, message: String? = null) {
        val request = FriendRequestEntity(
            fromUserId = currentUserId,
            toUserId = targetUserId,
            status = RequestStatus.PENDING,
            message = message
        )
        userDao.insertFriendRequest(request)
    }
    
    suspend fun acceptFriendRequest(requestId: Long) {
        val request = userDao.getRequestById(requestId) ?: return
        if (request.toUserId == currentUserId && request.status == RequestStatus.PENDING) {
            userDao.updateFriendRequest(request.copy(status = RequestStatus.ACCEPTED))
            val friendship1 = FriendshipEntity(userId = request.fromUserId, friendId = request.toUserId)
            val friendship2 = FriendshipEntity(userId = request.toUserId, friendId = request.fromUserId)
            userDao.addFriend(friendship1)
            userDao.addFriend(friendship2)
            ensureUserExists(request.fromUserId, "Friend_${request.fromUserId}")
        }
    }
    
    suspend fun declineFriendRequest(requestId: Long) {
        val request = userDao.getRequestById(requestId) ?: return
        if (request.toUserId == currentUserId) {
            userDao.updateFriendRequest(request.copy(status = RequestStatus.DECLINED))
        }
    }
    
    suspend fun deleteFriend(friendId: String) {
        val friendship1 = FriendshipEntity(userId = currentUserId, friendId = friendId)
        val friendship2 = FriendshipEntity(userId = friendId, friendId = currentUserId)
        userDao.removeFriend(friendship1)
        userDao.removeFriend(friendship2)
    }
    
    private suspend fun ensureUserExists(userId: String, name: String) {
        if (userDao.getUserById(userId) == null) {
            userDao.insertUser(UserEntity(userId = userId, name = name))
        }
    }
    
    suspend fun initializeDemoData() {
        ensureUserExists(currentUserId, "秋山 陽")
        val demoUsers = listOf(
            UserEntity("user_002", "田中 太郎", statusMessage = "営業部"),
            UserEntity("user_003", "佐藤 花子", statusMessage = "開発部"),
            UserEntity("user_004", "鈴木 一郎", statusMessage = "マーケティング"),
            UserEntity("user_005", "高橋 愛", statusMessage = "デザイナー")
        )
        demoUsers.forEach { userDao.insertUser(it) }
        
        if (!userDao.isFriend(currentUserId, "user_002")) {
            userDao.addFriend(FriendshipEntity(currentUserId, "user_002"))
            userDao.addFriend(FriendshipEntity("user_002", currentUserId))
        }
        if (!userDao.isFriend(currentUserId, "user_003")) {
            userDao.addFriend(FriendshipEntity(currentUserId, "user_003"))
            userDao.addFriend(FriendshipEntity("user_003", currentUserId))
        }
        
        val pendingRequest = FriendRequestEntity(
            fromUserId = "user_004",
            toUserId = currentUserId,
            status = RequestStatus.PENDING,
            message = "はじめまして、友達になりたいです！"
        )
        userDao.insertFriendRequest(pendingRequest)
    }
}