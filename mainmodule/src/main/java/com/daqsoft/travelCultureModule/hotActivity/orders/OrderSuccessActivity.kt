package com.daqsoft.travelCultureModule.hotActivity.orders

import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainActivityOrderComplateBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.travelCultureModule.hotActivity.bean.OrderSimpleResult
import com.daqsoft.travelCultureModule.net.MainRepository
/**
 * @des 活动预订成功
 * @author PuHua
 * @Date 2020/1/6 10:20
 */

@Route(path = MainARouterPath.MAIN_ACTIVITY_SUCCESS)
class OrderSuccessActivity :
    TitleBarActivity<MainActivityOrderComplateBinding, OrderSuccessActivityViewModel>() {

    @JvmField
    @Autowired
    var orderCode: String = ""

    @JvmField
    @Autowired
    var orderType: String = ""

    @JvmField
    @Autowired
    var faithAuditStatus: String = ""

    @JvmField
    @Autowired
    var isCommentator: Boolean = false
    @JvmField
    @Autowired
    var isOnlyCommentator: Boolean = false

    private var orderId: String = ""

    override fun getLayout(): Int = R.layout.main_activity_order_complate

    override fun setTitle(): String = getString(R.string.main_activity_order_success)

    override fun injectVm(): Class<OrderSuccessActivityViewModel> =
        OrderSuccessActivityViewModel::class.java

    override fun initView() {
        mBinding.tvCheck.onNoDoubleClick {
            checkOrder()
        }
        mModel.orderSimpleResult.observe(this, Observer {

            var orderStr = ""
            if (orderType == ResourceType.CONTENT_TYPE_ACTIVITY) {
                mBinding.tvName.text = "提交成功!"
//                mBinding.notice = "您电子码已经发送到您手机，请查收!"
                if(BaseApplication.appArea=="xj"){
                    mBinding.notice = "电子码已生成，请您查看订单，截图留存预约二维码，或关注“游新疆”公众号，点击游新疆在底部菜单栏我的-我的预约查看预约码"
                }else{
                    mBinding.notice = "您电子码已经发送到您手机，请查收!"
                }

                mBinding.image.setImageResource(getImageResource(1))
            } else if (orderType == ResourceType.CONTENT_TYPE_ACTIVITY_SHIU) {
                if (faithAuditStatus == "1") {
                    mBinding.tvName.text = "预订成功!"
//                    mBinding.notice = "您电子码已经发送到您手机，请查收!"
                    if(BaseApplication.appArea=="xj"){
                        mBinding.notice = "电子码已生成，请您查看订单，截图留存预约二维码，或关注“游新疆”公众号，点击游新疆在底部菜单栏我的-我的预约查看预约码"
                    }else{
                        mBinding.notice = "您电子码已经发送到您手机，请查收!"
                    }
                } else {
                    mBinding.notice = "您的${orderStr}预约信息需要审核，审核通过后我们将用短信的方式告知您，请及时关注短信通知，谢谢!"
                }
                mBinding.image.setImageResource(getImageResource(1))
            } else if (orderType == ResourceType.CONTENT_TYPE_VENUE) {
                if (isCommentator != null) {
                    if (isCommentator) {
                        title = "预约成功"
                        mBinding.tvName.text = "提交成功，请等待审核！"
                        if (isOnlyCommentator) {
                            mBinding.notice = "您的讲解预约需要审核，审核通过后我们将用短信的方式告知您，请及时关注短信通知，谢谢！"
                        } else {
                            mBinding.notice = "您的场馆预约已成功，讲解预约需要审核，审核通过后我们将用短信的方式告知您，请及时关注短信通知，谢谢！"
                        }
                        mBinding.image.setImageResource(getImageResource(2))
                    } else {
                        mBinding.tvName.text = "预约成功！"
                        title = "预约成功"
//                        mBinding.notice = "电子码已发送至您 ${it.phone} 手机，请查收！"

                        if(BaseApplication.appArea=="xj"){
                            mBinding.notice = "电子码已生成，请您查看订单，截图留存预约二维码，或关注“游新疆”公众号，点击游新疆在底部菜单栏我的-我的预约查看预约码"
                        }else{
                            mBinding.notice = "电子码已发送至您 ${it.phone} 手机，请查收！"
                        }
                        mBinding.image.setImageResource(getImageResource(1))
                    }
                } else {
                    mBinding.tvName.text = "预约成功!"
                    title = "预约成功"
//                    mBinding.notice = "电子码已发送至您 ${it.phone} 手机，请查收！"
                    if(BaseApplication.appArea=="xj"){
                        mBinding.notice = "电子码已生成，请您查看订单，截图留存预约二维码，或关注“游新疆”公众号，点击游新疆在底部菜单栏我的-我的预约查看预约码"
                    }else{
                        mBinding.notice = "电子码已发送至您 ${it.phone} 手机，请查收！"
                    }

                    mBinding.image.setImageResource(getImageResource(1))
                }
            } else if (orderType == ResourceType.CONTENT_TYPE_SCENERY) {
                mBinding.tvName.text = "预约成功！"
                title = "预约成功"
//                mBinding.notice = "电子码已发送至您 ${it.phone} 手机，请查收！"
                if(BaseApplication.appArea=="xj"){
                    mBinding.notice = "电子码已生成，请您查看订单，截图留存预约二维码，或关注“游新疆”公众号，点击游新疆在底部菜单栏我的-我的预约查看预约码"
                }else{
                    mBinding.notice = "电子码已发送至您 ${it.phone} 手机，请查收！"
                }

                mBinding.image.setImageResource(getImageResource(1))
            }
            mBinding.date = it.createTime

            orderId = it.id.toString()
        })

    }

    override fun initData() {
        mModel.checkSimpleResult(orderCode)
    }

    /**
     * 查看订单
     */
    fun checkOrder() {
        if (!orderId.isNullOrEmpty()) {
            if (orderType == ResourceType.CONTENT_TYPE_VENUE) {
                // 去到订单详情
                ARouter.getInstance().build(ARouterPath.UserModule.USER_ORDER_DETAIL)
                    .withString("orderId", orderId)
                    .withString("type", orderType)
                    .navigation()
            } else {
                // 去到订单详情
                ARouter.getInstance().build(ARouterPath.UserModule.USER_ORDER_DETAIL)
                    .withString("orderId", orderId)
                    .withString("type", orderType)
                    .navigation()
            }
            finish()
        } else {
            ToastUtils.showMessage("请稍等，服务器正在处理~")
        }
    }

    /**
     * 继续浏览
     */
    fun countinue(v: View) {
        finish()
        AppManager.instance.gotoHomeActivity()
    }

    /**
     * 获取不同状态下的图片
     * @param type Int 0 失败 1 成功 2 审核
     * @return Int 图片资源id
     */
    private fun getImageResource(type:Int):Int{
        return if (BaseApplication.appArea == "sc"){
            when(type){
                0->R.mipmap.common_failed
                1->R.mipmap.common_success
                2->R.mipmap.common_under_review
                else->R.mipmap.common_success
            }
        }else{
            R.mipmap.success
        }
    }
}


/**
 * @des 座位选择的viewmodel
 * @author PuHua
 * @Date 2020/1/12 16:03
 */
class OrderSuccessActivityViewModel : BaseViewModel() {

    val orderSimpleResult = MediatorLiveData<OrderSimpleResult>()

    /**
     * 查看简要信息
     */
    fun checkSimpleResult(orderId: String) {

        MainRepository.service.getOrderResult(orderId)
            .excute(object : BaseObserver<OrderSimpleResult>(mPresenter) {
                override fun onSuccess(response: BaseResponse<OrderSimpleResult>) {
                    if (response.code == 0) {
                        orderSimpleResult.postValue(response.data)
                    }
                }
            })
    }
}

