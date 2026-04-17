package com.mito.kyoto.ui.profile

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mito.kyoto.R
import com.mito.kyoto.ui.theme.LocaleManager
import com.mito.kyoto.ui.theme.getLanguageDisplayName
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(context.applicationContext as android.app.Application)
    )
    val currentLanguage by viewModel.currentLanguage.collectAsState()

    val scope = rememberCoroutineScope()
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("マイページ") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // 用户信息卡片
            item {
                UserProfileCard(
                    userName = viewModel.userName,
                    userPosition = viewModel.userPosition,
                    avatarUrl = viewModel.userAvatarUrl
                )
            }

            // 功能菜单分组：收藏
            item {
                SectionHeader(title = "コンテンツ")
            }
            item {
                MenuItem(
                    icon = Icons.Default.Favorite,
                    title = "お気に入り",
                    onClick = {
                        // TODO: 跳转收藏列表
                        Toast.makeText(context, "近日公開予定", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // 功能菜单分组：设置
            item {
                SectionHeader(title = "設定")
            }
            item {
                MenuItem(
                    icon = Icons.Default.Language,
                    title = "言語",
                    trailingText = getLanguageDisplayName(currentLanguage),
                    onClick = { showLanguageDialog = true }
                )
            }
            item {
                MenuItem(
                    icon = Icons.Default.Info,
                    title = "このアプリについて",
                    onClick = { showAboutDialog = true }
                )
            }
        }
    }

    // 语言选择对话框
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = currentLanguage,
            onLanguageSelected = { code ->
                scope.launch {
                    viewModel.updateLanguage(code)
                    // 重启 Activity 使语言生效（简单处理）
                    context.applicationContext.let { appCtx ->
                        LocaleManager.applyLanguage(appCtx, code)
                    }
                    // 实际项目中建议使用 recreate() 或重启整个任务栈，这里用 Toast 提示
                    Toast.makeText(context, "言語設定を変更しました。アプリを再起動すると反映されます。", Toast.LENGTH_LONG).show()
                }
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    // 关于对话框
    if (showAboutDialog) {
        AboutDialog(
            onDismiss = { showAboutDialog = false }
        )
    }
}

@Composable
fun UserProfileCard(
    userName: String,
    userPosition: String,
    avatarUrl: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 头像占位
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.take(1),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = userName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = userPosition,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "日本京都水戸津科技株式会社",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        fontSize = 14.sp,
        color = Color.Gray,
        fontWeight = FontWeight.Medium
    )
}

@Composable
fun MenuItem(
    icon: ImageVector,
    title: String,
    trailingText: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            if (trailingText != null) {
                Text(
                    text = trailingText,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.Gray
                )
            } else {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
fun LanguageSelectionDialog(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("言語選択") },
        text = {
            Column {
                listOf(
                    LocaleManager.LANG_JA to "日本語",
                    LocaleManager.LANG_ZH_TW to "台灣中國語",
                    LocaleManager.LANG_EN to "English"
                ).forEach { (code, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(code) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentLanguage == code,
                            onClick = { onLanguageSelected(code) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = name, fontSize = 16.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("キャンセル")
            }
        }
    )
}

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("このアプリについて") },
        text = {
            Column {
                Text("日本京都水戸津科技株式会社", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("公式コミュニケーションアプリ")
                Spacer(modifier = Modifier.height(4.dp))
                Text("バージョン 1.0.0")
                Spacer(modifier = Modifier.height(16.dp))
                Text("© 2026 Mito Kyoto Technology")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("閉じる")
            }
        }
    )
}

// 手动 ViewModel 工厂（因使用了 AndroidViewModel）
class ProfileViewModelFactory(private val application: Application) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}