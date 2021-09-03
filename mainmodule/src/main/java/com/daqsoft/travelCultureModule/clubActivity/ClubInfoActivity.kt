package com.daqsoft.travelCultureModule.clubActivity


import android.text.Html
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.adapter.setCenterCropImage
import com.daqsoft.baselib.adapter.setImageUrl
import com.daqsoft.baselib.adapter.setImageUrlqwx
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.SystemHelper
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.TitleBar
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityClubInfoBinding
import com.daqsoft.mainmodule.databinding.ItemClubImgeBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.Constant
import com.daqsoft.provider.businessview.event.UpdateCommentEvent
import com.daqsoft.provider.businessview.fragment.ProviderAddCommentFragment
import com.daqsoft.provider.businessview.model.ShareModel
import com.daqsoft.provider.commentModule.CommentAdapter
import com.daqsoft.provider.getRealImages
import com.daqsoft.provider.utils.MapNaviUtils
import com.daqsoft.provider.view.popupwindow.SharePopWindow
import com.daqsoft.travelCultureModule.clubActivity.adapter.ClubActivityAdapter
import com.daqsoft.travelCultureModule.clubActivity.adapter.ClubPersonAdapter
import com.daqsoft.travelCultureModule.clubActivity.adapter.ClubZixunAdapter
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubInfoBean
import com.daqsoft.travelCultureModule.clubActivity.bean.ClubPersonBean
import com.daqsoft.travelCultureModule.clubActivity.view.BottomDialog
import com.daqsoft.travelCultureModule.clubActivity.vm.ClubActicityInfoViewModel
import com.daqsoft.travelCultureModule.resource.IjkCustomPlayer
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.lang.Exception

/**
 * @des 社团详情页面
 * @author 秦文肖
 * @Date 2019/12/5 17:52
 */
@Route(path = MainARouterPath.MAIN_CLUB_INFO)
class ClubInfoActivity : TitleBarActivity<ActivityClubInfoBinding, ClubActicityInfoViewModel>() {


    @Autowired
    @JvmField
    var id : Int = -1

    override fun getLayout(): Int = R.layout.activity_club_info

    override fun setTitle(): String = "社团详情"
    /**
     * 分享弹窗
     */
    var sharePopWindow: SharePopWindow? = null

    override fun injectVm(): Class<ClubActicityInfoViewModel> =
        ClubActicityInfoViewModel::class.java
    private var addCommentPopFragment: ProviderAddCommentFragment? = null
    override fun initView() {

        EventBus.getDefault().register(this)
        // 点击获取更多人员
        mBinding.llClubPerson.tvClubAllPerson.setOnClickListener {
            //   mModel.getClubPersonList(intent.getStringExtra("id"),page_person++.toString())
        }

//        mBinding.tvShare.onNoDoubleClick {
//            toast("功能开发中")
//        }
    }
    override fun initCustomTitleBar(mTitleBar: TitleBar) {
        showShareButton(com.daqsoft.provider.R.mipmap.common_nav_button_share)
        setShareClick(object : View.OnClickListener {
            override fun onClick(v: View?) {
                mModel.clubInfo?.let {
                    if (sharePopWindow == null) {
                        sharePopWindow = SharePopWindow(this@ClubInfoActivity)
                    }
                    var content= Constant.SHARE_DEC
                    if(!TextUtils.isEmpty(it.value?.associaSummary)){
                        content=it.value?.associaSummary!!
                    }
                    sharePopWindow?.setShareContent(
                        it.value?.name, content,  it.value?.images.getRealImages(),
                        ShareModel.getClubDesc(it.value?.id.toString())
                    )
                    if (!sharePopWindow!!.isShowing) {
                        sharePopWindow?.showAsDropDown(mTitleBar)
                    }
                }
            }

        })
    }
    var adapter =
        object : RecyclerViewAdapter<ItemClubImgeBinding, String>(R.layout.item_club_imge) {
            override fun setVariable(mBinding: ItemClubImgeBinding, position: Int, item: String) {
                setImageUrlqwx(
                    mBinding.ivClubImggeItem, item, AppCompatResources.getDrawable(
                        BaseApplication.context, R.drawable.placeholder_img_fail_240_180
                    ), 5
                )
            }

        }
    // 人员
    var page_person = 1
    // 活动
    var page_activity = 1
    // 评论
    var page_conment = 1
    lateinit var cur_bean: ClubInfoBean
    override fun initData() {
        id = getIntent().getExtras().getInt("id")
        mModel.getClubInfo(id)
        mModel.getClubActivity(id.toString(), page_activity.toString(), "3")
        mModel.getZanData(id.toString(), ResourceType.CONTENT_TYPE_ASSOCIATION)
        mModel.getZixunList(id.toString(), ResourceType.CONTENT_TYPE_ASSOCIATION, "", "4", "1", "")
        mModel.getActivityCommentList(
            id.toString(),
            ResourceType.CONTENT_TYPE_ASSOCIATION,
            page_conment,
            10
        )
        mModel.getClubPersonList(id.toString(), page_person.toString())

        mBinding.llClubZixun.llZixunMore.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_CONTENT)
                .withString("channelCode", Constant.HOME_CONTENT_TYPE_societyNews)
                .withString("linksResourceType", ResourceType.CONTENT_TYPE_ASSOCIATION)
                .withString("linksResourceId", id.toString())
                .navigation()
        }

        mBinding.tvEditor.setOnClickListener {
//            var dialog = BottomDialog(this, true, BottomDialog.CallBack {
//                if (!it.equals("")) {
//                    mModel.post_comment(id.toString(), ResourceType.CONTENT_TYPE_ASSOCIATION, it)
//                }
//            })
//            dialog.setContentView(View.inflate(this, R.layout.layout_say, null))
//            dialog.setCanceledOnTouchOutside(true)
//            dialog.show()
            showCommentPopup()
        }
