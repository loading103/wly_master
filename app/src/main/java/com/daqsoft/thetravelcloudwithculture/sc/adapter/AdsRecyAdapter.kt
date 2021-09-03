package com.daqsoft.thetravelcloudwithculture.sc.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.net.ProviderApi
import com.daqsoft.provider.net.StatisticsRepository
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.ItemHomeModuleBinding
import com.daqsoft.provider.bean.HomeMenu
import com.daqsoft.thetravelcloudwithculture.ui.utils.JumpUtils
import java.lang.Exception

/**
 * @Description
 * @ClassName   AdsRecyAdapter
 * @Author      luoyi
 * @Time        2020/5/20 11:12
 */
class AdsRecyAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private var mContext: Context? = null

    private var datas: MutableList<HomeMenu> = mutableListOf()

    constructor(context: Context, data: MutableList<HomeMenu>) {
        mContext = context
        this.datas = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var binding: ItemHomeModuleBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_home_module,
            parent,
            false
        )
        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var binding: ItemHomeModuleBinding? = DataBindingUtil.getBinding(holder.itemView)
        var position = position
        if (datas.size > 0) {
            position %= datas.size
        }
        binding?.url = datas[position].imgUrl
        binding?.root?.onNoDoubleClick {
            try {
                var hm: HomeMenu? = datas[position]
                hm?.let {
                    if (!hm.name.isNullOrEmpty()) {
                        StatisticsRepository.instance.statistcsModuleClick(hm.name, ProviderApi.REGION_ADV)
                    }
                    JumpUtils.menuPageJumpUtils(it)
                }

            } catch (e: Exception) {
            }

        }
        binding?.executePendingBindings()

    }

    internal class ViewHolder : RecyclerView.ViewHolder {

        constructor(itemView: View) : super(itemView) {

        }
    }

}