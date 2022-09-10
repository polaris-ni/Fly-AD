package com.lyni.app.lib.core.common.base.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

/**
 * @date 2022/2/14
 * @author Liangyong Ni
 * description ViewModel基类
 */
@Suppress("MemberVisibilityCanBePrivate")
open class BaseViewModel : ViewModel() {
    // Loading 状态
    protected val isLoading = UnPeekLiveData<Boolean>()

    //缺省页面
    protected val isEmpty = UnPeekLiveData<Boolean>()

    // 请求异常
    private val error = UnPeekLiveData<Throwable?>()

    fun isLoadingLiveData(): LiveData<Boolean> = isLoading
    fun isEmptyLiveData(): LiveData<Boolean> = isEmpty
    fun errorLiveData(): LiveData<Throwable?> = error

    fun <T> Flow<T>.flowLoading(): Flow<T> {
        return this.onStart {
            isLoading.postValue(true)
            error.postValue(null)
            isEmpty.postValue(false)
        }.onCompletion {
            isLoading.postValue(false)
        }
    }

    private fun <T> Flow<T>.flowCatch() = this.catch {
        error.postValue(it)
//        it.printStackTrace()
    }
}