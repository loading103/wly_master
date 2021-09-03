package com.daqsoft.usermodule.ui.message

import android.text.TextUtils
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.NoticeDetailBean
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.utils.WebViewUtils
import com.daqsoft.provider.view.BaseDialog
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.MessageNoticeDetailBinding


/**
 * @Description 通知详情页面
 * @Author      PuHua
 */
@Route(path =  ARouterPath.UserModule.USER_MEASSAGE_NOTICE_DETAIL)
class NoticeDetailActivity : TitleBarActivity<MessageNoticeDetailBinding, NoticeDetailActivity.NoticeDetailViewModel>() {

    @JvmField
    @Autowired
    var id: String = ""

    override fun getLayout(): Int = R.layout.message_notice_detail

    override fun setTitle(): String = "通知详情"

    override fun injectVm(): Class<NoticeDetailViewModel> = NoticeDetailViewModel::class.java


    var sharetitle: String = "通知详情"

    var shareContent: String = "点击查看详情"

    var shareUrl: String = ""
    /**
     * 分享弹窗
     */
    var sharePopWindow: SharePopWindow? = null

    override fun initView() {
        initViewModel()

    }

    private fun initViewModel() {
        mModel.datas.observe(this, Observer {
            if(it!=null){
                mBinding.data=it
                sharetitle=it.title
                shareUrl=it.coverImage
                if(!TextUtils.isEmpty(it.describes)){
                    shareContent=it.describes
                }

//                if (!it.content.isNullOrEmpty() && it.content.contains("frame")) {
//                    WebViewUtils.pptWeb(mBinding.tvContent, it.content,null,this@NoticeDetailActivity)
//                } else {
                    val html = StringUtil.getHtml(" <html><header><style type=\"text/css\">body{word-wrap:break-word;}</style></header>" + it.content + "</html>")
                    WebViewUtils.pptWeb(mBinding.tvContent,it.content,html,this@NoticeDetailActivity)
//                }
            }
        })
    }

    override fun initData() {
        if(TextUtils.isEmpty(id)){
            return
        }
        mModel.getNoticeDetailDatas(id)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(View.OnClickListener {
                if (sharePopWindow == null) {
                    sharePopWindow = SharePopWindow(this@NoticeDetailActivity)
                }
                // 设置分享数据
                sharePopWindow?.setShareContent(
                    sharetitle, shareContent, shareUrl,
                    ShareModel.getNoticeDesc(id)
                )
                if (!sharePopWindow!!.isShowing) {
                    sharePopWindow?.showAsDropDown(mTitleBar)
                }
        })
    }

    class NoticeDetailViewModel : BaseViewModel() {
        val datas = MutableLiveData<NoticeDetailBean>()
        /**
         * 获取通知详请数据
         */
        fun getNoticeDetailDatas(id:String) {
            mPresenter?.value?.loading = true
            UserRepository.userService.getMessageNoticeDetail(id)
                .excute(object : BaseObserver<NoticeDetailBean>(mPresenter) {
                    override fun onSuccess(response: BaseResponse<NoticeDetailBean>) {
                        datas.postValue(response.data)
                    }
                })
        }
    }
}