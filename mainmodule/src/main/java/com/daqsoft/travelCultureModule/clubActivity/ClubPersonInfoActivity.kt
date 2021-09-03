package com.daqsoft.travelCultureModule.clubActivity

import android.text.Html
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.adapter.setCenterCropImage
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityClubPersonInfoBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.daqsoft.travelCultureModule.clubActivity.vm.ClubPersonInfoViewModel

@Route(path = MainARouterPath.MAIN_CLUB_PERSON_INFO)
class ClubPersonInfoActivity : TitleBarActivity<ActivityClubPersonInfoBinding, ClubPersonInfoViewModel>() {
    override fun getLayout(): Int = R.layout.activity_club_person_info

    override fun setTitle(): String = "成员详情"

    override fun injectVm(): Class<ClubPersonInfoViewModel> = ClubPersonInfoViewModel::class.java
    /**
     * 分享弹窗
     */
    var sharePopWindow: SharePopWindow? = null

    var id: String = ""

    private var name="社团成员信息"
    private var headUrl=""

    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(View.OnClickListener {
                if (sharePopWindow == null) {
                    sharePopWindow = SharePopWindow(this)
                }
                // 设置分享数据
                var content= Constant.SHARE_DEC
                sharePopWindow?.setShareContent(
                   name , "点击查看详情", headUrl,
                    ShareModel.getClubMemberDesc(id)
                )
                if (!sharePopWindow!!.isShowing) {
                    sharePopWindow?.showAsDropDown(mTitleBar)
                }
        })
    }
    override fun initView() {

    }

    override fun initData() {
        id = intent.getStringExtra("id")
        mModel.getPersonInfo(id)
        mModel.personInfo.observe(this, Observer {
            if (it != null) {
                mBinding.tvCiPiName.text = it.name
                if(it.name?.isNotEmpty()){
                    name=it.name
                    headUrl=it.image
                }
                mBinding.tvCiPiDuty.text = it.duty
                mBinding.webCiPiContent.settings.javaScriptEnabled = true
                mBinding.webCiPiContent.loadDataWithBaseURL(null, StringUtil.getHtml(it.introduce),"text/html","utf-8",null)
                setCenterCropImage(
                    mBinding.tvCiPiHead, it.image, AppCompatResources.getDrawable(
                        BaseApplication.context, R.drawable.mine_profile_photo_default
                    ), true
                )
            }
        })
    }
}