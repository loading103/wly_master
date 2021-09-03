package com.daqsoft.provider.uiTemplate.menu.topMenu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.vlayout.DelegateAdapter
import com.alibaba.android.vlayout.LayoutHelper
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.bean.HomeMenu
import com.daqsoft.provider.databinding.ItemTopMenuTemplateBinding
import com.daqsoft.provider.uiTemplate.BaseDelegateAdapter
import com.daqsoft.provider.uiTemplate.TemplateType
import com.daqsoft.provider.view.convenientbanner.ConvenientBanner
import com.daqsoft.provider.view.convenientbanner.holder.CBViewHolderCreator
import com.daqsoft.provider.view.convenientbanner.holder.Holder
import com.daqsoft.provider.view.convenientbanner.listener.OnPageChangeListener
import retrofit2.http.HEAD
import java.lang.ref.SoftReference

/**
 * @Description
 * @ClassName   TopMenuAdapter
 * @Author      luoyi
 * @Time        2020/10/10 15:01
 */
class TopMenuAdapter(val helper: LayoutHelper) :
    DelegateAdapter.Adapter<RecyclerView.ViewHolder>() {
    var menus: MutableList<CommonTemlate> = mutableListOf()
    var fragmentManger: SoftReference<FragmentManager>? = null

    override fun getItemCount(): Int {
        return 1
    }

    fun addAll(datas: MutableList<CommonTemlate>) {
        menus.clear()
        menus.addAll(datas)
    }

    override fun getItemViewType(position: Int): Int {
        return TemplateType.TOP_MENU
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TopMenuViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_top_menu_template, parent, false
            )
        )
    }

    override fun onCreateLayoutHelper(): LayoutHelper {
        return helper
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TopMenuViewHolder) {
            holder.fragmentManger = fragmentManger
            holder.bindDataToUI(menus)
        }

    }
}