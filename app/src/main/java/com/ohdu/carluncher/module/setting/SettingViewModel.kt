package com.ohdu.carluncher.module.setting

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.AppUtils
import com.ohdu.carluncher.bean.AppInfoBean
import com.ohdu.carluncher.room.CarDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class SettingViewModel : ViewModel() {

    val appInfo = mutableStateOf<List<AppInfoBean>>(mutableListOf())


    init {
        queryApps()
    }

    private fun queryApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val appInfo = AppUtils.getAppsInfo()
            CarDatabase.getDatabase().carDao().appInfo()
                .flowOn(Dispatchers.Main)
                .collect {
                    this@SettingViewModel.appInfo.value = it
                }
        }
    }

    fun updateApps(appInfoBean: AppInfoBean) {
        viewModelScope.launch(Dispatchers.IO) {
            val appInfo = AppUtils.getAppsInfo()
            CarDatabase.getDatabase().carDao().updateAppInfo(appInfoBean)
        }
    }
}