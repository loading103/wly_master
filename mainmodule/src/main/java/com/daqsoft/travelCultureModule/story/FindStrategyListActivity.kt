package com.daqsoft.travelCultureModule.story

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.daqsoft.baselib.base.*
import com.daqsoft.baselib.utils.PageDealUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.*
import com.daqsoft.provider.bean.*
import com.daqsoft.provider.utils.DividerTextUtils
import com.daqsoft.provider.utils.GaosiUtils
import com.daqsoft.travelCultureModule.story.story.StoryAdapter
import com.daqsoft.travelCultureModule.story.vm.FindStrategyListActivityViewModel
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * @Description 找攻略列表页面
 * @ClassName   HotActivityDetailActivity
 * @Author      PuHua
 * @Time        2019/12/23 17:28
 */
@Route(path = MainARouterPath.MAIN_STRATEGY_FIND)
class FindStrategyListActivity : TitleBarActivity<MainStrategyListBinding, FindStrategyListActivityViewModel>() {

    @JvmField
    @Autowired
    var id: String = ""

    @JvmField
    @Autowired
    var classifyId: String = ""

    var currType: String = ""

    override fun getLayout(): Int = R.layout.main_strategy_list

    override fun setTitle(): String = getString(R.string.main_strategy_find)


    override fun injectVm(): Class<FindStrategyListActivityViewModel> = FindStrategyListActivityViewModel::class.java

    private val storyAdapter by lazy { StoryAdapter(this) }

    @SuppressLint("CheckResult")
    override fun initView() {
        mBinding.vm = mModel

        mModel.storyList.observe(this, Observer {
            if (it != null) {
                if (mModel.currPage == 1) {
                    storyAdapter.clear()
                }
                if (it != null && !it.datas.isNullOrEmpty()) {
                    for (i in 0 until (it!!.datas!!.size ?: 0)) {
                        var data = it.datas!![i]

                        if (data.storyType == Constant.STORY_TYPE_STORY) {
                            storyAdapter!!.addViewType(R.layout.item_story_main)
                        } else if (data.storyType == Constant.STORY_TYPE_STRATEGY) {
                            storyAdapter!!.addViewType(R.layout.item_find_strategy)
                        } else {
                            storyAdapter!!.addViewType(R.layout.item_story_main)
                        }
                        storyAdapter!!.addItem(data)
                    }
                }
                if (it.page!!.currPage < it.page!!.totalPage) {
                    storyAdapter.loadComplete()
                } else {
                    storyAdapter.loadEnd()
                }

                if(currType==mModel.TYPEHOT && it.page!!.currPage>=3){
                    storyAdapter.loadEnd()
                }

            } else {
                storyAdapter.loadComplete()
            }

        })
        storyAdapter?.setOnLoadMoreListener {
            mModel.currPage = mModel.currPage + 1
            mModel.getHotStoryList(currType)
        }

        mModel.strategyCover.observe(this, Observer {
            if (it != null) {
                val spannableString = SpannableString("|精选推荐|${it.title}")
                spannableString.setSpan(
                    ForegroundColorSpan(resources.getColor(R.color.orange)),
                    0,
                    6,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                if (it.tagName.isNullOrEmpty()) {
                    mBinding.tvTag.visibility = View.GONE
                }
                mBinding.tvContent.text = spannableString
                var locationInfo = DividerTextUtils.convertDotString(
                    java.lang.StringBuilder(),
                    if (!it.regionNum.isNullOrEmpty() && it.regionNum != "0") {
                        it.regionNum + "个目的地"
                    } else {
                        ""
                    }, if (!it.playPointNum.isNullOrEmpty() && it.playPointNum != "0") {
                        it.playPointNum + "个游玩点"
                    } else {
                        ""
                    }, if (!it.hotelNum.isNullOrEmpty() && it.hotelNum != "0") {
                        it.hotelNum + "家酒店"
                    } else {
                        ""
                    }, if (!it.foodNum.isNullOrEmpty() && it.foodNum != "0") {
                        it.foodNum + "家餐饮"
                    } else {
                        ""
                    }
                )
                if (!locationInfo.isNullOrEmpty()) {
                    mBinding.tvLocation.text = locationInfo
                    mBinding.tvLocation.visibility = View.VISIBLE
                } else {
                    mBinding.tvLocation.visibility = View.GONE
                }
                //暂时加载一张图片
                if (!it.images.isNullOrEmpty()) {
                    changeImage(it.images[0])
                    Glide.with(this@FindStrategyListActivity).load(it.images[0])
                        .into(mBinding.cityImage)
                } else {
                    mBinding.cityImage.visibility = View.GONE
                }
            } else {
                mBinding.tvTag.visibility = View.GONE
            }

        })

        // 故事
        val storyLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        mBinding.rvStory.layoutManager = storyLayoutManager
        mBinding.rvStory.adapter = storyAdapter

        // 点击热门
        var d = RxView.clicks(mBinding.tvHot)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                val set = ConstraintSet()
                set.clone(mBinding.clStoryTypeSelect)

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
                    currType = mModel.TYPEHOT
                    mModel.currPage = 1
                    mModel.getHotStoryList(mModel.TYPEHOT)
                }
            }
        // 点击最新
        RxView.clicks(mBinding.tvNew)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                val set = ConstraintSet()
                set.clone(mBinding.clStoryTypeSelect)

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
                    currType = mModel.NEW
                    mModel.currPage = 1
                    mModel.getHotStoryList(mModel.NEW)
                }
            }
        mBinding.tvSearch.setOnClickListener {
            ARouter.getInstance().build(MainARouterPath.MAIN_ALL_SEARCH)
                .navigation()
        }
        mBinding.clLayout.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_STORY_DETAIL)
                .withString("id", mModel.strategyCover.value?.id)
                .withInt("type", 1)
                .navigation()
        }
    }


    override fun initData() {

        mModel.getStoryCover()
        currType = mModel.TYPEHOT
        mModel.getHotStoryList(mModel.TYPEHOT)

    }

    private fun changeImage(img: String) {
        Glide.with(mBinding.ivGaosi)
            .asBitmap()
            .load(img)
            .centerCrop()
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {

                    var b = GaosiUtils.rsBlur(applicationContext, resource, 25)
                    mBinding.ivGaosi.setImageBitmap(b)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })
    }

    /**
     * 去到添加故事详情页面
     */
    fun gotoShareStory(v: View) {
        ARouter.getInstance()
            .build(MainARouterPath.MAIN_STORY_STRATEGY_ADD)
            .navigation()
    }

    fun gotoCommentPage(v: View) {

        ARouter.getInstance()
            .build(MainARouterPath.MAIN_ACTIVITY_COMMENT)
            .withString("id", id)
            .navigation()
    }
}