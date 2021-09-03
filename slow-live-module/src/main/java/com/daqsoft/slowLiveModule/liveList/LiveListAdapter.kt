package com.daqsoft.slowLiveModule.liveList

import android.annotation.SuppressLint
import android.graphics.drawable.AnimationDrawable
import android.view.View
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.GlideModuleUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.slowLiveModule.R
import com.daqsoft.slowLiveModule.bean.LiveListBean
import com.daqsoft.slowLiveModule.databinding.SlowLiveItemLiveListBinding
import java.lang.StringBuilder


/**
 * @Description
 * @ClassName   LiveListAdapter
 * @Author      Wongxd
 * @Time        2020/4/15 20:04
 */
internal class LiveListAdapter :
    RecyclerViewAdapter<SlowLiveItemLiveListBinding, LiveListBean>(R.layout.slow_live_item_live_list) {


    @SuppressLint("CheckResult")
    override fun setVariable(
        mBinding: SlowLiveItemLiveListBinding,
        position: Int,
        item: LiveListBean
    ) {

        mBinding.item = item

        val drawable = mBinding.ivLive.background as AnimationDrawable
        if (!drawable.isRunning) {
            drawable.start()
        }
        if(item.getType()=="1"){
            mBinding.ivLive.visibility= View.VISIBLE
            mBinding.tvState.visibility= View.VISIBLE
            mBinding.tvState.text="Live直播中"
        }else  if(item.getType()=="2"){
            mBinding.ivLive.visibility= View.GONE
            mBinding.tvState.visibility= View.VISIBLE
            mBinding.tvState.text="直播回放"
        }else{
            mBinding.ivLive.visibility= View.GONE
            mBinding.tvState.visibility= View.GONE
        }
        mBinding.tvPlayNum.text = item.showNum.toString()
        var name: StringBuilder = StringBuilder("")
        if (!item?.scenicName.isNullOrEmpty()) {
            name.append(item!!.scenicName)
            name.append("-")
        }
        name.append("" + item?.scenicSpotsName)
        mBinding.tvName.text = name

        mBinding.ivBg.apply {
//            if(item.getType()=="2" && !item.replayCover.isNullOrEmpty()){
//                loadWithDefault(item.replayCover)
//            }else{
//                loadWithDefault(item.getImages())
//            }
            if(item.getType()=="2" && !item.replayCover.isNullOrEmpty()){
                GlideModuleUtil.loadDqImage(item.replayCover,this)
            }else{
                GlideModuleUtil.loadDqImage(item.getImages(), this)
            }
        }


        mBinding.root.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.SlowLiveModule.SLOW_LIVE_DETAIL_ACTIVITY)
                .withInt("scenicSpotsId", item.scenicSpotsId)
                .withString("scenicSpotsName", item.scenicSpotsName)
                .navigation()
        }

    }


}