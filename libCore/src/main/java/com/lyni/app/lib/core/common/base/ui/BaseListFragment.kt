package com.lyni.app.lib.core.common.base.ui

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.lyni.app.lib.core.common.base.interfaces.ListPageListener
import com.lyni.app.lib.core.common.base.viewmodel.BaseListViewModel
import com.lyni.treasure.ktx.otherwise
import com.lyni.treasure.ktx.positive

/**
 * @date 2022/3/5
 * @author Liangyong Ni
 * description 列表Fragment基类
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseListFragment<VB : ViewBinding, VM : BaseListViewModel<T>, T : Any> :
    BaseFragment<VB, VM>(), ListPageListener<T> {

    // 列表
    protected val mRecyclerView by lazy { getRecyclerView() }

    // 适配器
    protected val mAdapter by lazy { onCreateAdapter() }

    override fun layoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(context)

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
    protected open fun onDataLoaded(dataList: List<T>) {
        getRefreshLayout().apply {
            viewModel.isFirstPage().positive {
                finishRefresh()
                mAdapter.setNewInstance(dataList as MutableList<T>)
                viewModel.isLastPage().positive {
                    setNoMoreData(true)
                }.otherwise {
                    setEnableLoadMore(enableLoadMore())
                }
            }.otherwise {
                mAdapter.addData(dataList as MutableList<T>)
                viewModel.isLastPage().positive {
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
        }
    }

    override fun refresh() {
        viewModel.refresh()
    }
}