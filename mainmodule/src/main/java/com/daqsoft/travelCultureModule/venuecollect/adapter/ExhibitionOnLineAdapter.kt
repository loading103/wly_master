package com.daqsoft.travelCultureModule.venuecollect.adapter

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemExhibitionListBinding
import com.daqsoft.mainmodule.databinding.ItemExhibitionOnlineBinding
import com.daqsoft.mainmodule.databinding.ItemPlaygroundLsBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.ExhibitionTagBean
import com.daqsoft.provider.bean.VenueCollectBean
import com.daqsoft.travelCultureModule.playground.adapter.PlayGroundLsAdapter
import org.jetbrains.anko.backgroundResource
import org.jetbrains.anko.imageResource
import java.lang.Exception

internal class ExhibitionOnLineAdapter : RecyclerViewAdapter<ItemExhibitionOnlineBinding, VenueCollectBean>(R.layout.item_exhibition_online) {


    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemExhibitionOnlineBinding, position: Int, item: VenueCollectBean) {
        mBinding.datas=item
        //标签
        var tags= mutableListOf<String>()
        if(!TextUtils.isEmpty(item.typeName)){
            val tagAdapter = TagAdapter()
            mBinding.rvTag2.adapter =tagAdapter
            mBinding.rvTag2.visibility= View.VISIBLE
            tags.addAll(item.typeName.split(","))
            tagAdapter.setNewData(tags)
        }else{
            mBinding.rvTag2.visibility= View.GONE
        }
        //收藏
        if(item.vipResourceStatus.collectionStatus){
            mBinding.imgCollect.setImageResource(R.mipmap.activity_collect_selected)
        }else{
            mBinding.imgCollect.setImageResource(R.mipmap.activity_collect_normal)
        }

        mBinding.imgCollect.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                if (onCollectClickListener != null && item.vipResourceStatus != null) {
                    onCollectClickListener!!.onCollectClick(item.id.toString(), position, item.vipResourceStatus.collectionStatus,"CONTENT_TYPE_EXHIBITION")
                }
            } else {
                ToastUtils.showMessage("该操作需要登录，请先登录")
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }

        mBinding.root.onNoDoubleClick {
            if(TextUtils.isEmpty( item.panorama)){
                ToastUtils.showMessage("跳转地址异常")
                return@onNoDoubleClick
            }
            ARouter.getInstance()
                .build(ARouterPath.Provider.WEB_ACTIVITY)
                .withString("mTitle", item.name)
                .withString("html", StringUtil.formatHtmlUrl( item.panorama))
                .navigation()
        }

    }
    override fun payloadUpdateUi(mBinding: ItemExhibitionOnlineBinding, position: Int, payloads: MutableList<Any>) {
        if (payloads[0] == "updateCollect") {
            if (getData()[position].vipResourceStatus != null) {
                val status = getData()[position].vipResourceStatus.collectionStatus
                if (status) {
                    mBinding.imgCollect.imageResource = R.mipmap.activity_collect_selected
                } else {
                    mBinding.imgCollect.imageResource = R.mipmap.activity_collect_normal
                }
            }
        }
    }
    /**
     * 更新收藏状态
     */
    fun notifyCollectStatus(position: Int, status: Boolean) {
        try {
            if (position < getData().size) {
                if (getData()[position].vipResourceStatus != null) {
                    getData()[position].vipResourceStatus.collectionStatus = status
                    notifyItemChanged(position, "updateCollect")
                }
            }
        } catch (e: Exception) {

        }
    }

    var onCollectClickListener: OnCollectClickListener? = null

    interface OnCollectClickListener {
        fun onCollectClick(id: String, postion: Int, status: Boolean,type: String)
    }
}