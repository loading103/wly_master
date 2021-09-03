package com.daqsoft.travelCultureModule.country.adapter

/**
 * desc :
 * @author 江云仙
 * @date 2020/4/29 18:31
 */

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
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.BrandMDD


class CountryCityAdapter(listData:MutableList<BrandMDD>): BaseAdapter() {
    var list_mdd=listData
    override fun getView(postion: Int, view: View?, p2: ViewGroup?): View? {
        val view = LayoutInflater.from(BaseApplication.context).inflate(R.layout.item_mdd_list, p2, false)
        view.setOnClickListener {
            // 乡村游
            ARouter.getInstance().build(ARouterPath.CountryModule.COUNTRY_TOUR_LIST)
                .withString("jumpSiteId",list_mdd[postion].id.toString())
                .navigation()
        }
        var ivLogo=view.findViewById<ImageView>(R.id.iv_mdd_logo)
        var ivPlay=view.findViewById<ImageView>(R.id.iv_mdd_play)
        var tvSign=view.findViewById<TextView>(R.id.tv_mdd_sign)
        var tvName=view.findViewById<TextView>(R.id.tv_mdd_name)
        var tvIntroduce=view.findViewById<TextView>(R.id.tv_mdd_introduce)
        var tvContent=view.findViewById<TextView>(R.id.tv_mdd_content)
        if(list_mdd.get(postion).images!="") {
            setImageUrlqwx(
                ivLogo, list_mdd.get(postion).images.split(",")[0], AppCompatResources.getDrawable(
                    BaseApplication.context, R.drawable.placeholder_img_fail_240_180), 5)
        }else{
            setImageUrlqwx(
                ivLogo, list_mdd.get(postion).images, AppCompatResources.getDrawable(
                    BaseApplication.context, R.drawable.placeholder_img_fail_240_180), 5)
        }
        if(list_mdd.get(postion).videoUrl==""){
            ivPlay.visibility=View.GONE
        }
        if(list_mdd.get(postion).siteLabelNames==""){
            tvSign.visibility=View.INVISIBLE
        }else{
            tvSign.visibility=View.VISIBLE
            tvSign.text=list_mdd.get(postion).siteLabelNames
        }
        tvName.text= list_mdd.get(postion).name
        if(list_mdd.get(postion).slogan=="")
            tvIntroduce.visibility=View.GONE
        tvIntroduce.text=list_mdd.get(postion).slogan
        if(list_mdd.get(postion).scenicNameStr.length>=20) {
            tvContent.text = list_mdd.get(postion).scenicNameStr.subSequence(0, 20).toString() + "...等" + list_mdd.get(postion).scenicCount + "个玩乐点"
        }else if(list_mdd.get(postion).scenicNameStr==""){
            // tvContent.visibility=View.GONE
        }else{
            tvContent.text = list_mdd.get(postion).scenicNameStr  + list_mdd.get(postion).scenicCount + "个玩乐点"
        }
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