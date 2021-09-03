package com.daqsoft.travelCultureModule.branches.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.alibaba.android.arouter.launcher.ARouter

import com.daqsoft.baselib.adapter.setImageUrl
import com.daqsoft.baselib.adapter.setImageUrlqwx
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.mainmodule.R
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.BrandSiteInfo
import com.daqsoft.provider.bean.ScenicBean

class gvMDDBaseAdapter(listData: MutableList<BrandSiteInfo>) : BaseAdapter() {
    var list_mdd = listData
    override fun getView(postion: Int, view: View?, p2: ViewGroup?): View? {
        val view = LayoutInflater.from(BaseApplication.context).inflate(R.layout.item_brand_mdd, p2, false)
        // 目的地城市名片
        view.setOnClickListener {
            ARouter.getInstance().build(MainARouterPath.MAIN_MDD_CITY_INFO)
                .withString("id", list_mdd.get(postion).resourceId.toString())
                .navigation()
        }
        var ivLogo = view.findViewById<ImageView>(R.id.iv_ib_mdd_logo)

        var tvName = view.findViewById<TextView>(R.id.tv_ib_mdd_name)
        var images =""
        if(list_mdd[postion].images.isNotEmpty()){
             images = list_mdd[postion].images.split(",") [0]
        }
        setImageUrlqwx(
            ivLogo, images, AppCompatResources.getDrawable(
                BaseApplication.context, R.drawable.placeholder_img_fail_240_180
            ), 5
        )

        tvName.text = list_mdd.get(postion).name

        return view

    }

    override fun getItem(p0: Int): Any {
        return p0
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return list_mdd.size
    }


}