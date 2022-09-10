package com.lyni.app.adno

import android.app.Application
import com.lyni.treasure.utils.Utils

/**
 * @author Liangyong Ni
 * @date 2022/9/11
 * description [App]
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.openDebug()
    }
}