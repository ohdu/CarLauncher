package com.ohdu.carluncher

import android.app.Application
import android.content.Context


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }

    companion object {
          var appContext: Context? = null
    }
}