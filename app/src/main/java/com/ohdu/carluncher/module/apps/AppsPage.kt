package com.ohdu.carluncher.module.apps

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.blankj.utilcode.util.AppUtils
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import kotlin.math.ceil


@OptIn(ExperimentalPagerApi::class)
@Composable
fun AppListPage(appViewModel: AppsViewModel = viewModel()) {
    // 获取本机安装的App
    val phoneApps by appViewModel.appInfo
    // 一页最多8个
    val maxSizePage = 8.0
    HorizontalPager(
        count = ceil(phoneApps.size / maxSizePage).toInt()
    ) {
        // 每页的apps
        val pageApps = phoneApps.filterIndexed { index, _ ->
            index < (maxSizePage * it.plus(1)) && index >= (maxSizePage * it)
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(8.dp),
            userScrollEnabled = false,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(pageApps) { appInfo ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            // 打开app
                            AppUtils.launchApp(appInfo.packageName)
                        }
                ) {
                    Spacer(modifier = Modifier.height(10.dp))
                    AsyncImage(
                        model = appInfo.icon,
                        contentDescription = appInfo.name,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = appInfo.name,
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(22.dp))
                }
            }
        }
    }

}

