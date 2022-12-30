package com.ohdu.carluncher.module.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.blankj.utilcode.util.AppUtils
import com.ohdu.carluncher.util.RouterUtil

@Composable
fun SettingPage(navController: NavHostController) {
    LazyColumn {
        item {
            IconButton(onClick = { navController.navigate(RouterUtil.ROUTER_APP_SETTING_PAGE) }) {
                Row {
                    Text(text = "应用设置")
                }
            }
        }
    }
}


@Composable
fun AppSetting(settingViewModel: SettingViewModel = viewModel()) {
    // 获取本机安装的App
    val phoneApps by settingViewModel.appInfo

    LazyColumn(contentPadding = PaddingValues(12.dp)) {
        items(phoneApps) { appInfo ->
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp)
                    .clickable {
                        // 打开app
                        AppUtils.launchApp(appInfo.packageName)
                    }
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                AsyncImage(
                    model = appInfo.icon,
                    contentDescription = appInfo.name,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = appInfo.name,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.weight(1f))
                Checkbox(checked = appInfo.isShow, onCheckedChange = {
                    settingViewModel.updateApps(appInfo.copy(isShow = it))
                })
            }
        }
    }
}