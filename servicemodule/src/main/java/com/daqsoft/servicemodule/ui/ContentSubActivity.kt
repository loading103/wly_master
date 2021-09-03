package com.daqsoft.servicemodule.ui

import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ActivityArtFoundBinding
import com.daqsoft.android.scenic.servicemodule.databinding.ItemArtFoundBinding
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.servicemodule.bean.ArtFoundListBean
import com.daqsoft.servicemodule.bean.Subsets
import com.daqsoft.servicemodule.net.ServiceRepository
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * @Description
 * @ClassName   ContentSubActivity
 * @Author      luoyi
 * @Time        2020/9/23 11:58
 */
@Route(path = ARouterPath.ServiceModule.CONTENT_SUB_ACITIVTY)
class ContentSubActivity : TitleBarActivity<ActivityArtFoundBinding, ContentSubViewModel>() {
    @Autowired
    @JvmField
    var channelCode = ""
    /**
     * 关联资源ID
     */
    @Autowired
    @JvmField
    var titleStr = ""

    override fun getLayout(): Int {
        return R.layout.activity_art_found
    }

    override fun setTitle(): String {
        return titleStr?:"栏目列表"
    }

    override fun injectVm(): Class<ContentSubViewModel> = ContentSubViewModel::class.java

    override fun initView() {
        mBinding.recyArtFound.visibility = View.GONE
        mBinding.recyArtFound.apply {
            layoutManager = LinearLayoutManager(this@ContentSubActivity)
            adapter = artFoundAdapter
        }
        //艺术基金列表
        mModel.result.observe(this, Observer {
            mBinding.recyArtFound.visibility = View.VISIBLE
            val data = it
            if (data != null) {
                artFoundAdapter.add(data)
                artFoundAdapter.notifyDataSetChanged()
            }
        })
    }

    private val artFoundAdapter =
        object : RecyclerViewAdapter<ItemArtFoundBinding, Subsets>(R.layout.item_art_found) {
            @SuppressLint("CheckResult")
            override fun setVariable(mBinding: ItemArtFoundBinding, position: Int, item: Subsets) {
                mBinding.url = item.backgroundImg
                mBinding.tvName.text = item.name
                if (item.content.isNotEmpty()) {
                    mBinding.tvContent.visibility = View.VISIBLE
                    mBinding.tvContent.text = item.content
                } else {
                    mBinding.tvContent.visibility = View.INVISIBLE
                }
                RxView.clicks(mBinding.root)
                    .throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe {
                        //点击跳转
//                        ARouter.getInstance()
//                            .build(ARouterPath.ServiceModule.SERVICE_ART_DETAIL_ACTIVITY)
//                            .withString("jumpUrl", StringUtil.getTokenUrl(item.url))
//                            .withString("jumpTitle", item.name)
//                            .navigation()
                        ARouter.getInstance().build(MainARouterPath.MAIN_CONTENT)
                            .withString("channelCode", item.channelCode)
                            .navigation()
                    }
            }
        }

    override fun initData() {
        channelCode?.let {
            mModel.channelSubset(channelCode)
        }

    }
}

class ContentSubViewModel : BaseViewModel() {
    var result = MutableLiveData<MutableList<Subsets>>()
    /**
     *获取艺术基金图片列表
     */
    fun channelSubset(channelCode: String) {
        val map = HashMap<String, String>()
        map["channelCode"] = channelCode
        ServiceRepository().service.channelSubset(map)
            .excute(object : BaseObserver<ArtFoundListBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ArtFoundListBean>) {
                    result.postValue(response.data?.subset)
                }

            })
    }
}
