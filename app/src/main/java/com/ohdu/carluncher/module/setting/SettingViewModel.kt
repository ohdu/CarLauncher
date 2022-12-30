package com.ohdu.carluncher.module.setting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.AppUtils
import com.ohdu.carluncher.bean.AppInfoBean
import com.ohdu.carluncher.room.CarDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ItemPosition

class SettingViewModel : ViewModel() {

    var appInfo by mutableStateOf<List<AppInfoBean>>(mutableListOf())


    init {
        queryApps()
    }

    private fun queryApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val appInfo = AppUtils.getAppsInfo()
            this@SettingViewModel.appInfo = CarDatabase.getDatabase().carDao().appInfo()
                .map { localData ->
                    localData.icon =
                        appInfo.find { it.packageName == localData.packageName }?.icon
                    localData
                }
        }
    }

    fun sortApp(fromIndex: ItemPosition, toIndex: ItemPosition) {
        // 1 -> 0
        if (fromIndex.key == toIndex.key) return
        viewModelScope.launch(Dispatchers.IO) {
            appInfo = appInfo.toMutableList().apply {
                add(appInfo.indexOfFirst { it.packageName == toIndex.key }, removeAt(appInfo.indexOfFirst { it.packageName == fromIndex.key }))
            }.mapIndexed { index, appInfoBean ->
                appInfoBean.sortWeight = index
                appInfoBean
            }
            CarDatabase.getDatabase().carDao().updateAppInfo(*appInfo.toTypedArray())
        }
    }

    fun canDragOver(draggedOver: ItemPosition, dragging: ItemPosition) = appInfo.any { it.packageName == draggedOver.key }

    fun updateApps(vararg appInfoBean: AppInfoBean) {
        viewModelScope.launch(Dispatchers.IO) {
            CarDatabase.getDatabase().carDao().updateAppInfo(*appInfoBean)
            queryApps()
        }
    }
}