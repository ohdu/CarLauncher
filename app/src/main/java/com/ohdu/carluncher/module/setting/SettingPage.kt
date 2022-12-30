package com.ohdu.carluncher.module.setting

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.ohdu.carluncher.room.CarDatabase
import com.ohdu.carluncher.util.RouterUtil
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import kotlin.math.log

@Composable
fun SettingPage(navController: NavHostController) {
    LazyColumn {
        item {
            TextButton(
                onClick = { AppUtils.launchApp(RouterUtil.SETTING_PAGE_PACKAGE_NAME) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "系统设置")
                Spacer(modifier = Modifier.weight(1f))
            }

            TextButton(
                onClick = { navController.navigate(RouterUtil.ROUTER_APP_SETTING_PAGE) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "应用设置")
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppSetting(settingViewModel: SettingViewModel = viewModel()) {

    // 拖拽排序
    val state = rememberReorderableLazyListState(
        onMove = settingViewModel::sortApp,
        canDragOver = settingViewModel::canDragOver
    )
    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        state = state.listState,
        modifier = Modifier
            .reorderable(state = state)
            .detectReorderAfterLongPress(state = state)
    ) {
        stickyHeader {
            Row(modifier = Modifier.background(Color.White).padding(12.dp)) {
                Text(text = "可长按拖拽进行排序")
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "是否在主页显示")
            }
        }
        items(settingViewModel.appInfo, { it.packageName }) { appInfo ->
            ReorderableItem(reorderableState = state, key = appInfo.packageName) {
                val elevation = animateDpAsState(if (it) 16.dp else 0.dp)
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(elevation.value)
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
}