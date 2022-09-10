package com.lyni.app.lib.core.common.base.config

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.lifecycle.LifecycleObserver
import com.lyni.app.lib.core.R
import com.lyni.treasure.ktx.*

/**
 * @date 2022/3/11
 * @author Liangyong Ni
 * description toolbar config
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class ToolbarConfig(private var provider: ToolbarProvider) : LifecycleObserver {
    private val binding by lazy { provider.getToolbarBinding() }

    /**
     * 设置返回图标事件
     * @param showBack  是否显示返回键
     * @param backResId 返回键icon，传入null将不会改变图标，隐藏icon请将[showBack]设为true，或使用[disableBack]
     * @param listener  返回监听，设置为null时将不会修改行为，如果需要取消监听，请设置为函数体为空的监听
     */
    fun setBack(
        showBack: Boolean = true,
        @DrawableRes backResId: Int?,
        listener: (() -> Unit)?
    ): ToolbarConfig = apply {
        binding.apply {
            if (!showBack) {
                ivReturn.gone()
            } else {
                ivReturn.visible()
                backResId?.let {
                    ivReturn.setImageDrawable(it.getDrawable())
                }
            }
            listener?.let {
                ivReturn.onClick { listener.invoke() }
            }
        }
    }

    /**
     * 启用返回键
     */
    fun enableBack() = setBack(true, null, null)

    /**
     * 关闭返回键
     */
    fun disableBack() = setBack(false, null, null)

    /**
     * 设置返回键图标
     */
    fun backRes(@DrawableRes backRes: Int) = setBack(true, backRes, null)

    /**
     * 设置返回事件
     * @param click 点击事件，设置为空时会清除原有的点击事件
     */
    fun backClick(click: (() -> Unit)?) = apply {
        binding.ivReturn.onClick {
            click?.invoke()
        }
    }

    /**
     * 设置toolbar背景颜色
     * @param backgroundColor 颜色
     */
    fun backgroundColor(backgroundColor: Int): ToolbarConfig = apply {
        binding.toolbar.setBackgroundColor(backgroundColor.getColor())
    }

    /**
     * 设置标题
     * @param text      标题文字
     * @param textColor 标题文字颜色
     */
    fun title(text: String? = null, @ColorRes textColor: Int? = R.color.color_black): ToolbarConfig = apply {
        binding.apply {
            text?.let {
                tvTitle.visible()
                tvTitle.text = text
                textColor?.let { tvTitle.setTextColor(textColor.getColor()) }
            } ?: let {
                tvTitle.gone()
            }
        }
    }

    /**
     * 基于ImageView的功能按键
     * @param menuResId 资源id，为空时会隐藏menu
     */
    fun menuIcon(menuResId: Int?): ToolbarConfig = apply {
        binding.apply {
            menuResId?.let {
                ivMenu.visible()
                ivMenu.setImageDrawable(menuResId.getDrawable())
            } ?: let {
                ivMenu.gone()
            }
        }
    }

    /**
     * 基于ImageView的功能按键点击事件
     * @param listener 点击事件，设置为空时会清除原有的点击事件
     */
    fun menuClick(listener: (() -> Unit)?): ToolbarConfig = apply {
        binding.ivMenu.onClick {
            listener?.invoke()
        }
    }

    /**
     * 显示基于ImageView的功能按键
     */
    fun showMenu(): ToolbarConfig = apply {
        binding.ivMenu.visible()
    }

    /**
     * 隐藏基于ImageView的功能按键
     */
    fun hideMenu(): ToolbarConfig = apply {
        binding.ivMenu.gone()
    }
}