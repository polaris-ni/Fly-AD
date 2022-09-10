package com.lyni.app.lib.core.common.base.interfaces

import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * @date 2022/3/4
 * @author Liangyong Ni
 * description 分页列表接口
 */
interface ListPageListener<T> {

    /**
     * 获取RecyclerView
     *
     * @return [RecyclerView]
     */
    fun getRecyclerView(): RecyclerView

    /**
     * 构建适配器
     *
     * @return [BaseQuickAdapter]
     */
    fun onCreateAdapter(): BaseQuickAdapter<T, out BaseViewHolder>

    /**
     * 设置布局管理器
     *
     * @return [RecyclerView.LayoutManager]
     */
    fun layoutManager(): RecyclerView.LayoutManager
}