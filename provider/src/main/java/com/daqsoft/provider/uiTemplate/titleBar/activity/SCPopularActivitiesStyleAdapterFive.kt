package com.daqsoft.provider.uiTemplate.titleBar.activity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.provider.R
import com.daqsoft.provider.databinding.ItemHomePopularActivityFiveBinding
import com.daqsoft.provider.databinding.ItemHomePopularActivityScStyleFourBinding

/**
 * @Description
 * @ClassName   SCPopularActivitiesStyleAdapterFive
 * @Author      luoyi
 * @Time        2020/12/9 9:10
 */
class SCPopularActivitiesStyleAdapterFive : SCPopularActivitiesStyleBaseAdapter<SCPopularActivitiesStyleAdapterFive.RecyclerViewItemHolder>() {

    inner class RecyclerViewItemHolder(val binding: ItemHomePopularActivityFiveBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewItemHolder {
        val mBinding = DataBindingUtil.inflate<ItemHomePopularActivityFiveBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_home_popular_activity_five,
            parent,
            false
        )
        return RecyclerViewItemHolder(mBinding)
    }

    override fun onBindViewHolder(holder: RecyclerViewItemHolder, position: Int) {
        bindDataToView(holder.binding,position)
    }

    @SuppressLint("SetTextI18n", "CutPasteId")
    private fun bindDataToView(mBinding:ItemHomePopularActivityFiveBinding, position: Int) {

        val item = menus[position]

    }
}