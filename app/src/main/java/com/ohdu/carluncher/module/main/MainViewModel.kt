package com.ohdu.carluncher.module.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.AppUtils
import com.ohdu.carluncher.bean.AppInfoBean
import com.ohdu.carluncher.room.CarDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    var appInfo by mutableStateOf<List<AppInfoBean>>(mutableListOf())
    var navApp by mutableStateOf<List<AppInfoBean>>(mutableListOf())

    init {
        queryApps()
        queryNavApp()
    }

    private fun queryNavApp() {
        viewModelScope.launch(Dispatchers.IO) {
            val appInfo = AppUtils.getAppsInfo()
            CarDatabase.getDatabase().carDao().navAppInfo()
                .transform { localData ->
                    emit(localData.filter { localShowData ->
                        localShowData.icon =
                            appInfo.find { it.packageName == localShowData.packageName }?.icon
                        localShowData.navShow
                    })
                }
                .map {
                    val data = it.toMutableList()
                    restructuringNavInfo(data)
                    data
                }
                .map {
                    it.toMutableList().apply {
                        add(0, AppInfoBean())
                        add(it.size, AppInfoBean())
                    }
                }
                .flowOn(Dispatchers.Main)
                .collect {
                    this@MainViewModel.navApp = it
                }
        }
    }


    private fun restructuringNavInfo(appInfoBean: MutableList<AppInfoBean>) {
        if (appInfoBean.size == 3) {
            return
        }
        appInfoBean.add(AppInfoBean())
        restructuringNavInfo(appInfoBean)
    }

    private fun queryApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val appInfo = AppUtils.getAppsInfo()
            CarDatabase.getDatabase().carDao().appInfoToFlow()
                .transform { localData ->
                    emit(localData.map { localShowData ->
                        localShowData.icon =
                            appInfo.find { it.packageName == localShowData.packageName }?.icon
                        localShowData
                    })
                }
                .flowOn(Dispatchers.Main)
                .collect {
                    this@MainViewModel.appInfo = it
                }
        }
    }

    fun updateNavApp(oldAppInfoBean: AppInfoBean?, appInfoBean: AppInfoBean) {
        viewModelScope.launch(Dispatchers.IO) {
            appInfoBean.navShow = true
            if (oldAppInfoBean != null) {
                oldAppInfoBean.navShow = false
                CarDatabase.getDatabase().carDao().updateAppInfo(oldAppInfoBean, appInfoBean)
            } else {
                CarDatabase.getDatabase().carDao().updateAppInfo(appInfoBean)
            }
        }
    }
}