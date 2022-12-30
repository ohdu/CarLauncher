package com.ohdu.carluncher

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.blankj.utilcode.util.LogUtils
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ohdu.carluncher.module.apps.AppListPage
import com.ohdu.carluncher.module.setting.AppSetting
import com.ohdu.carluncher.module.setting.SettingPage
import com.ohdu.carluncher.ui.theme.CarLuncherTheme
import com.ohdu.carluncher.util.RouterUtil

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
    val icons = listOf(Icons.Filled.Home, Icons.Filled.Settings)
    val items = listOf("home", "设置")
    NavigationRail(header = {

    }) {
        items.forEachIndexed { index, item ->
            NavigationRailItem(
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item) },
                selected = false,//currentDestination?.route?.let { it == item } ?: false,
                onClick = {
                    if (index == 0) {
                        navController.navigate(RouterUtil.ROUTER_HOME_PAGE)
                    } else {
                        navController.navigate(RouterUtil.ROUTER_SETTING_PAGE)
                    }
                }
            )
        }
    }
}
