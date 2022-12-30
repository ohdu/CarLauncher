package com.ohdu.carluncher.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ohdu.carluncher.bean.AppInfoBean
import kotlinx.coroutines.flow.Flow

@Dao
interface CarRoomDao {

    @Query("SELECT * FROM app_info_table ORDER BY isShow DESC")
    fun appInfo(): Flow<List<AppInfoBean>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAppInfo(appInfo: List<AppInfoBean>)

    @Update
    fun updateAppInfo(appInfo: AppInfoBean)
}