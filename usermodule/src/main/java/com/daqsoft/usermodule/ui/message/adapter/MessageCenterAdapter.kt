package com.daqsoft.usermodule.ui.message.adapter

import android.text.TextUtils
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.provider.ARouterPath
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ItemMessageCenterListBinding
import com.daqsoft.provider.bean.MessageRootBean

/**
 * @Description
 * @ClassName   MessageCenterAdapter
 * @Author      luoyi
 * @Time        2020/12/16 13:54
 */
class MessageCenterAdapter : RecyclerViewAdapter<ItemMessageCenterListBinding, MessageRootBean>(R.layout.item_message_center_list) {

    override fun setVariable(mBinding: ItemMessageCenterListBinding, position: Int, item: MessageRootBean) {
        mBinding.data = item
        mBinding.llRoot.setOnClickListener {
            when(position){
                0->{
                    ARouter.getInstance()
                        .build(ARouterPath.UserModule.USER_MEASSAGE_LIST_DETAIL)
                        .withString("classify",item.classify)
                        .withString("selected",item.selected)
                        .navigation()
                }
                1->{
                    ARouter.getInstance()
                        .build(ARouterPath.UserModule.USER_MEASSAGE_HD_LIST_DETAIL)
                        .withString("classify",item.classify)
                        .withString("selected",item.selected)
                        .navigation()
                }
                2,3,4->{
                    ARouter.getInstance()
                        .build(ARouterPath.UserModule.USER_MEASSAGE_QT_LIST_DETAIL)
                        .withString("classify",item.classify)
                        .navigation()
                }

            }
        }
    }
}