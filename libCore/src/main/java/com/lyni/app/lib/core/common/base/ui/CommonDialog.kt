package com.lyni.app.lib.core.common.base.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.lyni.app.lib.core.R
import com.lyni.app.lib.core.common.base.entity.DialogType
import com.lyni.app.lib.core.databinding.CommonDialogLoadingBinding
import com.lyni.treasure.ktx.getDrawable
import com.lyni.treasure.ktx.gone
import com.lyni.treasure.ktx.visible

/**
 * @date 2022/3/4
 * @author Liangyong Ni
 * description 弹出的对话框：成功、失败、进行、默认
 */
class CommonDialog(context: Context) : Dialog(context) {

    private val binding by lazy {
        CommonDialogLoadingBinding.inflate(layoutInflater)
    }

    private val lottieAnimationView: LottieAnimationView by lazy {
        binding.lottieDialog
    }

    private val description: TextView by lazy {
        binding.textView
    }

    private val ivStatic: ImageView by lazy {
        binding.ivStatic
    }

    private val ivSuccess by lazy { R.drawable.common_ic_done_blue_24.getDrawable() }
    private val ivWarning by lazy { R.drawable.common_ic_warning_24.getDrawable() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window?.setBackgroundDrawableResource(R.drawable.common_bg_12_ffffff)
    }

    fun show(desc: String? = null, type: DialogType) {
        if ((desc?.length ?: 0) > 14) {
            throw IllegalArgumentException("提示字符不能多于14个")
        }
        when (type) {
            DialogType.Default -> {
                lottieAnimationView.gone()
                ivStatic.gone()
            }
            DialogType.Executing -> {
                lottieAnimationView.visible()
                ivStatic.gone()
            }
            DialogType.Success -> {
                lottieAnimationView.gone()
                ivStatic.visible()
                ivStatic.setImageDrawable(ivSuccess)
            }
            DialogType.Warning -> {
                lottieAnimationView.gone()
                ivStatic.visible()
                ivStatic.setImageDrawable(ivWarning)
            }
        }
        description.text = desc
        show()
    }
}