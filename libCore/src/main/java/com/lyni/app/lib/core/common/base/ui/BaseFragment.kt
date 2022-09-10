package com.lyni.app.lib.core.common.base.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.lyni.app.lib.core.R
import com.lyni.app.lib.core.common.base.entity.CustomHolder
import com.lyni.app.lib.core.common.base.entity.DialogType
import com.lyni.app.lib.core.common.base.interfaces.DialogHandler
import com.lyni.app.lib.core.common.base.viewmodel.BaseViewModel
import com.lyni.app.lib.core.databinding.CommonFragmentRootBinding
import com.lyni.app.lib.core.databinding.CommonLayoutCustomHolderBinding
import com.lyni.treasure.ktx.*
import kotlinx.coroutines.delay
import java.lang.reflect.ParameterizedType

/**
 * @date 2022/3/5
 * @author Liangyong Ni
 * description Fragment基类
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel> : Fragment(), DialogHandler {
    companion object {
        private const val STATUS_DEFAULT = 0
        private const val STATUS_LOADING = 1
        private const val STATUS_EMPTY = 2
        private const val STATUS_ERROR = 3
    }

    private lateinit var rootBinding: CommonFragmentRootBinding
    protected lateinit var binding: VB
    protected lateinit var viewModel: VM
    private var currentStatus: Int = STATUS_DEFAULT
    private val type = javaClass.genericSuperclass
    private val dialog: CommonDialog by lazy {
        CommonDialog(requireContext())
    }
    private val customHolderView: CommonLayoutCustomHolderBinding by lazy {
        CommonLayoutCustomHolderBinding.inflate(layoutInflater)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootBinding = CommonFragmentRootBinding.inflate(inflater)
        if (type is ParameterizedType) {
            val clazz = type.actualTypeArguments[0] as Class<VB>
            val method = clazz.getMethod("inflate", LayoutInflater::class.java)
            binding = method.invoke(null, layoutInflater) as VB
            viewModel = ViewModelProvider(this)[type.actualTypeArguments[1] as Class<VM>]
        }
        rootBinding.flMain.addView(binding.root, 0)
        return rootBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initViewModel()
        initView()
        initLiveData()
        initListener()
    }

    /**
     * 初始化data
     */
    open fun initData() {}

    /**
     * 初始化ViewModel
     */
    open fun initViewModel() {}

    /**
     * 初始化view
     */
    open fun initView() {
        rootBinding.refreshHeader.setAssetsFileName("lottie_refresh.json")
        rootBinding.refreshFooter.setAssetsFileName("lottie_refresh.json")
    }

    /**
     * 初始化LiveData
     */
    open fun initLiveData() {
        viewModel.isLoadingLiveData().observe(viewLifecycleOwner) {
            it.positive { showLoading() }.otherwise { dismissLoading() }
        }
        viewModel.isEmptyLiveData().observe(viewLifecycleOwner) {
            it.positive { showEmpty() }.otherwise { dismissEmpty() }
        }
        viewModel.errorLiveData().observe(viewLifecycleOwner) {
            it?.let { showError() } ?: dismissError()
        }
    }

    /**
     * 初始化监听器
     */
    open fun initListener() {
        getRefreshLayout().apply {
            setEnableRefresh(enableRefresh())
            setEnableLoadMore(enableLoadMore())
            setOnRefreshListener {
//                setEnableLoadMore(false)
                refresh()
            }
        }
    }

    /**
     * 显示页面错误态
     */
    open fun showError() {
        if (currentStatus != STATUS_ERROR) {
            currentStatus = STATUS_ERROR
            setCustomHolderView(errorHolder())
            replaceMainView(customHolderView.root)
        }
    }

    /**
     * 恢复默认显示
     */
    open fun dismissError() {
        if (currentStatus == STATUS_ERROR) {
            currentStatus = STATUS_DEFAULT
            replaceMainView(binding.root)
        }
    }

    /**
     * 显示页面空态
     */
    open fun showEmpty() {
        if (currentStatus != STATUS_EMPTY) {
            currentStatus = STATUS_EMPTY
            setCustomHolderView(emptyHolder())
            replaceMainView(customHolderView.root)
        }
    }

    /**
     * 恢复默认显示
     */
    open fun dismissEmpty() {
        if (currentStatus == STATUS_EMPTY) {
            currentStatus = STATUS_DEFAULT
            replaceMainView(binding.root)
        }
    }

    /**
     * 显示加载框
     */
    open fun showLoading() {
        if (currentStatus != STATUS_LOADING) {
            currentStatus = STATUS_LOADING
            loadingDialog(true)
        }
    }

    /**
     * 关闭加载框
     */
    open fun dismissLoading() {
        asyncLaunch {
            delay(500)
            currentStatus = STATUS_DEFAULT
            loadingDialog(false)
            getRefreshLayout().finishRefresh()
        }
    }

    /**
     * 下拉刷新功能是否启用
     */
    open fun enableRefresh(): Boolean = false


    /**
     * 上拉加载功能是否启动
     */
    open fun enableLoadMore(): Boolean = false

    /**
     * 页面刷新
     */
    open fun refresh() {
        currentStatus = STATUS_DEFAULT
    }

    /**
     * 替换main FrameLayout中的内容
     * @param view View
     */
    protected fun replaceMainView(view: View) {
        rootBinding.flMain.removeAllViews()
        rootBinding.flMain.addView(view, 0)
        getRefreshLayout().finishRefresh()
    }

    /**
     * 提供刷新视图的引用
     */
    fun getRefreshLayout() = rootBinding.srlRoot

    /**
     * 空态页面CustomHolder
     * @return CustomHolder
     */
    open fun emptyHolder() = CustomHolder(
        R.drawable.common_ic_empty_96,
        R.string.text_00001.getString(),
        R.string.text_00002.getString(),
        true,
        R.string.refresh.getString()
    ) {
        refresh()
    }

    /**
     * 错误态页面CustomHolder
     * @return CustomHolder
     */
    open fun errorHolder() = CustomHolder(
        R.drawable.common_ic_error_96,
        R.string.text_00003.getString(),
        R.string.text_00004.getString(),
        true,
        R.string.refresh.getString()
    ) {
        refresh()
    }

    /**
     * 设置CustomHolderView
     * @param holder CustomHolder
     */
    private fun setCustomHolderView(holder: CustomHolder) {
        customHolderView.apply {
            holder.title?.let { tvTitle.text = it } ?: tvTitle.gone()
            holder.description?.let { tvDesc.text = it } ?: tvDesc.gone()
            holder.imageRes?.getDrawable()?.let {
                ivPlaceHolder.setImageDrawable(it)
            }
            holder.showButton.positive {
                btnAction.visible()
                btnAction.text = holder.btnText
                btnAction.onClick { holder.event.invoke() }
            }.otherwise {
                btnAction.gone()
            }
        }
    }

    inline fun <reified T : Activity> navigateTo(isFinish: Boolean = false) {
        startActivity(Intent(requireContext(), T::class.java))
        isFinish.positive {
            requireActivity().finish()
        }
    }

    protected fun asyncLaunch(call: suspend () -> Unit) = lifecycleScope.safeLaunch(action = call)

    fun <T : Activity> navigateTo(clz: Class<T>) {
        startActivity(Intent(requireContext(), clz))
    }

    protected fun showToast(msg: String, duration: Long = 500) {
        defaultDialog(msg, duration)
    }

    override fun showDialog(desc: String, type: DialogType) {
        dialog.show(desc, type)
    }

    override fun loadingDialog(isShow: Boolean) {
        loadingDialog(isShow, "正在加载")
    }

    override fun loadingDialog(isShow: Boolean, desc: String) {
        isShow.positive {
            dialog.show(desc, DialogType.Executing)
        }.otherwise {
            dialogDismiss()
        }
    }

    override fun warningDialog(desc: String, duration: Long) {
        asyncLaunch {
            dialog.show(desc, DialogType.Warning)
            delay(duration)
            dialogDismiss()
        }
    }

    override fun defaultDialog(desc: String, duration: Long) {
        asyncLaunch {
            dialog.show(desc, DialogType.Default)
            delay(duration)
            dialogDismiss()
        }
    }

    override fun successDialog(desc: String, duration: Long) {
        asyncLaunch {
            dialog.show(desc, DialogType.Success)
            delay(duration)
            dialogDismiss()
        }
    }

    override fun dialogDismiss() {
        dialog.dismiss()
    }

    fun setRefreshHeaderBackground(@ColorInt color: Int? = null, @DrawableRes res: Int? = null) {
        color?.let { rootBinding.refreshHeader.setBackgroundColor(it) }
        res?.let { rootBinding.refreshHeader.setBackgroundResource(res) }
    }

    fun setRefreshFooterBackground(@ColorInt color: Int? = null, @DrawableRes res: Int? = null) {
        color?.let { rootBinding.refreshFooter.setBackgroundColor(it) }
        res?.let { rootBinding.refreshFooter.setBackgroundResource(res) }
    }

    fun setRootBackground(@ColorInt color: Int? = null, @DrawableRes res: Int? = null) {
        color?.let { rootBinding.srlRoot.setBackgroundColor(it) }
        res?.let { rootBinding.srlRoot.setBackgroundResource(res) }
    }
}