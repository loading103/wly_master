package com.daqsoft.travelCultureModule.branches

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.widget.RelativeLayout
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityBrandIntuoduceBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.utils.GaosiUtils
import com.daqsoft.travelCultureModule.branches.adapter.gvBaseAdapter
import com.daqsoft.travelCultureModule.branches.adapter.gvMDDBaseAdapter


@Route(path = MainARouterPath.MAIN_BRANCH_OTHER)
class BrandIntroduceActivity :
    TitleBarActivity<ActivityBrandIntuoduceBinding, BranchDetailActivityViewModel>() {
    var id: String = ""

    @JvmField
    @Autowired
    var siteId: String = ""

    /**
     * type=1 获取品牌介绍
     * type=2 获取游玩列表
     * type=3 获取目的地列表
     */
    var type: String = ""

    override fun getLayout(): Int = R.layout.activity_brand_intuoduce

    override fun setTitle(): String = "品牌简介"

    override fun injectVm(): Class<BranchDetailActivityViewModel> =
        BranchDetailActivityViewModel::class.java

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {

        mModel.branchDetailBean.observe(this, androidx.lifecycle.Observer {
            mBinding.tvBiName.text = it.name
            mBinding.tvBiContent.text = it.slogan
            Glide.with(this)
                .asBitmap()
                .load(it.brandImage)
                .centerCrop()
                .override(335, 187)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        var b = GaosiUtils.rsBlur(applicationContext, resource, 15)
                        mBinding.ivBiLogo.setImageBitmap(b)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })

            if (type == "1") {
                mBinding.tvBrandBpjs.text = "品牌介绍"
                mBinding.mvContentInfo.visibility = View.VISIBLE
                mBinding.mvContentInfo.settings.javaScriptEnabled = true
                var setting = mBinding.mvContentInfo.settings
                setting.setJavaScriptEnabled(true);
                setting.cacheMode = WebSettings.LOAD_DEFAULT;
                setting.userAgentString = System.getProperty("http.agent")
                // 把html中的内容放大webview等宽的一列中
                setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                setting.setAppCacheEnabled(true);
                setting.setDomStorageEnabled(true);
                setting.textZoom = 100
                mBinding.mvContentInfo.settings.setJavaScriptEnabled(true)
                mBinding.mvContentInfo.loadDataWithBaseURL(
                    null,
                    StringUtil.getHtml(it.suggest),
                    "text/html",
                    "utf-8",
                    null
                )
            } else if (type == "2") {
                mBinding.tvBrandBpjs.text = "品牌资源"
                mBinding.gvBrand.visibility = View.VISIBLE
                mModel.getBrandScenicList(id, "100")
            } else if (type == "3") {
                mBinding.tvBrandBpjs.text = "目的地"
                val spec: Int = View.MeasureSpec.makeMeasureSpec(16, View.MeasureSpec.UNSPECIFIED)
                mBinding.tvBrandBpjs.measure(spec, spec)
                // getMeasuredWidth
                // getMeasuredWidth
                val measuredWidth: Int = mBinding.tvBrandBpjs.getMeasuredWidth()
                var param = mBinding.vBrandBpjs.layoutParams
                param.width = measuredWidth
                mBinding.vBrandBpjs.layoutParams = param
                mBinding.gvBrand.visibility = View.VISIBLE
                mModel.getBrandMDD(id, "1000")
            }
        })
        // 景区玩乐
        mModel.branchScenicList.observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                if (it.size > 0) {
                    mBinding.gvBrand.numColumns = 2
                    var adapter = gvBaseAdapter(it, "1")
                    mBinding.gvBrand.adapter = adapter
                }
            }
        })

        // 目的地
        mModel.mddList.observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                if (it.size > 0) {
                    mBinding.gvBrand.numColumns = 3
                    var adapter = gvMDDBaseAdapter(it)
                    mBinding.gvBrand.adapter = adapter
                }
            }
        })
    }

    override fun initData() {
        id = intent.getStringExtra("id")
        type = intent.getStringExtra("type")
        if(BaseApplication.appArea.equals("sc")){
            mBinding.ivBranch.visibility=View.VISIBLE
        }else{
            mBinding.ivBranch.visibility=View.GONE
        }
        mModel.getBranchList(id, siteId)

    }
}