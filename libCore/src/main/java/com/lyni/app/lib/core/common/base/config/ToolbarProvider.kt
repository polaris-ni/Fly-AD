package com.lyni.app.lib.core.common.base.config

import com.lyni.app.lib.core.databinding.CommonLayoutToolbarBinding

/**
 * @date 2022/3/11
 * @author Liangyong Ni
 * description 提供toolbar
 */
interface ToolbarProvider {
    /**
     * 提供toolbar
     */
    fun getToolbarBinding(): CommonLayoutToolbarBinding
}