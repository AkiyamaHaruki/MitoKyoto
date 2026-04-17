package com.mito.kyoto.ui.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mito.kyoto.data.local.entities.UserEntity
import com.mito.kyoto.data.local.dao.FriendRequestWithUser
import com.mito.kyoto.data.repository.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val repository: FriendRepository
) : ViewModel() {
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()
    
    private val _selectedTab = MutableStateFlow(FriendsTab.FRIENDS)
    val selectedTab: StateFlow<FriendsTab> = _selectedTab.asStateFlow()
    
    val friends: StateFlow<List<UserEntity>> = repository.getFriends()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val friendRequests: StateFlow<List<FriendRequestWithUser>> = repository.getFriendRequests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val searchResults: StateFlow<List<UserEntity>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isNotBlank()) {
                repository.searchUsers(query)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
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
    
    fun sendFriendRequest(targetUserId: String, message: String? = null) {
        viewModelScope.launch {
            repository.sendFriendRequest(targetUserId, message)
        }
    }
    
    fun acceptFriendRequest(requestId: Long) {
        viewModelScope.launch {
            repository.acceptFriendRequest(requestId)
        }
    }
    
    fun declineFriendRequest(requestId: Long) {
        viewModelScope.launch {
            repository.declineFriendRequest(requestId)
        }
    }
    
    fun deleteFriend(friendId: String) {
        viewModelScope.launch {
            repository.deleteFriend(friendId)
        }
    }
    
    fun initializeDemoData() {
        viewModelScope.launch {
            repository.initializeDemoData()
        }
    }
}

enum class FriendsTab {
    FRIENDS, REQUESTS
}