package com.daqsoft.usermodule.ui.message.adapter

import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.bean.CommponentDetail
import com.daqsoft.provider.bean.MessageTabBean
import com.daqsoft.provider.bean.MessageTopBean
import com.daqsoft.provider.businessview.adapter.ProviderRecTabAdapter
import com.daqsoft.provider.uiTemplate.component.adapter.ComponentTwoItemCustomAdapter
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ItemMessageTabBinding

/**
 * @Description
 * @ClassName   MessageTabAdapter
 * @Author      luoyi
 * @Time        2020/12/16 14:24
 */
class MessageHdTabAdapter : RecyclerViewAdapter<ItemMessageTabBinding, MessageTopBean>(R.layout.item_message_tab) {

    override fun setVariable(mBinding: ItemMessageTabBinding, position: Int, item: MessageTopBean) {
        mBinding.data=item
        mBinding.flMsgTab.onNoDoubleClick{
            getData().forEachIndexed { index, messageTopBean ->
                messageTopBean.choosed = index==position
                mBinding.tvName.isSelected=index==position
            }
            notifyDataSetChanged()
            onItemClickListener?.onItemClick(position,item)
        }

        when(position){
            0->{
                if(item.choosed){
                    mBinding.imgMsgTab.setImageResource(R.mipmap.mine_notification_hudong_icon_good_selected)
                }else{
                    mBinding.imgMsgTab.setImageResource(R.mipmap.mine_notification_hudong_icon_good_normal)
                }
            }
            1->{
                if(item.choosed){
                    mBinding.imgMsgTab.setImageResource(R.mipmap.mine_notification_hudong_icon_book_selected)
                }else{
                    mBinding.imgMsgTab.setImageResource(R.mipmap.mine_notification_hudong_icon_book_normal)
                }
            }
            2->{
                if(item.choosed){
                    mBinding.imgMsgTab.setImageResource(R.mipmap.mine_notification_hudong_icon_follow_selected)
                }else{
                    mBinding.imgMsgTab.setImageResource(R.mipmap.mine_notification_hudong_icon_follow_normal)
                }
            }
            3->{
                if(item.choosed){
                    mBinding.imgMsgTab.setImageResource(R.mipmap.mine_notification_hudong_icon_ask_selected)
                }else{
                    mBinding.imgMsgTab.setImageResource(R.mipmap.mine_notification_hudong_icon_ask_normal)
                }
            }
            4->{
                if(item.choosed){
                    mBinding.imgMsgTab.setImageResource(R.mipmap.mine_notification_hudong_icon_pk_selected)
                }else{
                    mBinding.imgMsgTab.setImageResource(R.mipmap.mine_notification_hudong_icon_pk_normal)
                }
            }
        }
    }

    var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener{
        fun onItemClick( position:Int,item:MessageTopBean)
    }

}