package com.daqsoft.legacyModule.adapter

import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.legacyModule.R
import com.daqsoft.legacyModule.bean.LegacyWatchStoryListBean
import com.daqsoft.legacyModule.databinding.LegacyModuleItemWatchStoryBinding
import com.daqsoft.provider.ARouterPath


/**
 * @des    我的关注 adapter
 * @class  LegacyWatchStoryAdapter
 * @author Wongxd
 * @date   2020-4-21 14:16
 *
 */
internal open class LegacyWatchStoryAdapter(val context:Context) : RecyclerViewAdapter<LegacyModuleItemWatchStoryBinding, LegacyWatchStoryListBean>
    (R.layout.legacy_module_item_watch_story) {
    override fun setVariable(mBinding: LegacyModuleItemWatchStoryBinding, position: Int, item: LegacyWatchStoryListBean) {
        mBinding.item = item
        var image = ""
        if(!item.images.isNullOrEmpty()){
            image = item.images.split(",")[0]
        }else if(!item.video.isNullOrEmpty()){
            image = StringUtil.getVideoCoverUrl(item.video)
        }
        Glide.with(context).load(image).placeholder(R.mipmap.placeholder_img_fail_h158).into(mBinding.iv)
        Glide.with(context).load(item.headUrl).placeholder(R.mipmap.mine_profile_photo_default).into(mBinding.ivHeader)
        mBinding.tvTime.text = "${item.createDate} 发布了1个作品"
        mBinding.root.onNoDoubleClick {
            ARouter.getInstance()
                .build(ARouterPath.LegacyModule.LEGACY_WORKS_DETAIL)
                .withInt("type",0)
                .withString("id",item.id)
                .navigation()
        }
    }
}