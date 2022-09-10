package com.lyni.app.lib.core.common.base.ui

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.lyni.app.lib.core.common.base.interfaces.ListPageListener
import com.lyni.app.lib.core.common.base.viewmodel.BaseListViewModel
import com.lyni.treasure.ktx.otherwise
import com.lyni.treasure.ktx.positive

/**
 * @date 2022/3/4
 * @author Liangyong Ni
 * description 列表基类
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseListActivity<VB : ViewBinding, VM : BaseListViewModel<T>, T : Any> :
    BaseActivity<VB, VM>(), ListPageListener<T> {
    //列表
    val mRecyclerView by lazy { getRecyclerView() }

    //适配器
    val mAdapter by lazy { onCreateAdapter() }

    //设置默认布局管理器
    override fun layoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(this)

    override fun initView() {
        super.initView()
        mRecyclerView.layoutManager = layoutManager()
        mRecyclerView.adapter = mAdapter
        viewModel.refresh()
    }

    override fun initListener() {
        getRefreshLayout().apply {
            setEnableRefresh(enableRefresh())
            setEnableLoadMore(enableLoadMore())
            setOnRefreshListener {
                setEnableLoadMore(false)
                refresh()
            }
            setOnLoadMoreListener {
                // 上拉加载数据
                viewModel.loadMore()
            }
        }
    }

    override fun initLiveData() {
        super.initLiveData()
        viewModel.listData.observe(this) {
            // 没有数据显示空态，否则回调数据加载函数
            viewModel.isEmpty().positive {
                showEmpty()
            }.otherwise {
                onDataLoaded(it)
            }
        }
    }

    override fun enableRefresh() = true

    override fun enableLoadMore() = true

    /**
     * 数据加载回调
     * @param dataList 加载的数据
     */
    protected open fun onDataLoaded(dataList: MutableList<T>) {
        getRefreshLayout().apply {
            viewModel.isFirstPage().positive {
                // 数据是第一页
                finishRefresh()
                mAdapter.setNewInstance(dataList)
                viewModel.isLastPage().positive {
                    setNoMoreData(true)
                }.otherwise {
                    setNoMoreData(false)
                    setEnableLoadMore(enableLoadMore())
                }
            }.otherwise {
                mAdapter.addData(dataList)
                viewModel.isLastPage().positive {
                    setNoMoreData(true)
                    finishLoadMoreWithNoMoreData()
                }.otherwise {
                    finishLoadMore()
                }
            }
        }
    }

    override fun showError() {
        super.showError()
        getRefreshLayout().apply {
            isRefreshing.positive {
                finishRefresh(false)
            }.otherwise {
                finishLoadMore(false)
            }
            setEnableRefresh(false)
            setEnableLoadMore(false)
        }
    }

    override fun showEmpty() {
        super.showEmpty()
        getRefreshLayout().apply {
            isRefreshing.positive {
                finishRefresh()
            }.otherwise {
                finishLoadMore()
            }
            setEnableRefresh(false)
            setEnableLoadMore(false)
        }
    }

    override fun dismissEmpty() {
        super.dismissEmpty()
        getRefreshLayout().setEnableLoadMore(enableLoadMore())
        getRefreshLayout().setEnableRefresh(enableRefresh())
    }

    override fun dismissError() {
        super.dismissError()
        getRefreshLayout().setEnableLoadMore(enableLoadMore())
        getRefreshLayout().setEnableRefresh(enableRefresh())
    }

    override fun refresh() {
        viewModel.refresh()
    }
}