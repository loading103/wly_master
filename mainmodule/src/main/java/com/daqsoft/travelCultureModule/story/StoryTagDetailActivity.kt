package com.daqsoft.travelCultureModule.story

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.*
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.adapter.GridStoryAdapter
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.travelCultureModule.story.story.StoryAdapter
import com.daqsoft.travelCultureModule.story.vm.StoryTagDetailActivityViewModel
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.main_story_tag_detail.*
import java.util.concurrent.TimeUnit
import kotlin.text.StringBuilder


/**
 * @Description 标签详情页面
 * @ClassName   HotActivityDetailActivity
 * @Author      PuHua
 * @Time        2019/12/23 17:28
 */
@Route(path = MainARouterPath.MAIN_STORY_TAG_DETAIL)
class StoryTagDetailActivity : TitleBarActivity<MainStoryTagDetailBinding, StoryTagDetailActivityViewModel>() {

    @JvmField
    @Autowired
    var id: String = ""

    @JvmField
    @Autowired
    var classifyId: String = ""

    @JvmField
    @Autowired
    var module: String? = ""


    override fun getLayout(): Int = R.layout.main_story_tag_detail

    override fun setTitle(): String = getString(R.string.home_story_tag_detail)


    override fun injectVm(): Class<StoryTagDetailActivityViewModel> = StoryTagDetailActivityViewModel::class.java

    private val storyAdapter by lazy { StoryAdapter(this,module = module) }

    private val storyGridAdapter by lazy { GridStoryAdapter(this,GridStoryAdapter.ARENA,module = module) }

