package com.ohdu.carluncher.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ohdu.carluncher.App
import com.ohdu.carluncher.bean.AppInfoBean

@Database(entities = [AppInfoBean::class], version = 1, exportSchema = false)
abstract class CarDatabase : RoomDatabase() {

    abstract fun carDao(): CarRoomDao

    companion object {
        @Volatile
        private var INSTANCE: CarDatabase? = null

        fun getDatabase(): CarDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    App.appContext!!,
                    CarDatabase::class.java,
                    CAR_DATABASE_NAME
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private const val CAR_DATABASE_NAME = "car_database"
    }
}