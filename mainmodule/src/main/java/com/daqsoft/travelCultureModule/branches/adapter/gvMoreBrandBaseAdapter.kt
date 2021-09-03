package com.daqsoft.travelCultureModule.branches.adapter

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
import com.daqsoft.provider.bean.HomeBranchBean

class gvMoreBrandBaseAdapter(listData:MutableList<HomeBranchBean>): BaseAdapter() {
    var list_more=listData
    override fun getView(postion: Int, view: View?, p2: ViewGroup?): View? {
        val view = LayoutInflater.from(BaseApplication.context).inflate(R.layout.item_brand_more_brand, p2, false)
        view.setOnClickListener {
            ARouter.getInstance().build(MainARouterPath.MAIN_BRANCH_DETAIL)
                .withString("id", list_more.get(postion).id)
                .navigation()
        }
        var ivLogo=view.findViewById<ImageView>(R.id.iv_ib_more_logo)

        var tvName=view.findViewById<TextView>(R.id.tv_ib_more_name)



        setImageUrlqwx(ivLogo,list_more.get(postion).brandImage, AppCompatResources.getDrawable(
            BaseApplication.context, R.drawable.placeholder_img_fail_240_180),5)

        tvName.text= list_more.get(postion).name

        return view

    }

    override fun getItem(p0: Int): Any {
        return p0
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return list_more.size
    }


}