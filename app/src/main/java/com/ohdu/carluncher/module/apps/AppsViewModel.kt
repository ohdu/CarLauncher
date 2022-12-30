package com.ohdu.carluncher.module.apps

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.AppUtils
import com.ohdu.carluncher.bean.AppInfoBean
import com.ohdu.carluncher.room.CarDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

class AppsViewModel : ViewModel() {

    val appInfo = mutableStateOf<List<AppInfoBean>>(mutableListOf())


    init {
        queryApps()
    }

    private fun insertApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val appInfo = AppUtils.getAppsInfo()
            // 将应用列表进行排序  系统应用放到最下面
            appInfo.sortBy { it.isSystem }
            val daoAppInfoBean = mutableListOf<AppInfoBean>()
            appInfo.forEachIndexed { index, it ->
                daoAppInfoBean.add(
                    AppInfoBean(
                        it.packageName,
                        it.name,
                        it.icon,
                        it.packagePath,
                        it.versionName,
                        it.versionCode,
                        it.minSdkVersion,
                        it.targetSdkVersion,
                        it.isSystem,
                        sortWeight = index
                    )
                )
            }
            CarDatabase.getDatabase().carDao().insertAppInfo(daoAppInfoBean)
            queryApps()
        }
    }

    private fun queryApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val appInfo = AppUtils.getAppsInfo()
            CarDatabase.getDatabase().carDao().appInfoToFlow()
                .transform { localData ->
                    emit(localData.filter { localShowData ->
                        localShowData.icon =
                            appInfo.find { it.packageName == localShowData.packageName }?.icon
                        localShowData.isShow
                    })
                }
                .flowOn(Dispatchers.Main)
                .collect {
                    if (it.isEmpty()) {
                        insertApps()
                    } else {
                        this@AppsViewModel.appInfo.value = it
                    }
                }
        }
    }
}