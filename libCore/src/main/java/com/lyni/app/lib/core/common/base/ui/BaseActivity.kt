package com.lyni.app.lib.core.common.base.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.lyni.app.lib.core.R
import com.lyni.app.lib.core.common.base.config.ToolbarConfig
import com.lyni.app.lib.core.common.base.config.ToolbarProvider
import com.lyni.app.lib.core.common.base.entity.CustomHolder
import com.lyni.app.lib.core.common.base.entity.DialogType
import com.lyni.app.lib.core.common.base.interfaces.DialogHandler
import com.lyni.app.lib.core.databinding.CommonActivityRootBinding
import com.lyni.app.lib.core.databinding.CommonLayoutCustomHolderBinding
import com.lyni.app.lib.core.common.base.viewmodel.BaseViewModel
import com.lyni.treasure.components.fitNavigationBar
import com.lyni.treasure.components.fitStatusBar
import com.lyni.treasure.components.immersiveNavigationBar
import com.lyni.treasure.components.immersiveStatusBar
import com.lyni.treasure.ktx.*
import kotlinx.coroutines.delay
import java.lang.reflect.ParameterizedType

/**
 * @date 2022/2/14
 * @author Liangyong Ni
 * description activity基类
 */
@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel> : AppCompatActivity(),
    ToolbarProvider, DialogHandler {

    companion object {
        private const val STATUS_DEFAULT = 0
        private const val STATUS_LOADING = 1
        private const val STATUS_EMPTY = 2
        private const val STATUS_ERROR = 3
    }

    protected lateinit var binding: VB
    protected lateinit var viewModel: VM
    private lateinit var rootBinding: CommonActivityRootBinding
    private val type = javaClass.genericSuperclass

    private var currentStatus: Int = STATUS_DEFAULT
    private val dialog: CommonDialog by lazy {
        CommonDialog(this)
    }
    private val customHolderView: CommonLayoutCustomHolderBinding by lazy {
        CommonLayoutCustomHolderBinding.inflate(layoutInflater)
    }
    private val toolbarConfig: ToolbarConfig by lazy { ToolbarConfig(this) }

    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootBinding = CommonActivityRootBinding.inflate(layoutInflater)
        if (type is ParameterizedType) {
            val clazz = type.actualTypeArguments[0] as Class<VB>
            val method = clazz.getMethod("inflate", LayoutInflater::class.java)
            binding = method.invoke(null, layoutInflater) as VB
            viewModel = ViewModelProvider(this)[type.actualTypeArguments[1] as Class<VM>]
        }
        rootBinding.flMain.addView(binding.root, 0)
        setContentView(rootBinding.root)
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
     * 初始化View
     */
    open fun initView() {
        rootBinding.refreshHeader.setAssetsFileName("lottie_refresh.json")
        rootBinding.refreshFooter.setAssetsFileName("lottie_refresh.json")
        toolbarConfig().backClick { super.onBackPressed() }.hideMenu()
        // 沉浸式状态栏和导航栏
        immersiveStatusBar()
        immersiveNavigationBar()
        fitStatusBar(true)
        fitNavigationBar(true)
    }

    /**
     * 初始化LiveData
     */
    open fun initLiveData() {
        viewModel.isLoadingLiveData().observe(this) {
            it.positive { showLoading() }.otherwise { dismissLoading() }
        }
        viewModel.isEmptyLiveData().observe(this) {
            it.positive { showEmpty() }.otherwise { dismissEmpty() }
        }
        viewModel.errorLiveData().observe(this) {
            it?.let { showError() } ?: dismissError()
        }
    }

    /**s
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
            delay(300)
            currentStatus = STATUS_DEFAULT
            loadingDialog(false)
            getRefreshLayout().isRefreshing.positive {
                getRefreshLayout().finishRefresh()
            }
        }
    }

    /**
     * 隐藏toolbar
     */
    protected fun hideToolbar() {
        rootBinding.toolbar.root.gone()
        val clParams = rootBinding.srlRoot.layoutParams as ConstraintLayout.LayoutParams
        clParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        clParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
        clParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
        clParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        rootBinding.srlRoot.layoutParams = clParams
    }

    /**
     * 提供刷新View引用
     */
    fun getRefreshLayout() = rootBinding.srlRoot

    /**
     * 提供Toolbar View引用
     */
    override fun getToolbarBinding() = rootBinding.toolbar

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

    /**
     * 下拉刷新功能是否启用
     */
    open fun enableRefresh(): Boolean = false

    /**
     * 上拉加载功能是否启动
     */
    open fun enableLoadMore(): Boolean = false

    /**
     * 替换main FrameLayout中的内容
     * @param view View
     */
    private fun replaceMainView(view: View) {
        rootBinding.flMain.removeAllViews()
        rootBinding.flMain.addView(view, 0)
        getRefreshLayout().finishRefresh()
    }

    /**
     * 设置CustomHolderView
     * @param holder CustomHolder
     */
    private fun setCustomHolderView(holder: CustomHolder) {
        customHolderView.apply {
            tvTitle.text = holder.title
            tvDesc.text = holder.description
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
     * 刷新操作
     */
    open fun refresh() {
        replaceMainView(binding.root)
    }

    /**
     * 配置ToolBar
     */
    open fun toolbarConfig() = toolbarConfig

    inline fun <reified T : Activity> navigateTo(isFinish: Boolean = false) {
        startActivity(Intent(this, T::class.java))
        isFinish.positive {
            finish()
        }
    }

    protected fun asyncLaunch(call: suspend () -> Unit) = lifecycleScope.safeLaunch(action = call)

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideInput(v, ev)) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                assert(v != null)
                imm.hideSoftInputFromWindow(v!!.windowToken, 0)
                v.clearFocus()
            }
            return super.dispatchTouchEvent(ev)
        }
        // 必不可少，否则所有的组件都不会有TouchEvent
        return window.superDispatchTouchEvent(ev) || onTouchEvent(ev)
    }

    open fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v is EditText) {
            val leftTop = intArrayOf(0, 0)
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        return false
    }

//    fun updateStatusBarColor(background: Int, isLightMode: Boolean? = null) {
//        isLightMode?.let {
//            // true表示Light Mode，状态栏字体呈黑色，反之呈白色
//            ViewCompat.getWindowInsetsController(window.decorView)?.isAppearanceLightStatusBars = isLightMode
//        }
//        // 修改状态栏背景颜色
//        window.statusBarColor = background
//    }

    protected fun showToast(msg: String, duration: Long = 1600) {
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

    protected fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        window.peekDecorView()?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}