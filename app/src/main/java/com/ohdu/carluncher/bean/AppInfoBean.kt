package com.ohdu.carluncher.bean

import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "app_info_table")
data class AppInfoBean(
    @PrimaryKey
    var packageName: String = "",
    var name: String = "",
    @Ignore
    var icon: Drawable? = null,
    var packagePath: String = "",
    var versionName: String = "",
    var versionCode: Int = 0,
    var minSdkVersion: Int = 0,
    var targetSdkVersion: Int = 0,
    var isSystem: Boolean = true,
    var isShow: Boolean = !isSystem,
    // 排序字段 数字越大 权重越高
    var sortWeight: Int = 0,
    // 是否显示在侧边导航栏
    var navShow:Boolean = false
)