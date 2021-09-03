package com.daqsoft.venuesmodule.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.ActivityRoomBean
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemVenueActivityRoomBinding
import org.jetbrains.anko.toast

/**
 * @Description 文化场活动馆室
 * @ClassName   VenuesActivityRoomAdapter
 * @Author      luoyi
 * @Time        2020/3/27 11:58
 */
class VenuesActivityRoomAdapter : RecyclerViewAdapter<ItemVenueActivityRoomBinding, ActivityRoomBean> {

    var mContext: Context? = null
    var isNeedShowMore: Boolean = false

    constructor(context: Context) : super(R.layout.item_venue_activity_room) {
        this.mContext = context
    }

    override fun getItemCount(): Int {
        if (isNeedShowMore) {
            if (getData().size > 3) {
                return 3
            }
        }
        return super.getItemCount()
    }

    override fun setVariable(mBinding: ItemVenueActivityRoomBinding, position: Int, item: ActivityRoomBean) {
        val images = item.images.split(",")
        if (images.isNotEmpty()) {
            mBinding.url = images[0]
        }
        mBinding.txtVenueActivityRoom.text = item.name
        val tags = mutableListOf<String>()
        if (!item.type.isNullOrEmpty()) {
            tags.add(item.type)
        }
        if (item.faithAuditStatus == "1") {
            tags.add("诚信免审")
        }
        if (item.faithUseStatus == "1") {
            tags.add("诚信优享")
        }
        if (!tags.isNullOrEmpty()) {
            val tagLayoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            val tagAdapter = VenueTagsAdapter(mContext!!)
            tagAdapter.add(tags)
            mBinding.recyVenueActivityRoomTags.layoutManager = tagLayoutManager
            mBinding.recyVenueActivityRoomTags.adapter = tagAdapter
            mBinding.recyVenueActivityRoomTags.visibility = View.VISIBLE
        } else {
            mBinding.recyVenueActivityRoomTags.visibility = View.GONE
        }


        if (!item.video.isNullOrEmpty()) {
            mBinding.imgVenueActivityRoomVideo.visibility = View.VISIBLE
        } else {
            mBinding.imgVenueActivityRoomVideo.visibility = View.GONE
        }
        if (!item.panoramaUrl.isNullOrEmpty()) {
            mBinding.imgVenueActivityRoom720.visibility = View.VISIBLE
        } else {
            mBinding.imgVenueActivityRoom720.visibility = View.GONE
        }

        if (!item.area.isNullOrEmpty()) {
            mBinding.txtActivityRoomInfo.text = mContext!!.getString(
                R.string.venue_activity_room_area,
                item.area, item.galleryful
            )
            mBinding.txtActivityRoomInfo.visibility = View.VISIBLE
        } else {
            mBinding.txtActivityRoomInfo.visibility = View.GONE
        }
        if (item.openStatus) {
            mBinding.txtOrderActivityRoom.visibility = View.VISIBLE
        } else {
            mBinding.txtOrderActivityRoom.visibility = View.GONE
        }
        mBinding.root.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                ARouter.getInstance()
                    .build(ARouterPath.VenuesModule.ACTIVITY_ROOM_DETAIL)
                    .withString("id", item.id)
                    .navigation()
            } else {
                ToastUtils.showMessage("预订活动室，必须登录~")
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
        mBinding.txtOrderActivityRoom.onNoDoubleClick {
            if (AppUtils.isLogin()) {
                ARouter.getInstance()
                    .build(ARouterPath.VenuesModule.ACTIVITY_ROOM_DETAIL)
                    .withString("id", item.id)
                    .navigation()
            } else {
                ToastUtils.showMessage("预订活动室，必须登录~")
                ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }

    }
}
