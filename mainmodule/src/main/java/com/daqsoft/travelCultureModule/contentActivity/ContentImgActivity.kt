package com.daqsoft.travelCultureModule.contentActivity

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Observer
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.setImageUrl
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityContentImgBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.businessview.fragment.ProviderAddCommentFragment
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.daqsoft.provider.net.StatisticsRepository
import com.daqsoft.travelCultureModule.contentActivity.bean.ContentInfoBean
import com.daqsoft.travelCultureModule.contentActivity.bean.ImageUrl
import com.daqsoft.travelCultureModule.contentActivity.vm.ContentActivityViewModel
import com.umeng.socialize.UMShareAPI
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.toast

/**
 * 咨询图片详情
 */
@Route(path = MainARouterPath.MAIN_CONTENT_IMG)
class ContentImgActivity : TitleBarActivity<ActivityContentImgBinding, ContentActivityViewModel>() {
    lateinit var contentId: String
    var viewList = ArrayList<View>()
    var mList = ArrayList<ImageUrl>()
    lateinit var cur_bean: ContentInfoBean

    /**
     * 分享弹窗
     */
    var sharePopWindow: SharePopWindow? = null
    private var addCommentPopFragment: ProviderAddCommentFragment? = null
    override fun getLayout(): Int = R.layout.activity_content_img

    override fun setTitle(): String = "资讯详情"

    override fun injectVm(): Class<ContentActivityViewModel> = ContentActivityViewModel::class.java
    var adapter = object : PagerAdapter() {
        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(viewList[position])
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val imageView = ImageView(this@ContentImgActivity)
            //  var param = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP)
            //    imageView.layoutParams=param
            setImageUrl(
                imageView, mList[position].url, AppCompatResources.getDrawable(
                    BaseApplication.context, R.drawable.placeholder_img_fail_240_180
                ), 1
            )
            container.addView(imageView)
            viewList.add(imageView)
            return imageView
        }

