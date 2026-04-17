package com.mito.kyoto.ui.profile

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.background
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
import com.mito.kyoto.MainActivity
import com.mito.kyoto.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val currentLanguageCode = remember {
        context.getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE)
            .getString("app_language", "ja") ?: "ja"
    }
    val currentLanguageDisplay = when (currentLanguageCode) {
        "ja" -> "日本語"
        "zh-TW" -> "台灣中國語"
        "en" -> "English"
        else -> "日本語"
    }

    val comingSoonText = stringResource(R.string.profile_coming_soon)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.profile_title)) },
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
            item {
                UserProfileCard(
                    userName = "秋山 陽",
                    userPosition = stringResource(R.string.profile_user_position),
                    companyName = stringResource(R.string.profile_company_full)
                )
            }

            item {
                SectionHeader(title = stringResource(R.string.profile_content_section))
            }
            item {
                MenuItem(
                    icon = Icons.Default.Favorite,
                    title = stringResource(R.string.profile_favorites),
                    onClick = {
                        Toast.makeText(context, comingSoonText, Toast.LENGTH_SHORT).show()
                    }
                )
            }

            item {
                SectionHeader(title = stringResource(R.string.profile_settings_section))
            }
            item {
                MenuItem(
                    icon = Icons.Default.Language,
                    title = stringResource(R.string.profile_language),
                    trailingText = currentLanguageDisplay,
                    onClick = { showLanguageDialog = true }
                )
            }
            item {
                MenuItem(
                    icon = Icons.Default.Info,
                    title = stringResource(R.string.profile_about),
                    onClick = { showAboutDialog = true }
                )
            }
        }
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = currentLanguageCode,
            onLanguageSelected = { code ->
                (context as? MainActivity)?.let { activity ->
                    MainActivity.saveLanguage(activity, code)
                    Toast.makeText(context, "言語設定を保存しました。\n2秒後にアプリを終了します。", Toast.LENGTH_LONG).show()
                    showLanguageDialog = false
                    // 延迟2秒后退出应用
                    Handler(Looper.getMainLooper()).postDelayed({
                        activity.finishAffinity()
                    }, 2000)
                }
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

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
    companyName: String
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
                    text = companyName,
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
        title = { Text(stringResource(R.string.language_selection)) },
        text = {
            Column {
                listOf(
                    "ja" to stringResource(R.string.language_japanese),
                    "zh-TW" to stringResource(R.string.language_chinese_tw),
                    "en" to stringResource(R.string.language_english)
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
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.about_title)) },
        text = {
            Column {
                Text(stringResource(R.string.profile_company_full), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.about_description))
                Spacer(modifier = Modifier.height(4.dp))
                Text(stringResource(R.string.about_version))
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(R.string.about_copyright))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}