//        mModel.message_comment.observe(this@ClubInfoActivity, Observer {
//            toast(it)
//        })
        mBinding.tvLikeNum.setOnClickListener {
            if (cur_bean != null && cur_bean.resourceStatus != null) {
                if (cur_bean.resourceStatus!!.likeStatus) {
                    mModel.check_dislike(id.toString(), ResourceType.CONTENT_TYPE_ASSOCIATION)
                    cur_bean.resourceStatus!!.likeStatus = false
                    checkBoomlike(false)
                    if (!cur_bean.likeNum.isNullOrEmpty()) {
                        cur_bean.likeNum = (cur_bean.likeNum!!.toInt() - 1).toString()
                        mBinding.tvLikeNum.text = cur_bean.likeNum
                    }
                } else {

                    mModel.check_like(id.toString(), ResourceType.CONTENT_TYPE_ASSOCIATION)
                    cur_bean.resourceStatus!!.likeStatus = true
                    checkBoomlike(true)
                    if (!cur_bean.likeNum.isNullOrEmpty()) {
                        cur_bean.likeNum = (cur_bean.likeNum!!.toInt() + 1).toString();
                        mBinding.tvLikeNum.text = cur_bean.likeNum
                    }
                }
            }
        }
        mBinding.tvCollectNum.setOnClickListener {
            if (cur_bean != null && cur_bean.resourceStatus != null) {
                if (cur_bean!!.resourceStatus!!.collectionStatus) {
                    mModel.check_discolect(id.toString(), ResourceType.CONTENT_TYPE_ASSOCIATION)
                    cur_bean.resourceStatus!!.collectionStatus = false
                    checkBoomColect(false)
                    if (!cur_bean.collectionNum.isNullOrEmpty()) {
                        cur_bean.collectionNum = (cur_bean.collectionNum!!.toInt() - 1).toString();
                        mBinding.tvCollectNum.text = cur_bean.collectionNum
                    }
                } else if (cur_bean != null && !cur_bean!!.resourceStatus!!.collectionStatus) {
                    mModel.check_colect(id.toString(), ResourceType.CONTENT_TYPE_ASSOCIATION)
                    cur_bean.resourceStatus!!.collectionStatus = true
                    checkBoomColect(true)
                    if (!cur_bean.collectionNum.isNullOrEmpty()) {
                        cur_bean.collectionNum = (cur_bean.collectionNum!!.toInt() + 1).toString();
                        mBinding.tvCollectNum.text = cur_bean.collectionNum
                    }
                }
            }
        }
        mBinding.tvCiGuanzhu.onNoDoubleClick {
            if (cur_bean != null && cur_bean!!.resourceFansStatus != null) {
                if (cur_bean!!.resourceFansStatus!!.fansStaus) {
                    mModel.attentionResourceCancle(
                        id.toString(),
                        ResourceType.CONTENT_TYPE_ASSOCIATION
                    )
//                    mBinding.tvCiGuanzhu.text = "关注"
//                    ToastUtils.showMessage("取消关注成功")
//                    cur_bean.resourceFansStatus!!.fansStaus = false
                } else if (!cur_bean!!.resourceFansStatus!!.fansStaus) {
                    mModel.attentionResource(id.toString(), ResourceType.CONTENT_TYPE_ASSOCIATION)
//                    mBinding.tvCiGuanzhu.text = "已关注"
//                    ToastUtils.showMessage("关注成功")
//                    cur_bean.resourceFansStatus!!.fansStaus = true
                }
            }
        }

        // 关注
        mModel.subscribeLiveData.observe(this, Observer {
            if (it){
                if (cur_bean != null && cur_bean!!.resourceFansStatus != null) {
                    mBinding.tvCiGuanzhu.text = "已关注"
                    ToastUtils.showMessage("关注成功")
                    cur_bean.resourceFansStatus!!.fansStaus = true
                }
            }

        })
        // 取消关注
        mModel.unsubscribeLiveData.observe(this, Observer {
            if (it){
                if (cur_bean != null && cur_bean!!.resourceFansStatus != null) {
                    mBinding.tvCiGuanzhu.text = "关注"
                    ToastUtils.showMessage("取消关注成功")
                    cur_bean.resourceFansStatus!!.fansStaus = false
                }
            }
        })

        //社团详情
        mModel.clubInfo.observe(this, Observer {
            if (it != null) {
                var data = it
                cur_bean = it
                mBinding.tvCiName.text = it.name
                if (!it.name.isNullOrEmpty()) {
                    setTitleContent("" + it.name)
                }
                if (it.associaLevel == "") {
                    mBinding.tvCiLevel.visibility = View.GONE
                } else {
                    mBinding.tvCiLevel.text = it.associaLevel
                }
                if (it.type == null) {
                    mBinding.tvCiType.visibility = View.GONE
                } else {
                    mBinding.tvCiType.text = it.type
                }
                var other = ""
                if (!it.startTime.isNullOrEmpty()) other = other + it.startTime + "创建 | "
                if (it.teamPerson != 0) other = other + it.teamPerson + "位成员 | "
                if (it.fansNum != 0) other = other + it.fansNum + "个粉丝"
                mBinding.tvCiOther.text = other
                mBinding.tvLikeNum.text = it.likeNum
                mBinding.tvCollectNum.text = it.collectionNum
                mBinding.tvEditor.text = it.commentNum
                mBinding.llClubPinglun.tvClubPinglunAllnum.text = "(${it.commentNum})"
                if (it.resourceFansStatus != null) {
                    if (it.resourceFansStatus.fansStaus) {
                        mBinding.tvCiGuanzhu.text = "已关注"
                    } else {
                        mBinding.tvCiGuanzhu.text = "关注"
                    }
                }
                if (it.resourceStatus != null) {
                    checkBoomlike(it.resourceStatus.likeStatus)
                    checkBoomColect(it.resourceStatus.collectionStatus)
                }
                if (it.icon == "") mBinding.ivCiIcon.visibility = View.GONE
                setImageUrlqwx(
                    mBinding.ivCiIcon, it.icon, AppCompatResources.getDrawable(
                        BaseApplication.context, R.drawable.placeholder_img_fail_240_180
                    ), 10
                )
                if (it.video != "") {
                    mBinding.ivCiVideoCover.setUp(
                        it.video,
                        JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL,
                        ""
                    )
                    setImageUrlqwx(
                        mBinding.ivCiVideoCover.thumbImageView,
                        it.videoCover,
                        AppCompatResources.getDrawable(
                            BaseApplication.context, R.drawable.placeholder_img_fail_h300
                        ),
                        10
                    )
                } else {
                    mBinding.ivCiVideoCover.visibility = View.GONE
                }
                if (!it.phone.isNullOrEmpty()) {
                    mBinding.llClubInfo.tvCiPhone.apply {
                        text = it.phone
                        onNoDoubleClick {
                            SystemHelper.callPhone(this@ClubInfoActivity,it.phone)
                        }
                    }
                    mBinding.llClubInfo.llCiPhone.visibility = View.VISIBLE
                } else {
                    mBinding.llClubInfo.llCiPhone.visibility = View.GONE
                }

                mBinding.llClubInfo.tvCiAddress.setText(it.address)
                mBinding.llClubInfo.tvCiAddress.apply {
                    text = it.address
                    onNoDoubleClick {

                        if (it.latitude != 0.0 && it.longitude != 0.0) {
                            if (MapNaviUtils.isGdMapInstalled()) {
                                MapNaviUtils.openGaoDeNavi(
                                    context, 0.0, 0.0, null,
                                    it.latitude, it.longitude,
                                    it.name
                                )
                            } else {
                                toast("非常抱歉，系统未安装高德地图")
                            }
                        } else {
                            toast("非常抱歉，暂无位置信息")
                        }
                    }
                }
                if(!it.introduce.isNullOrEmpty()){
                    mBinding.llClubInfo.tvCiContent.setText(Html.fromHtml(it.introduce))
                }
                mBinding.llClubInfo.tvCiJsall.setOnClickListener {
                    if (cur_bean != null) {
                        ARouter.getInstance().build(MainARouterPath.MAIN_CLUB_INFO_INTRODUCE)
                            .withParcelable("data", cur_bean)
                            .navigation()
                    }
                }
                val tagLayoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                mBinding.llClubInfo.rvCiJsimages.layoutManager = tagLayoutManager

                if (!data.images.isNullOrEmpty()) {
                    val imges = data.images!!.split(",")
                    for (value in imges.indices) {
                        adapter.addItem(imges[value])
                    }
                    mBinding.llClubInfo.rvCiJsimages.isNestedScrollingEnabled = false
                    mBinding.llClubInfo.rvCiJsimages.adapter = adapter
                }
                mBinding.llClubPerson.tvClubPersonNumber.text = "(" + it.teamPerson + ")"
            }
        })
        val tagLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.llClubActivity.rvClubActivity.layoutManager = tagLayoutManager
        val clubActivityAdapter = ClubActivityAdapter(this)
        mBinding.llClubActivity.rvClubActivity.adapter = clubActivityAdapter
        //活动
        mModel.clubActivityList.observe(this, Observer {
            if (it != null) {
                mBinding.llClubActivity.llTitle.visibility = View.VISIBLE
                if (it.size > 0) {
                    if (it.size < 3) {
                        mBinding.llClubActivity.tvClubAllActivity.visibility = View.GONE
                    }
                    clubActivityAdapter.add(it)
                } else {
                    if (page_activity == 1) {
                        mBinding.tvCiActivity1.visibility = View.GONE
                        mBinding.tvCiActivity.visibility = View.GONE
                        mBinding.llClubActivity.llTitle.visibility = View.GONE
                    } else {
                        mBinding.llClubActivity.tvClubAllActivity.visibility = View.VISIBLE
                        mBinding.llClubActivity.tvClubAllActivity.text = "没有更多了"
                        mBinding.llClubActivity.tvClubAllActivity.isClickable = false
                    }
                }
            }
        })
        mModel.page_total.observe(this, Observer {
            mBinding.llClubActivity.tvCiaNumber.text = "($it)"
        })
        mBinding.llClubActivity.tvClubAllActivity.setOnClickListener {
            page_activity++
            mModel.getClubActivity(id.toString(), page_activity.toString(), "3")


        }
        // 赞
        mModel.thumbList.observe(this, Observer {
            if (it.size > 0) {
                mBinding.llClubZan.llTitle.visibility = View.VISIBLE
                for (i in it.indices) {
                    if (i > 9)
                        break

                    var imageView: ImageView = ImageView(this@ClubInfoActivity)
                    var param = LinearLayout.LayoutParams(
                        120,
                        120
                    )
                    param.rightMargin = 10
                    imageView.layoutParams = param as ViewGroup.LayoutParams?

                    setCenterCropImage(
                        imageView, it[i].headUrl, AppCompatResources.getDrawable(
                            BaseApplication.context, R.drawable.placeholder_img_fail_h158
                        ), true
                    )
                    mBinding.llClubZan.llCiZan.addView(imageView)
                }

                mBinding.llClubZan.tvEnd.isVisible = it.size >= 10
                mBinding.llClubZan.tvCiZanNum.text = "共" + mModel.totalSize + "人点赞"
            } else {

                mBinding.llClubZan.llTitle.visibility = View.GONE
            }
        })
        //资讯
        mModel.zixunList.observe(this, Observer {
            if (it != null && !it.datas.isNullOrEmpty()) {
                mBinding.llClubZixun.llTitle.visibility = View.VISIBLE
                mBinding.tvCiZixun1.visibility = View.VISIBLE
                mBinding.tvCiZixun.visibility = View.VISIBLE
                val tagLayoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                mBinding.llClubZixun.rvClubZixun.layoutManager = tagLayoutManager
                val zixunAdapter = ClubZixunAdapter(this)
                mBinding.llClubZixun.rvClubZixun.adapter = zixunAdapter
                for (index in it.datas!!.indices) {
                    if (index > 7) {
                        it.datas!!.removeAt(8)
                    }
                }
                zixunAdapter.add(it.datas!!)
            } else {
                mBinding.tvCiZixun1.visibility = View.GONE
                mBinding.tvCiZixun.visibility = View.GONE
                mBinding.llClubZixun.llTitle.visibility = View.GONE
            }
        })
        //获取评论列表
        val tagLayoutManager2 =
            LinearLayoutManager(this@ClubInfoActivity, LinearLayoutManager.VERTICAL, false)
        mBinding.llClubPinglun.rvClubPinglun.layoutManager = tagLayoutManager2
        val commentAdapter = CommentAdapter(this)
        mBinding.llClubPinglun.rvClubPinglun.adapter = commentAdapter
        mModel.commentBeans.observe(this, Observer {
            if (it.size != null) {
                if (page_conment == 1 && it.size < 1) {
                    mBinding.tvCiDianping1.visibility = View.GONE
                    mBinding.tvCiDianping.visibility = View.GONE
                    mBinding.llClubPinglun.llItemPinglun.visibility = View.GONE
                } else {
                    if(page_conment==1){
                        commentAdapter.clear()
                    }
                    mBinding.llClubPinglun.llItemPinglun.visibility = View.VISIBLE
                    if (it.size < 3) {
                        commentAdapter.loadEnd()
                    }
                    commentAdapter.add(it)
                }
            }
        })
        commentAdapter.setOnLoadMoreListener {
            page_conment++
            mModel.getActivityCommentList(
                id.toString(),
                ResourceType.CONTENT_TYPE_ASSOCIATION,
                page_conment,
                10
            )
        }
        //获取社团成员
        mModel.clubPersonList.observe(this@ClubInfoActivity, Observer {
            if (it.size > 0) {

                if (it.size >= 3) {
                    //展示更多按钮
                    mBinding.llClubPerson.tvClubAllPerson.visibility = View.VISIBLE
                } else {
                    mBinding.llClubPerson.tvClubAllPerson.visibility = View.GONE
                }

                mBinding.llClubPerson.llTitle.visibility = View.VISIBLE

                mBinding.llClubPerson.rvClubPerson.layoutManager = LinearLayoutManager(
                    this@ClubInfoActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                val adapter = ClubPersonAdapter(this)
                mBinding.llClubPerson.rvClubPerson.adapter = adapter

//                for (position in it.indices) {
//                    if (position > 2) {
//                        break
//                    } else {
//                        adapter.addItem(it[position])
//                    }
//                }

                adapter.add(it)

                mBinding.llClubPerson.tvClubAllPerson.onNoDoubleClick {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_CLUB_PERSON_LIST)
                        .withString("clubId", id.toString())
                        .navigation()
                }
            } else {
                mBinding.tvCiPerson.visibility = View.GONE
                mBinding.tvCiPerson1.visibility = View.GONE
                mBinding.llClubPerson.llTitle.visibility = View.GONE
            }
        })
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

    fun checkBoomColect(colect: Boolean) {
        if (colect) {
            mBinding.tvCollectNum.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                resources.getDrawable(R.mipmap.main_bottom_icon_collect_selected),
                null,
                null
            )
        } else {
            mBinding.tvCollectNum.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                resources.getDrawable(R.mipmap.main_bottom_icon_collect_normal), null, null
            )
        }
    }

    /**
     * 显示弹窗
     */
    private fun showCommentPopup() {
//        commentPopup?.showAtLocation(mBinding.root, Gravity.BOTTOM, 0, 0)
        if (addCommentPopFragment == null) {
            addCommentPopFragment =
                ProviderAddCommentFragment.newInstance(id.toString(),  ResourceType.CONTENT_TYPE_ASSOCIATION)
        }
        if (!addCommentPopFragment!!.isAdded) {
            addCommentPopFragment?.show(supportFragmentManager, "story_add_comment")
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            mBinding.ivCiVideoCover.pasuePlayer()
        } catch (e: Exception) {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            EventBus.getDefault().unregister(this)
            mBinding.ivCiVideoCover.release()
        } catch (e: Exception) {

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordEvent(event: UpdateCommentEvent) {
        page_conment=1
        mModel.getActivityCommentList(
            id.toString(),
            ResourceType.CONTENT_TYPE_ASSOCIATION,
            page_conment,
            10
        )
    }
}


