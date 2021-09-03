package com.daqsoft.thetravelcloudwithculture.ui.adapter

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.vlayout.LayoutHelper
import com.bumptech.glide.Glide
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.CommonTemlate
import com.daqsoft.provider.uiTemplate.BaseDelegateAdapter
import com.daqsoft.provider.uiTemplate.TemplateType
import com.daqsoft.thetravelcloudwithculture.R
import com.daqsoft.thetravelcloudwithculture.databinding.ItemMineMenuStyleBinding
import com.daqsoft.thetravelcloudwithculture.databinding.ItemMineMoudleRealNameBinding
import com.daqsoft.thetravelcloudwithculture.databinding.ItemMineTopMenuTemplateBinding
import org.jetbrains.anko.toast

/**
 * @ClassName    RealNameAdapter
 * @Description  实名认证
 * @Author       yuxc
 * @CreateDate   2020/11/13
 */
class RealNameAdapter(helper: LayoutHelper) : BaseDelegateAdapter<ItemMineMoudleRealNameBinding>(helper, R.layout.item_mine_moudle_real_name) {

    override fun bindDataToView(mBinding: ItemMineMoudleRealNameBinding, position: Int) {

        when (SPUtils.getInstance().getInt(SPKey.REALNAMESTATUS)) {
            6 -> {
                mBinding.tvNoRealName.visibility = View.GONE
                mBinding.tvRealName.visibility = View.VISIBLE
                mBinding.tvRealName.text = "已认证"
                mBinding.tvRealName.setCompoundDrawablesWithIntrinsicBounds(mBinding.root.context.resources.getDrawable(R.mipmap.mine_smrz_icon_success), null, null, null)
            }
            4 -> {
                mBinding.tvNoRealName.visibility = View.GONE
                mBinding.tvRealName.visibility = View.VISIBLE
                mBinding.tvRealName.text = "审核中"
                mBinding.tvRealName.setCompoundDrawablesWithIntrinsicBounds(mBinding.root.context.resources.getDrawable(R.mipmap.mine_smrz_icon_shenhe), null, null, null)
            }
            79 -> {
                mBinding.tvNoRealName.visibility = View.GONE
                mBinding.tvRealName.visibility = View.VISIBLE
                mBinding.tvRealName.text = "审核不通过"
                mBinding.tvRealName.setCompoundDrawablesWithIntrinsicBounds(mBinding.root.context.resources.getDrawable(R.mipmap.mine_smrz_icon_failed), null, null, null)
            }
            8 -> {
                mBinding.tvNoRealName.visibility = View.GONE
                mBinding.tvRealName.visibility = View.VISIBLE
                mBinding.tvRealName.text = "已撤回"
                mBinding.tvRealName.setCompoundDrawablesWithIntrinsicBounds(mBinding.root.context.resources.getDrawable(R.mipmap.mine_smrz_icon_cancel), null, null, null)
            }
            else -> {
                mBinding.tvNoRealName.visibility = View.VISIBLE
                mBinding.tvRealName.visibility = View.GONE
            }
        }

        mBinding.root.onNoDoubleClick {
            if(AppUtils.isLogin()){


                when (SPUtils.getInstance().getInt(SPKey.REALNAMESTATUS)) {
                    6 -> {
                        // 已实名
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.AUTHENTICATE_COMPLETE_ACTIVITY)
                            .navigation()
                    }
                    4 -> {
                        // 待审核
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.AUTHENTICATE_REVIEW_ACTIVITY)
                            .navigation()
                    }
                    79 -> {
                        // 审核未通过
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.AUTHENTICATE_NOT_PASS_ACTIVITY)
                            .navigation()
                    }
                    8 -> {
                        // 已经撤回消息
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.AUTHENTICATE_NOT_PASS_ACTIVITY)
                            .withBoolean("isDraw",true)
                            .navigation()
                    }
                    else -> {
                        // 未实名，审核已撤回
                        ARouter.getInstance()
                            .build(ARouterPath.UserModule.AUTHENTICATE_COMMIT_ACTIVITY)
                            .navigation()
                    }
                }
            }else{
                ToastUtils.showUnLoginMsg()
                ARouter.getInstance()
                    .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                    .navigation()
            }
        }
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun getItemViewType(position: Int): Int {
        return TemplateType.MINE_REAL_NAME
    }
}