        override fun getCount(): Int {
            return mList.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }
    }

    override fun initView() {
        EventBus.getDefault().register(this)
        contentId = intent.getStringExtra("id")
        mBinding.tvLikeNum.setOnClickListener {
            if (gotoLogin()) {
                if (cur_bean != null && cur_bean.vipResourceStatus.likeStatus) {
                    mModel.check_dislike(contentId, ResourceType.CONTENT)
                    cur_bean.vipResourceStatus.likeStatus = false
                    checkBoomlike(false)
                    cur_bean.likeNum = cur_bean.likeNum - 1
                    mBinding.tvLikeNum.text = cur_bean.likeNum.toString()
                } else if (cur_bean != null && !cur_bean.vipResourceStatus.likeStatus) {
                    mModel.check_like(contentId, ResourceType.CONTENT)
                    cur_bean.vipResourceStatus.likeStatus = true
                    checkBoomlike(true)
                    cur_bean.likeNum = cur_bean.likeNum + 1;
                    mBinding.tvLikeNum.text = cur_bean.likeNum.toString()
                }
            }
        }
        mBinding.tvCollectNum.setOnClickListener {
            if (gotoLogin()) {
                if (cur_bean != null && cur_bean!!.vipResourceStatus.collectionStatus) {
                    mModel.check_discolect(contentId, ResourceType.CONTENT)
                    cur_bean.vipResourceStatus.collectionStatus = false
                    checkBoomColect(false)
                    cur_bean.collectionNum = cur_bean.collectionNum - 1;
                    mBinding.tvCollectNum.text = cur_bean.collectionNum.toString()
                } else if (cur_bean != null && !cur_bean!!.vipResourceStatus.collectionStatus) {
                    mModel.check_colect(contentId, ResourceType.CONTENT)
                    cur_bean.vipResourceStatus.collectionStatus = true
                    checkBoomColect(true)
                    cur_bean.collectionNum = cur_bean.collectionNum + 1;
                    mBinding.tvCollectNum.text = cur_bean.collectionNum.toString()
                }
            }
        }

        mBinding.tvShare.onNoDoubleClick {
            toast("分享功能开发中")
        }


        mBinding.tvComtentSay.setOnClickListener {
            if (gotoLogin()) {
                // 获取焦点
//                mBinding.etContentSay.setFocusable(true);
//                mBinding.etContentSay.setFocusableInTouchMode(true);
//                mBinding.etContentSay.requestFocus();
//                // 弹出软键盘
//                var imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                imm.showSoftInput(mBinding.etContentSay, 0);
//                mBinding.llContentLike.visibility = View.GONE
//                mBinding.llContentComent.visibility = View.VISIBLE
                showCommentPopup()
            }
        }
        //发送评论
        mBinding.tvContentSend.setOnClickListener {
            mBinding.llContentLike.visibility = View.VISIBLE
            mBinding.llContentComent.visibility = View.GONE
            var imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(mBinding.etContentSay.getWindowToken(), 0);
            var content = mBinding.etContentSay.text.toString()
            if (!content.equals("")) {
                mModel.post_comment(contentId, ResourceType.CONTENT, content)
                mBinding.etContentSay.setText("")
            }

        }
        mModel.message_comment.observe(this@ContentImgActivity, Observer {
            toast(it)
        })
        mModel.zixunInfo.observe(this@ContentImgActivity, Observer {
            if (it != null) {
                cur_bean = it
                StatisticsRepository.instance.statisticsContent(it?.title)
                mBinding.tvComment.apply {
                    cur_bean.let {
                        text = "${cur_bean.commentNum}"
                        onNoDoubleClick {
                            ARouter.getInstance()
                                .build(MainARouterPath.MAIN_CONTENT_COMMENT_LIST)
                                .withString("infoId", cur_bean.id.toString())
                                .navigation()
                        }
                    }
                }

                mBinding.tvLikeNum.text = it.likeNum.toString()
                mBinding.tvCollectNum.text = it.collectionNum.toString()
                checkBoomlike(it.vipResourceStatus.likeStatus)
                checkBoomColect(it.vipResourceStatus.collectionStatus)
                var imges = it.imageUrls
                for (value in imges.indices) {
                    if (value == 0) {
                        mBinding.tvCiImgTitle.text = imges[value].name
                        mBinding.tvCiImgContent.text = imges[value].description
                        if (imges[value].resourceName.isNotEmpty()) {
                            mBinding.tvImgTitle.visibility = View.VISIBLE
                            mBinding.tvImgTitle.text = imges[value].resourceName
                        } else {
                            mBinding.tvImgTitle.visibility = View.GONE
                        }
                    }
                    mList.add(imges[value] as ImageUrl)
                }
                mBinding.tvCiImgCur.text = "1"
                mBinding.tvCiImgAll.text = "/" + mList.size.toString()
                adapter.notifyDataSetChanged()
            }
        })
        mBinding.vpCiiImg.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                mBinding.tvCiImgCur.text = (position + 1).toString()

            }

            override fun onPageSelected(position: Int) {
                mBinding.tvCiImgTitle.text = mList[position].name
                mBinding.tvCiImgContent.text = mList[position].description
                if (mList[position].resourceName.isNotEmpty()) {
                    mBinding.tvImgTitle.visibility = View.VISIBLE
                    mBinding.tvImgTitle.text = mList[position].resourceName
                } else {
                    mBinding.tvImgTitle.visibility = View.GONE
                }
            }

        })
        mBinding.vpCiiImg.adapter = adapter
    }

    fun checkBoomlike(like: Boolean) {
        if (like) {
            mBinding.tvLikeNum.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                resources.getDrawable(R.mipmap.main_bottom_icon_like_selected), null, null
            )
        } else {
            mBinding.tvLikeNum.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                resources.getDrawable(R.mipmap.main_bottom_icon_like_normal), null, null
            )
        }
    }

    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(object : View.OnClickListener {
            override fun onClick(v: View?) {
                cur_bean?.let {
                    if (sharePopWindow == null) {
                        sharePopWindow = SharePopWindow(this@ContentImgActivity)
                    }
                    sharePopWindow?.setShareContent(
                        it.title, if (it.content.isNullOrEmpty()) {
                            "点击查看详情"
                        } else {
                            it.summary
                        }, if (it.imageUrls.isNullOrEmpty()) {
                            ""
                        } else {
                            "" + it.imageUrls[0]
                        },
                        ShareModel.getPicZXDesc(contentId)
                    )
                    if (!sharePopWindow!!.isShowing) {
                        sharePopWindow?.showAsDropDown(mTitleBar)
                    }
                }
            }

        })
    }

    /**
     * 显示弹窗
     */
    private fun showCommentPopup() {
        if (addCommentPopFragment == null) {
            addCommentPopFragment =
                ProviderAddCommentFragment.newInstance(contentId ?: "", ResourceType.CONTENT)
        }
        if (!addCommentPopFragment!!.isAdded) {
            addCommentPopFragment?.show(supportFragmentManager, "story_add_comment")
        }
    }

    fun checkBoomColect(colect: Boolean) {
        if (colect) {
            mBinding.tvCollectNum.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                resources.getDrawable(R.mipmap.main_bottom_icon_collect_selected), null, null
            )
        } else {
            mBinding.tvCollectNum.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                resources.getDrawable(R.mipmap.main_bottom_icon_collect_normal), null, null
            )
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        try {
            sharePopWindow = null
            UMShareAPI.get(this).release()
        } catch (e: Exception) {
        }
    }

    fun gotoLogin(): Boolean {
        if (!AppUtils.isLogin()) {
            toast(R.string.unlogin_tip)
            ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY).navigation()
            return false
        }
        return true
    }

    override fun initData() {
        mModel.getZixunInfo(contentId)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordEvent(event: UpdateCommentEvent) {
    }

}