package com.lyni.app.adno

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import com.lyni.app.adno.databinding.ActivityMainBinding
import com.lyni.app.lib.core.common.base.ui.BaseActivity
import com.lyni.app.lib.core.common.base.viewmodel.BaseViewModel
import com.lyni.app.lib.core.service.AdnoService
import com.lyni.treasure.ktx.onClick
import com.lyni.treasure.ktx.startService
import com.lyni.treasure.ktx.stopService
import com.lyni.treasure.utils.Log

class MainActivity : BaseActivity<ActivityMainBinding, BaseViewModel>() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun initView() {
        super.initView()
        hideToolbar()
    }

    override fun initListener() {
        super.initListener()
        binding.tvAccess.onClick {
            if (!isAccessibilitySettingsOn(this, AdnoService::class.java.canonicalName)) {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                //跳转设置打开无障碍
            }
        }
        binding.tvStart.onClick {
            startService<AdnoService>()
        }
        binding.tvStop.onClick {
            stopService<AdnoService>()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService<AdnoService>()
    }

    /**
     * 检测辅助功能是否开启
     *
     * @param mContext
     * @return boolean
     */
    private fun isAccessibilitySettingsOn(mContext: Context, serviceName: String): Boolean {
        var accessibilityEnabled = 0
        // 对应的服务
        val service = "$packageName/$serviceName"
        //Log.i(TAG, "service:" + service);
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                mContext.applicationContext.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
            Log.v(TAG, "accessibilityEnabled = $accessibilityEnabled")
        } catch (e: Settings.SettingNotFoundException) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.message)
        }
        val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------")
            val settingValue = Settings.Secure.getString(
                mContext.applicationContext.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
                    Log.v(TAG, "-------------- > accessibilityService :: $accessibilityService $service")
                    if (accessibilityService.equals(service, ignoreCase = true)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!")
                        return true
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***")
        }
        return false
    }
}