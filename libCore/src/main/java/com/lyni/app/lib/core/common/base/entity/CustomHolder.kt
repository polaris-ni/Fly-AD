package com.lyni.app.lib.core.common.base.entity

import androidx.annotation.Keep

/**
 * @date 2022/3/4
 * @author Liangyong Ni
 * description 自设定占位页
 */
@Keep
data class CustomHolder(
    val imageRes: Int? = null,
    val title: String? = null,
    val description: String? = null,
    val showButton: Boolean = true,
    val btnText: String? = null,
    val event: () -> Unit = {}
)