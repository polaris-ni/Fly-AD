package com.lyni.app.lib.core.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.lyni.treasure.utils.Log

/**
 * @author Liangyong Ni
 * @date 2022/9/11
 * description [AdnoService]
 */
class AdnoService : AccessibilityService() {

    companion object {
        private const val TAG = "AdnoService"
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        val config = AccessibilityServiceInfo()
        //配置监听的事件类型为界面变化|点击事件
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_VIEW_CLICKED
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
//        if (Build.VERSION.SDK_INT >= 16) {
        config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS
//        }
        serviceInfo = config
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val nodeInfo = event.source //当前界面的可访问节点信息
        if (nodeInfo != null && event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) { //界面变化事件
            val activityInfo = rootInActiveWindow

//            val componentName = ComponentName(event.packageName.toString(), event.className.toString())
//            val activityInfo = tryGetActivity(componentName)
            val isActivity = activityInfo != null
            Log.e(TAG, "onAccessibilityEvent: ${nodeInfo.packageName} $isActivity")
            if (isActivity) {
                val nodeInfoList = nodeInfo.findAccessibilityNodeInfosByText("跳过")
                Log.e(TAG, "onAccessibilityEvent: ${nodeInfoList.size}")
                for (info in nodeInfoList) {
                    val charSequence = info.text
                    if (charSequence != null) {
                        val msg = charSequence.toString()
                        if (msg.contains("跳过")) {
                            info.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                            Log.e(TAG, "onAccessibilityEvent: Skip Successfully!")
                        } else {
                            Log.e(TAG, "onAccessibilityEvent: No Target String found!")
                        }
                    }
                }
            }
        }
    }

    private fun skip(nodeInfoList: List<AccessibilityNodeInfo>) {
        Log.d(TAG, "nodes size: ${nodeInfoList.size}")
        if (nodeInfoList.isNotEmpty()) {
            nodeInfoList[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
    }

    private fun tryGetActivity(componentName: ComponentName): ActivityInfo? {
        return try {
            packageManager.getActivityInfo(componentName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    override fun onInterrupt() {}

}