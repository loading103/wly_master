package com.daqsoft.venuesmodule.adapter

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.ActivityRoomBean
import com.daqsoft.provider.view.FlowLayout
import com.daqsoft.venuesmodule.R
import com.daqsoft.venuesmodule.databinding.ItemThemePavilionBinding
import org.jetbrains.anko.textColorResource

/**
 * @package name：com.daqsoft.venuesmodule.adapter
 * @date 2020/9/16 18:37
 * @author zp
 * @describe  主题展馆 adapter
 */
class ThemePavilionAdapter : RecyclerViewAdapter<ItemThemePavilionBinding, ActivityRoomBean>(R.layout.item_theme_pavilion) {

    var isNeedShowMore: Boolean = false

    override fun getItemCount(): Int {
        if (isNeedShowMore) {
            if (getData().size > 4) {
                return 4
            }
        }
        return super.getItemCount()
    }

    override fun setVariable(
        mBinding: ItemThemePavilionBinding,
        position: Int,
        item: ActivityRoomBean
    ) {
        // 宣传图
        val images = item.images.split(",")
        var url = ""
        if (images.isNotEmpty()) {
            url = images[0]
        }
        Glide
            .with(mBinding.root.context)
            .load(url)
            .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(5.dp)))
            .placeholder(R.drawable.placeholder_img_fail_240_180)
            .into(mBinding.image)

        // 名称
        mBinding.title.text = item.name

        // 标签
        mBinding.tag.removeAllViews()
        val tags = arrayListOf<String>()
        item.openStatus?.takeIf { true }?.apply { tags.add(mBinding.content.resources.getString(R.string.venue_can_be_reserved)) }
        item.type?.takeIf { it.isNotBlank() }?.apply { tags.add(this) }
        if (tags.isNotEmpty()){
            tags.forEachIndexed{index: Int, s: String ->
                createTagTextView(s,if (index == 0) R.color.green_36cd64 else R.color.color_333,mBinding.tag, index != tags.size-1)
            }
        }

        // 面积 and人数
        if (!item.area.isNullOrEmpty()) {
            mBinding.content.visibility = View.VISIBLE
            mBinding.content.text = mBinding.root.context.getString(R.string.venue_activity_room_area, item.area, item.galleryful)
        } else {
            mBinding.content.visibility = View.GONE
        }

        // 720
        mBinding.panoramic.visibility = if (item.panoramaUrl.isNullOrEmpty()) View.GONE else View.VISIBLE

        // video
        mBinding.video.visibility = if (item.video.isNullOrEmpty()) View.GONE else View.VISIBLE

        // item
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
    }

    /**
     * 创建标签
     */
    private fun createTagTextView(text:String,textColor:Int = R.color.gray_999999, parent: FlowLayout, needDrawable:Boolean = true) {
        val tv = TextView(parent.context)
        tv.textSize = 11f
        tv.text = text
        tv.textColorResource = textColor
        if(needDrawable){
            val drawable = parent.context.resources.getDrawable(R.drawable.shape_round_gray_d2)
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            tv.setCompoundDrawables(null,null,drawable,null)
            tv.compoundDrawablePadding = 6
        }
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).also {
            it.setMargins(0,0,6,0)
        }
        tv.layoutParams = layoutParams
        parent.addView(tv)
    }
}