package com.lyni.app.lib.core.common.base.ui

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.lyni.treasure.ktx.nowTime
import java.lang.reflect.ParameterizedType


/**
 * @date 2022/3/17
 * @author Liangyong Ni
 * description BaseDialogFragment
 */
@Suppress("MemberVisibilityCanBePrivate","unused")
abstract class BaseDialogFragment<VB : ViewBinding> : DialogFragment() {

    protected lateinit var binding: VB
    private val type = javaClass.genericSuperclass
    private val seed = nowTime()

    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(STYLE_NORMAL, getStyle())
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnKeyListener { _, keyCode, _ ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return@setOnKeyListener getCancelable().not()
                }
                false
            }
            setCanceledOnTouchOutside(getCancelable())
            setCancelable(getCancelable())
            this@BaseDialogFragment.isCancelable = getCancelable()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (type is ParameterizedType) {
            val clazz = type.actualTypeArguments[0] as Class<VB>
            val method = clazz.getMethod("inflate", LayoutInflater::class.java)
            binding = method.invoke(null, layoutInflater) as VB
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        initListener()
        super.onViewCreated(view, savedInstanceState)
    }

    open fun initListener() {}

    override fun onStart() {
        val params = dialog?.window?.attributes
        params?.let {
            it.width = getWidth()
            it.height = getHeight()
            it.gravity = getGravity()
        }
        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            decorView.setPadding(0, 0, 0, 0)
            attributes = params as WindowManager.LayoutParams
            getAnim()?.let {
                this.setWindowAnimations(it)
            }
        }

        super.onStart()
    }

    //?????????View?????????
    abstract fun initView()

    //??????style-????????????????????????style
    open fun getStyle(): Int = android.R.style.ThemeOverlay_Material_Dialog

    //????????????
    open fun getAnim(): Int? = null

    //????????????
    open fun getWidth(): Int = binding.root.layoutParams.height

    //???????????????
    open fun getHeight(): Int = binding.root.layoutParams.height

    //????????????
    open fun getGravity(): Int = Gravity.CENTER

    //??????????????????
    open fun getCancelable(): Boolean = true

    //??????show?????????????????????????????????
    override fun show(manager: FragmentManager, tag: String?) {
//        val transaction: FragmentTransaction = manager.beginTransaction()
//        val fragment: Fragment? = manager.findFragmentByTag(tag)
//        Log.e("TAG", "show: $fragment")
//        fragment?.let {
//            if (fragment is DialogFragment) {
//                fragment.dismiss()
//            }
//            transaction.remove(fragment)
//        }
//        transaction.commit()
        try {
            manager.beginTransaction().remove(this).commit()
            super.show(manager, tag)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun show(manager: FragmentManager) = show(manager, this.javaClass.simpleName + seed)

}