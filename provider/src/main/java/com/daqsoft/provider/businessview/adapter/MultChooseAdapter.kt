package com.daqsoft.provider.businessview.adapter

import android.annotation.SuppressLint
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.ChooseListBean
import com.daqsoft.provider.databinding.ItemSingleTextBinding


internal class MultChooseAdapter() : RecyclerViewAdapter<ItemSingleTextBinding, ChooseListBean>(R.layout.item_single_text) {


    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemSingleTextBinding, position: Int, item: ChooseListBean) {
        mBinding.datas=item
        mBinding.tvChoose.isSelected= item.haveChoosed
        mBinding.llRoot.onNoDoubleClick {
            item.haveChoosed=!item.haveChoosed
            notifyDataSetChanged()
        }
        when(position){
            0->   mBinding.tvChoose.text="A"
            1->   mBinding.tvChoose.text="B"
            2->   mBinding.tvChoose.text="C"
            3->   mBinding.tvChoose.text="D"
            4->   mBinding.tvChoose.text="E"
            5->   mBinding.tvChoose.text="F"
            6->   mBinding.tvChoose.text="G"
            7->   mBinding.tvChoose.text="H"
            8->   mBinding.tvChoose.text="I"
            9->   mBinding.tvChoose.text="J"
            10->   mBinding.tvChoose.text="K"
            11->   mBinding.tvChoose.text="L"
            12->   mBinding.tvChoose.text="M"
            13->   mBinding.tvChoose.text="N"
            14->   mBinding.tvChoose.text="O"
            15->   mBinding.tvChoose.text="P"

        }
    }



}