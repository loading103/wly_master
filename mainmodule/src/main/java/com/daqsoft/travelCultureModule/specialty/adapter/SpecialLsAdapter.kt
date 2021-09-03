package com.daqsoft.travelCultureModule.specialty.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.amap.api.maps.model.LatLng
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemPlaygroundLsBinding
import com.daqsoft.mainmodule.databinding.ItemSpecialListBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.SpeaiclBean
import com.daqsoft.travelCultureModule.playground.adapter.TagAdapter
import org.jetbrains.anko.backgroundResource

class SpecialLsAdapter : RecyclerViewAdapter<ItemSpecialListBinding, SpeaiclBean> {
    var mContext: Context? = null

    var adapter: SpecialTagAdapter? = null
    /**
     * 当前经纬度
     */
    var selfLocation: LatLng? = null

    var onItemClickListener: OnSpecialItemClickListener? = null

    constructor(context: Context) : super(R.layout.item_special_list) {
        mContext = context
    }

    @SuppressLint("CheckResult")
    override fun setVariable(
        mBinding: ItemSpecialListBinding,
        position: Int,
        item: SpeaiclBean
    ) {
        mBinding.item = item


        if(item.signType.isNullOrEmpty()) {
            mBinding.ivBiaozhi.visibility = View.GONE
        }else {
            if(item?.signType!![0] == "农产品地理标志"){
                mBinding.ivBiaozhi.visibility = View.VISIBLE
                mBinding.ivBiaozhi.setImageResource(R.mipmap.specialty_list_tag_nongchanpin)
            }else if (item?.signType!![0] == "地理标志保护产品"){
                mBinding.ivBiaozhi.visibility = View.VISIBLE
                mBinding.ivBiaozhi.setImageResource(R.mipmap.specialty_list_tag_guojiadili)
            }else{
                mBinding.ivBiaozhi.visibility = View.GONE
            }
        }
        // 收藏状态
        if (item.vipResourceStatus != null) {
            if (item?.vipResourceStatus?.collectionStatus == true) {
                mBinding.imgFoodsCollect.backgroundResource = R.mipmap.activity_collect_selected
            } else {
                mBinding.imgFoodsCollect.backgroundResource = R.mipmap.activity_collect_normal
            }
            mBinding.imgFoodsCollect.visibility = View.VISIBLE
        } else {
            mBinding.imgFoodsCollect.visibility = View.GONE
        }

        // 标志
        if(!item.getBiaoTag().isNullOrEmpty()){
            setType(item.getBiaoTag()!!,mBinding)
        }


        mBinding.root.onNoDoubleClick {
            ARouter.getInstance().build(MainARouterPath.MAIN_SPECIAL_DETAIL)
                .withString("id", item.id.toString())
                .navigation()
        }
        mBinding.imgFoodsCollect.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                if (onItemClickListener != null && item.vipResourceStatus != null) {
                    onItemClickListener!!.onCollectClick(item.id.toString(), position, item.vipResourceStatus?.collectionStatus!!)
                }
            } else {
                ToastUtils.showMessage("该操作需要登录，请先登录")
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
    }


    /**
     * 标签
     */
    private fun setType(types: MutableList<String>, mBinding: ItemSpecialListBinding) {
        val linearLayoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        adapter = SpecialTagAdapter()
        mBinding.rv.layoutManager = linearLayoutManager
        mBinding.rv.adapter = adapter
        adapter?.setNewData(types)
    }

    override fun payloadUpdateUi(mBinding: ItemSpecialListBinding, position: Int, payloads: MutableList<Any>) {
        if (payloads[0] == "updateCollect") {
            if (getData()[position].vipResourceStatus != null) {
                val status = getData()[position].vipResourceStatus?.collectionStatus!!
                if (status) {
                    mBinding.imgFoodsCollect.backgroundResource = R.mipmap.activity_collect_selected
                } else {
                    mBinding.imgFoodsCollect.backgroundResource = R.mipmap.activity_collect_normal
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
                    getData()[position].vipResourceStatus?.collectionStatus = status
                    notifyItemChanged(position, "updateCollect")
                }
            }
        } catch (e: Exception) {

        }
    }

    interface OnSpecialItemClickListener {
        fun onCollectClick(id: String, postion: Int, status: Boolean)
    }
}