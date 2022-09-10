package com.lyni.app.lib.core.common.base.interfaces

import com.lyni.app.lib.core.common.base.entity.DialogType

/**
 * @date 2022/4/4
 * @author Liangyong Ni
 * description IDialogShow
 */
interface DialogHandler {

    fun showDialog(desc: String, type: DialogType)

    fun loadingDialog(isShow: Boolean)

    fun loadingDialog(isShow: Boolean, desc: String)

    fun warningDialog(desc: String, duration: Long = 2000)

    fun defaultDialog(desc: String, duration: Long = 2000)

    fun successDialog(desc: String, duration: Long = 2000)

    fun dialogDismiss()
}