    @SuppressLint("CheckResult")
    override fun initView() {
        mBinding.vm = mModel

        mModel.storyList.observe(this, Observer {
            if( mBinding.tvNew.isSelected && storyGridAdapter!=null){
                storyGridAdapter.clear()
            }
            if( !mBinding.tvNew.isSelected && storyAdapter!=null){
                storyAdapter.clear()
            }
            for (i in 0 until (it!!.size ?: 0)) {
                var data = it!![i]
                if (mBinding.tvNew.isSelected) {
                    storyGridAdapter!!.addItem(data)
                } else {
                    if (data.storyType == Constant.STORY_TYPE_STORY) {
                        storyAdapter!!.addViewType(R.layout.item_story_main)
                    } else if (data.storyType == Constant.STORY_TYPE_STRATEGY) {
                        storyAdapter!!.addViewType(R.layout.item_story_list_strategy)
                    }
                    storyAdapter!!.addItem(data)
                }
            }
            if (mBinding.tvNew.isSelected) {
                storyGridAdapter!!.notifyDataSetChanged()
            } else {
                storyAdapter!!.notifyDataSetChanged()
            }
            if (it.isNullOrEmpty()) {
                mBinding.rvStory.visibility = View.GONE
                v_data_empty.visibility = View.VISIBLE
            } else {
                mBinding.rvStory.visibility = View.VISIBLE
                v_data_empty.visibility = View.GONE
            }
        })

        mModel.storyTagDetail.observe(this, Observer {
            mBinding.tvTagName.text = "# ${it.tagName} #"
            mBinding.tvContentNumber.text = it.contentNum.toString()
            mBinding.tvMemberNumber.text = it.participateNum.toString()
            mBinding.tvViewNumber.text = it.showNum.toString()
        })


        // 点击热门
        var d = RxView.clicks(mBinding.tvHot)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                val set = ConstraintSet()
                set.clone(mBinding.clStoryTypeSelect)
                v_data_empty.visibility = View.GONE
                if (!mBinding.tvHot.isSelected) {
                    mBinding.tvHot.isSelected = true
                    mBinding.tvNew.isSelected = false
                    set.connect(
                        mBinding.vIndicator.id,
                        ConstraintSet.LEFT,
                        mBinding.tvHot.id,
                        ConstraintSet.LEFT
                    )
                    set.connect(
                        mBinding.vIndicator.id,
                        ConstraintSet.RIGHT,
                        mBinding.tvHot.id,
                        ConstraintSet.RIGHT
                    )
                    set.applyTo(mBinding.clStoryTypeSelect)
                    switchStoryLayout()
                    mModel.getHotStoryList(mModel.TYPEHOT)
                }
            }
        // 点击最新
        RxView.clicks(mBinding.tvNew)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                val set = ConstraintSet()
                set.clone(mBinding.clStoryTypeSelect)
                v_data_empty.visibility = View.GONE
                if (!mBinding.tvNew.isSelected) {
                    mBinding.tvNew.isSelected = true

                    mBinding.tvHot.isSelected = false
                    set.connect(
                        mBinding.vIndicator.id,
                        ConstraintSet.LEFT,
                        mBinding.tvNew.id,
                        ConstraintSet.LEFT
                    )
                    set.connect(
                        mBinding.vIndicator.id,
                        ConstraintSet.RIGHT,
                        mBinding.tvNew.id,
                        ConstraintSet.RIGHT
                    )
                    set.applyTo(mBinding.clStoryTypeSelect)
                    switchStoryLayout(1)
                    mModel.getHotStoryList("")
                }
            }


        // 故事
        val storyLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvStory.layoutManager = storyLayoutManager
        mBinding.rvStory.adapter = storyAdapter

        // 封面
        mModel.coverList.observe(this, Observer {
            if (it != null) {
                val item = it
                mBinding.avatar = item.vipHead
                if(item.vipHead.isNullOrEmpty()){
                    mBinding.ivUser.visibility = View.GONE
                    mBinding.tvLocation.visibility = View.GONE
                    mBinding.tvLike.visibility = View.GONE
                    mBinding.tvComment.visibility = View.GONE
                }else{
                    mBinding.ivUser.visibility = View.VISIBLE
                    mBinding.tvLocation.visibility = View.VISIBLE
                    mBinding.tvLike.visibility = View.VISIBLE
                    mBinding.tvComment.visibility = View.VISIBLE
                }
                if (!item.images.isNullOrEmpty() || !item.cover.isNullOrEmpty()) {
                    if (!item.cover.isNullOrEmpty()) {
                        mBinding.ivImage.visibility = View.VISIBLE
                        mBinding.url = item.cover
                    }
                    if (!item.images.isNullOrEmpty()) {
                        if(!item.images[0].isNullOrEmpty()){
                            mBinding.url = item.images[0]
                            mBinding.ivImage.visibility = View.VISIBLE
                        }
                    }

                }
                mBinding.name = item.vipNickName
                if (item.resourceRegionName.isNullOrEmpty()) {
                    // 判断是否关联地址和类型
                    mBinding.tvLocation.visibility = View.GONE
                } else {
                    mBinding.tvLocation.visibility = View.VISIBLE
                }

                if (!item.tagName.isNullOrEmpty()) {
                    mBinding.ivTagImage.visibility = View.VISIBLE
                    mBinding.tvTag.visibility = View.VISIBLE
                    mBinding.tvTag.text = item.tagName
                }

                var location = DividerTextUtils.convertDotString(
                    StringBuilder(), item.resourceRegionName, ResourceType
                        .getName(item.resourceType), item.resourceName
                )
                mBinding.tvLocation.text = location
                mBinding.likeNumber = item.likeNum.toString()
                mBinding.commentNumber = item.commentNum.toString()
                mBinding.content = item.title

                if (!item.images.isNullOrEmpty()) {
                    Glide.with(this)
                        .asBitmap()
                        .load(item.images[0])
                        .centerCrop()
                        .into(object : CustomTarget<Bitmap>() {

                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {

//                            var b = GaosiUtils.rsBlur(this@StoryTagDetailActivity, resource, 25)
//
//                            mBinding.ivGaosi.setImageBitmap(b)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {

                            }
                        })

                    mBinding.ivImage.onNoDoubleClick {
                        if (AppUtils.isLogin()) {
                            ARouter.getInstance()
                                .build(MainARouterPath.MAIN_STORY_DETAIL)
                                .withString("id", it.id)
                                .withInt("type", 1)
                                .navigation()
                        } else {
                            ToastUtils.showMessage("非常抱歉，登录后才能访问~")
                            ARouter.getInstance()
                                .build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                                .navigation()
                        }
                    }
                }
            }
        })

    }


    /**
     * 切换故事布局
     * @param type 0 列表布局 1 瀑布流布局
     */
    private fun switchStoryLayout(type: Int = 0) {
        if (type == 0) {
            val storyLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            mBinding.rvStory.layoutManager = storyLayoutManager
            mBinding.rvStory.adapter = storyAdapter
        } else {
            val storyLayoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            mBinding.rvStory.layoutManager = storyLayoutManager
            mBinding.rvStory.adapter = storyGridAdapter
        }
    }

    override fun initData() {
        mModel.tagId = id
        mModel.getHotStoryTagDetail()
        mModel.getHotStoryList(mModel.TYPEHOT)
        mModel.getCoverList()
    }

    /**
     * 去到添加故事详情页面
     */
    fun gotoShareStory(v: View) {
        ARouter.getInstance()
            .build(MainARouterPath.MAIN_STORY_STRATEGY_ADD)
            .withInt("type", 0)
            .navigation()
    }


    fun gotoCommentPage(v: View) {

        ARouter.getInstance()
            .build(MainARouterPath.MAIN_ACTIVITY_COMMENT)
            .withString("id", id)
            .navigation()
    }
}
