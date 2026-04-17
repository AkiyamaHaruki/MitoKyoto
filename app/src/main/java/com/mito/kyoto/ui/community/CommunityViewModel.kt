package com.mito.kyoto.ui.community

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class BannerItem(
    val id: Int,
    val imageUrl: String,
    val title: String
)

data class Post(
    val id: String,
    val userName: String,
    val userAvatar: String?,
    val content: String,
    val images: List<String>,
    val likeCount: Int,
    val commentCount: Int,
    val timestamp: Long
)

class CommunityViewModel : ViewModel() {

    private val _banners = MutableStateFlow<List<BannerItem>>(emptyList())
    val banners: StateFlow<List<BannerItem>> = _banners.asStateFlow()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _selectedCategory = MutableStateFlow("推荐")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        // 模拟轮播图
        _banners.value = listOf(
            BannerItem(1, "https://picsum.photos/400/200?random=1", "京都水戸津科技 新製品発表"),
            BannerItem(2, "https://picsum.photos/400/200?random=2", "社内イベント開催のお知らせ"),
            BannerItem(3, "https://picsum.photos/400/200?random=3", "エンジニア交流会 参加者募集中")
        )

        // 模拟帖子数据
        _posts.value = listOf(
            Post(
                id = "1",
                userName = "秋山 陽",
                userAvatar = null,
                content = "新しいプロジェクトが始まりました！チームの皆さん、よろしくお願いします。",
                images = listOf("https://picsum.photos/300/300?random=10"),
                likeCount = 124,
                commentCount = 18,
                timestamp = System.currentTimeMillis() - 3600000
            ),
            Post(
                id = "2",
                userName = "田中 太郎",
                userAvatar = null,
                content = "週末のハイキング、最高でした！🗻",
                images = listOf(
                    "https://picsum.photos/300/300?random=11",
                    "https://picsum.photos/300/300?random=12"
                ),
                likeCount = 89,
                commentCount = 7,
                timestamp = System.currentTimeMillis() - 7200000
            ),
            Post(
                id = "3",
                userName = "佐藤 花子",
                userAvatar = null,
                content = "おすすめのカフェ見つけた☕️ 落ち着く空間で作業がはかどります。",
                images = listOf("https://picsum.photos/300/300?random=13"),
                likeCount = 256,
                commentCount = 32,
                timestamp = System.currentTimeMillis() - 86400000
            ),
            Post(
                id = "4",
                userName = "鈴木 一郎",
                userAvatar = null,
                content = "マーケティング部の新メンバー募集中！興味ある方はDMください。",
                images = emptyList(),
                likeCount = 45,
                commentCount = 12,
                timestamp = System.currentTimeMillis() - 172800000
            ),
            Post(
                id = "5",
                userName = "高橋 愛",
                userAvatar = null,
                content = "デザインの参考になるサイトをまとめました。後でブログに書きます。",
                images = listOf(
                    "https://picsum.photos/300/300?random=14",
                    "https://picsum.photos/300/300?random=15",
                    "https://picsum.photos/300/300?random=16"
                ),
                likeCount = 312,
                commentCount = 28,
                timestamp = System.currentTimeMillis() - 259200000
            )
        )
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
        // 模拟切换分类时刷新数据（可替换为实际请求）
        _isLoading.value = true
        // 模拟网络延迟
        kotlinx.coroutines.GlobalScope.launch {
            kotlinx.coroutines.delay(500)
            loadMockData()
            _isLoading.value = false
        }
    }

    fun likePost(postId: String) {
        _posts.value = _posts.value.map { post ->
            if (post.id == postId) {
                post.copy(likeCount = post.likeCount + 1)
            } else {
                post
            }
        }
    }
}