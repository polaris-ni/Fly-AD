package com.lyni.app.adno

import com.lyni.app.adno.databinding.ActivityMainBinding
import com.lyni.app.lib.core.common.base.ui.BaseActivity
import com.lyni.app.lib.core.common.base.viewmodel.BaseViewModel

class MainActivity : BaseActivity<ActivityMainBinding, BaseViewModel>() {
    override fun initView() {
        super.initView()
        hideToolbar()
    }
}