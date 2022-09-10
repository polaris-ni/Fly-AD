package com.lyni.app.lib.core.common.base.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lyni.treasure.ktx.otherwise
import com.lyni.treasure.ktx.positive

/**
 * @date 2022/3/4
 * @author Liangyong Ni
 * description 适用于列表的ViewModel
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseListViewModel<T : Any> : BaseViewModel() {
    // 当前页码
    protected var mPage = 0

    // 页面大小
    protected var mPageSize = 10

    // 加载的数据
    protected val mListData: MutableLiveData<MutableList<T>> = MutableLiveData()

    val listData: LiveData<MutableList<T>> = mListData

    /**
     * 加载数据
     * 改进添加默认协程执行
     * @param mPage page index
     * @param mPageSize page size
     */
    abstract fun loadData(mPage: Int, mPageSize: Int)

    /**
     * 加载数据
     */
    open fun loadMore() {
        mPage++
        loadData(mPage, mPageSize)
    }

    /**
     * 刷新数据
     */
    open fun refresh() {
        mPage = 0
        loadData(mPage, mPageSize)
    }

    /**
     * 是否是第一页
     * @return true表示第一页，反之不是
     */
    open fun isFirstPage(): Boolean = (mPage == 0)

    /**
     * 是否是最后一页
     * @return true表示最后一页，反之不是
     */
    open fun isLastPage() = mPageSize > (listData.value?.size ?: 0)

    /**
     * 数据是否为空
     * @return true表示数据为空，反之不是
     */
    open fun isEmpty(): Boolean {
        (mPage == 0 && listData.value.isNullOrEmpty()).positive {
            isEmpty.postValue(true)
            return true
        }.otherwise {
            isEmpty.postValue(false)
        }
        return false
    }
}