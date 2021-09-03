package com.daqsoft.provider.businessview.adapter

import android.annotation.SuppressLint
import android.view.View
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.ChooseListBean
import com.daqsoft.provider.databinding.ItemSingleResultTextBinding
import kotlin.math.roundToInt


internal class SingleChooseResultAdapter(val totleNumber:String) : RecyclerViewAdapter<ItemSingleResultTextBinding, ChooseListBean>(R.layout.item_single_result_text) {

    @SuppressLint("CheckResult")
    override fun setVariable(mBinding: ItemSingleResultTextBinding, position: Int, item: ChooseListBean) {
        mBinding.datas=item

        // 设置进度条百分比
//        val progress = (item.joinCount.toFloat() / totleNumber.toInt() * 100).toInt()

        val progress = (item.joinCount.toFloat() / totleNumber.toInt() * 100).roundToInt()
        if(item.userCheck){
            mBinding.llBar2.visibility=View.VISIBLE
            mBinding.progressBar2.isEnabled=false
            mBinding.progressBar2.progress= 5.coerceAtLeast(progress)
            mBinding.tvProgress2.text="$progress%"
            mBinding.llBar1.visibility=View.GONE

        }else{
            mBinding.llBar2.visibility=View.GONE
            mBinding.llBar1.visibility=View.VISIBLE
            mBinding.progressBar1.progress=progress
            mBinding.tvProgress1.text="$progress%"
        }

        when(position){
            0->   mBinding.tvChoose.text="A."
            1->   mBinding.tvChoose.text="B."
            2->   mBinding.tvChoose.text="C."
            3->   mBinding.tvChoose.text="D."
            4->   mBinding.tvChoose.text="E."
            5->   mBinding.tvChoose.text="F."
            6->   mBinding.tvChoose.text="G."
            7->   mBinding.tvChoose.text="H."
            8->   mBinding.tvChoose.text="I."
            9->   mBinding.tvChoose.text="J."
            10->   mBinding.tvChoose.text="K."
            11->   mBinding.tvChoose.text="L."
            12->   mBinding.tvChoose.text="M."
            13->   mBinding.tvChoose.text="N."
            14->   mBinding.tvChoose.text="O."
            15->   mBinding.tvChoose.text="P."
        }
    }



}