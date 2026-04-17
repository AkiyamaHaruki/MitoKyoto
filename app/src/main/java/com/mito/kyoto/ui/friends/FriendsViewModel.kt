package com.mito.kyoto.ui.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// 简单的用户数据类（不需要 Room）
data class SimpleUser(
    val userId: String,
    val name: String,
    val avatarUrl: String? = null,
    val statusMessage: String? = null
)

// 好友申请数据类
data class SimpleFriendRequest(
    val requestId: Long,
    val fromUser: SimpleUser,
    val message: String?
)

class FriendsViewModel : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _selectedTab = MutableStateFlow(FriendsTab.FRIENDS)
    val selectedTab: StateFlow<FriendsTab> = _selectedTab.asStateFlow()

    // 模拟好友列表
    private val _friends = MutableStateFlow<List<SimpleUser>>(emptyList())
    val friends: StateFlow<List<SimpleUser>> = _friends.asStateFlow()

    // 模拟好友申请列表
    private val _friendRequests = MutableStateFlow<List<SimpleFriendRequest>>(emptyList())
    val friendRequests: StateFlow<List<SimpleFriendRequest>> = _friendRequests.asStateFlow()

    // 搜索结果
    val searchResults: StateFlow<List<SimpleUser>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isNotBlank()) {
                // 模拟搜索：在所有模拟用户中过滤
                flowOf(mockAllUsers.filter {
                    it.name.contains(query, ignoreCase = true) ||
                    it.userId.contains(query, ignoreCase = true)
                })
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 模拟所有可搜索的用户
    private val mockAllUsers = listOf(
        SimpleUser("user_002", "田中 太郎", statusMessage = "営業部"),
        SimpleUser("user_003", "佐藤 花子", statusMessage = "開発部"),
        SimpleUser("user_004", "鈴木 一郎", statusMessage = "マーケティング"),
        SimpleUser("user_005", "高橋 愛", statusMessage = "デザイナー"),
        SimpleUser("user_006", "山田 健二", statusMessage = "経理部"),
        SimpleUser("user_007", "伊藤 さくら", statusMessage = "人事部")
    )

    init {
        initializeMockData()
    }

    private fun initializeMockData() {
        // 设置好友列表（当前用户的好友）
        _friends.value = listOf(
            SimpleUser("user_002", "田中 太郎", statusMessage = "営業部"),
            SimpleUser("user_003", "佐藤 花子", statusMessage = "開発部")
        )

        // 设置好友申请列表
        _friendRequests.value = listOf(
            SimpleFriendRequest(
                requestId = 1,
                fromUser = SimpleUser("user_004", "鈴木 一郎", statusMessage = "マーケティング"),
                message = "はじめまして、友達になりたいです！"
            ),
            SimpleFriendRequest(
                requestId = 2,
                fromUser = SimpleUser("user_005", "高橋 愛", statusMessage = "デザイナー"),
                message = "よろしくお願いします"
            )
        )
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _isSearching.value = query.isNotBlank()
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _isSearching.value = false
    }

    fun selectTab(tab: FriendsTab) {
        _selectedTab.value = tab
        clearSearch()
    }

    fun sendFriendRequest(targetUserId: String) {
        // 模拟发送好友申请
        viewModelScope.launch {
            val targetUser = mockAllUsers.find { it.userId == targetUserId } ?: return@launch
            val newRequest = SimpleFriendRequest(
                requestId = System.currentTimeMillis(),
                fromUser = targetUser,
                message = "友達申請が送信されました"
            )
            _friendRequests.value = _friendRequests.value + newRequest
        }
    }

    fun acceptFriendRequest(requestId: Long) {
        viewModelScope.launch {
            val request = _friendRequests.value.find { it.requestId == requestId } ?: return@launch
            // 将申请者添加到好友列表
            _friends.value = _friends.value + request.fromUser
            // 移除该申请
            _friendRequests.value = _friendRequests.value.filter { it.requestId != requestId }
        }
    }

    fun declineFriendRequest(requestId: Long) {
        _friendRequests.value = _friendRequests.value.filter { it.requestId != requestId }
    }

    fun deleteFriend(friendId: String) {
        _friends.value = _friends.value.filter { it.userId != friendId }
    }
}

enum class FriendsTab {
    FRIENDS, REQUESTS
}