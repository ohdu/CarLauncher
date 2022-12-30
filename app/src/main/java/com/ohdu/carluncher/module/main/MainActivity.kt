package com.ohdu.carluncher.module.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.LogUtils
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ohdu.carluncher.bean.AppInfoBean
import com.ohdu.carluncher.module.apps.AppListPage
import com.ohdu.carluncher.module.setting.AppSetting
import com.ohdu.carluncher.module.setting.SettingPage
import com.ohdu.carluncher.ui.theme.CarLuncherTheme
import com.ohdu.carluncher.util.RouterUtil
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
//            rememberSystemUiController().setStatusBarColor(Color.Transparent, false)
            CarLuncherTheme {
                val controller = rememberAnimatedNavController()
                Scaffold(bottomBar = {
                    QuickNav(controller)
                }) {
                    AnimatedNavHost(
                        navController = controller,
                        startDestination = RouterUtil.ROUTER_HOME_PAGE,
                        modifier = Modifier
                            // NavigationRail的宽度
                            .padding(start = 80.dp)
                            .fillMaxSize()
                    ) {
                        composable(RouterUtil.ROUTER_HOME_PAGE) {
                            AppListPage()
                        }
                        composable(RouterUtil.ROUTER_SETTING_PAGE) {
                            SettingPage(controller)
                        }
                        composable(RouterUtil.ROUTER_APP_SETTING_PAGE) {
                            AppSetting()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickNav(navController: NavHostController) {
    val mainViewModel: MainViewModel = viewModel()

    var oldAppInfoNavIndex = -1
    var showDialog by remember { mutableStateOf(false) }
    ChooseAppDialog(mainViewModel, showDialog = showDialog, onDismiss = {
        showDialog = false
    }, onClick = {
        mainViewModel.updateNavApp(
            if (oldAppInfoNavIndex == -1) null else mainViewModel.navApp[oldAppInfoNavIndex],
            it
        )
        showDialog = false
    })
    NavigationRail(header = {

    }) {
        mainViewModel.navApp.forEachIndexed { index, item ->
            NavigationRailItem(
                icon = {
                    if (index == 0 && item.packageName.isBlank()) {
                        Icon(
                            Icons.Filled.Home,
                            contentDescription = "",
                            modifier = Modifier.size(40.dp)
                        )
                    } else if (index == mainViewModel.navApp.size - 1 && item.packageName.isBlank()) {
                        Icon(
                            Icons.Filled.Settings,
                            contentDescription = "",
                            modifier = Modifier.size(40.dp)
                        )
                    } else if (item.packageName.isBlank()) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "",
                            modifier = Modifier.size(40.dp)
                        )
                    } else {
                        AsyncImage(
                            model = item.icon,
                            contentDescription = "",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                },
                label = { },
                selected = false,//currentDestination?.route?.let { it == item } ?: false,
                onClick = {
                    if (index == 0) {
                        navController.navigate(RouterUtil.ROUTER_HOME_PAGE)
                    } else if (index == 1) {
                        oldAppInfoNavIndex = index - 1
                        showDialog = true
                    } else {
                        navController.navigate(RouterUtil.ROUTER_SETTING_PAGE)
                    }
                }
            )
        }
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChooseAppDialog(
    mainViewModel: MainViewModel = viewModel(),
    showDialog: Boolean,
    onClick: (AppInfoBean) -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = { onDismiss.invoke() },
            // usePlatformDefaultWidth 是否限制dialog宽度
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(600.dp)
                    .background(Color.White)
            ) {
                items(mainViewModel.appInfo) { appInfo ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                // 打开app
                                onClick.invoke(appInfo)
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
}
