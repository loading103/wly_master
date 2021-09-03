package com.daqsoft.travelCultureModule.branches.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.setImageUrlqwx
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.mainmodule.R
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.BrandSiteInfo
import com.daqsoft.provider.bean.ScenicBean

/**
 * 品牌景区玩乐
 */
class gvBaseAdapter(listData: MutableList<BrandSiteInfo>, type: String) : BaseAdapter() {
    var list_scenic = listData
    var type_scenic = type
    override fun getView(postion: Int, view: View?, p2: ViewGroup?): View? {
        val view = LayoutInflater.from(BaseApplication.context).inflate(R.layout.item_brand_scenic, p2, false)
        view.setOnClickListener {
            ARouter.getInstance().build(MainARouterPath.MAIN_SCENIC_DETAIL)
                .withString("id", list_scenic.get(postion).resourceId.toString())
                .navigation()
        }
        var ivLogo = view.findViewById<ImageView>(R.id.iv_ib_logo)
        var ivViedo = view.findViewById<ImageView>(R.id.iv_ib_video)
        var ivGoldStory = view.findViewById<ImageView>(R.id.iv_ib_goldstory)
        var iv720 = view.findViewById<ImageView>(R.id.iv_ib_720)

        var tvLevel = view.findViewById<TextView>(R.id.tv_ib_level)
        var tvName = view.findViewById<TextView>(R.id.tv_ib_name)
        var tvContent = view.findViewById<TextView>(R.id.tv_ib_content)
        if (type_scenic == "1") {
            tvName.setTextColor(Color.parseColor("#333333"))
            tvContent.setTextColor(Color.parseColor("#999999"))
        }
        var logo_url = list_scenic.get(postion).images
        if (logo_url != "") {
            logo_url = logo_url.split(",")[0]
        }
        setImageUrlqwx(
            ivLogo, logo_url, AppCompatResources.getDrawable(
                BaseApplication.context, R.drawable.placeholder_img_fail_240_180
            ), 5
        )
        if (list_scenic.get(postion).videoUrl == "") {
            ivViedo.visibility = View.GONE
        }
        if (list_scenic.get(postion).commentaryUrl == "") {
            ivGoldStory.visibility = View.GONE
        }
        if (list_scenic.get(postion).fullViewUrl == "") {
            iv720.visibility = View.GONE
        }
        if(!list_scenic[postion].labelName.isNullOrEmpty()) {
            tvLevel.text = list_scenic.get(postion).labelName
        }
        tvName.text = list_scenic.get(postion).name
        tvContent.text = list_scenic.get(postion).regionName
        return view

    }

    override fun getItem(p0: Int): Any {
        return p0
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return list_scenic.size
    }


}