package com.lyni.app.lib.core.common.base.ui

import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * @date 2022/3/17
 * @author Liangyong Ni
 * description base view holder
 */
@Suppress("MemberVisibilityCanBePrivate")
open class BaseVBViewHolder<VB : ViewBinding>(val binding: VB) : BaseViewHolder(binding.root) {

    fun getIV(@IdRes viewId: Int): ImageView = getView(viewId)

    fun getTV(@IdRes viewId: Int): TextView = getView(viewId)
}