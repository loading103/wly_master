package com.daqsoft.travelCultureModule.onLineClick.ui

import android.annotation.SuppressLint
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityOnLineClickListBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.travelCultureModule.country.util.TextFontUtil
import com.daqsoft.travelCultureModule.onLineClick.adapter.OnLineClickClassifyAdapter
import com.daqsoft.travelCultureModule.onLineClick.adapter.OnLineClickListAdapter
import com.daqsoft.travelCultureModule.onLineClick.bean.OnLineClickBean
import com.daqsoft.travelCultureModule.onLineClick.model.OnLineClickListViewModel
import com.daqsoft.travelCultureModule.onLineClick.util.BlurBmpUtil
import com.google.android.material.appbar.AppBarLayout
import org.jetbrains.anko.support.v4.onRefresh


/**
 * desc :网红打卡列表
 * @author 江云仙
 * @date 2020/4/20 11:29
 */
@Route(path = ARouterPath.OnLineClickModule.ONLINE_CLICK_LIST)
class OnLineClickListActivity : TitleBarActivity<ActivityOnLineClickListBinding, OnLineClickListViewModel>() {
    private val mBlurBmpUtil by lazy { BlurBmpUtil() }
    /**
     * 网红打卡分类
     */
    private val onLineClickClassifyAdapter by lazy { OnLineClickClassifyAdapter() }
    //网红打卡列表
    private val onLineClickListAdapter by lazy { OnLineClickListAdapter() }

    override fun getLayout(): Int {
        return R.layout.activity_on_line_click_list
    }

    override fun setTitle(): String {
        return "网红打卡"
    }

    override fun injectVm(): Class<OnLineClickListViewModel> = OnLineClickListViewModel::class.java

    @SuppressLint("SetTextI18n")
    override fun initView() {
        //分类
        mModel.onLineClickClassifyBean.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                mBinding.rvHeader.xLineClick.visibility = View.VISIBLE
                onLineClickClassifyAdapter.menus.addAll(it)
                onLineClickClassifyAdapter.notifyDataSetChanged()
                if (it.size > 0) {
                    mBinding.tvTitle.visibility = View.VISIBLE
                    mBinding.tvTitle.text = "网红" + it[0].name
                    mBinding.rvHeader.tvName.text = it[0].name
                    //设置粗体和字体大小
                    val positionstr = "1"
                    val textBoldAndSize = TextFontUtil.setTextBoldAndSize(positionstr, 19, 0, positionstr.length)
                    mBinding.rvHeader.tvHeaderNum.text = textBoldAndSize.append("/" + it.size)

                    mModel.channelCode = it[0].channelCode
                    mModel.getOnLineClickList()
                    try {
                        if (!it[0].backgroundImg.isNullOrEmpty())
                            mBlurBmpUtil.returnBlurBitmap(this@OnLineClickListActivity, it[0].backgroundImg) { bmp ->
                                mBinding.rvHeader.imgBg.setImageBitmap(bmp)
                            }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                mBinding.tvTitle.visibility = View.GONE
                mBinding.rvHeader.xLineClick.visibility = View.GONE
            }
            // 网红打卡分类
            mBinding.rvHeader.xLineClick.setAdapter(onLineClickClassifyAdapter)
            //页面切换
            mBinding.rvHeader.xLineClick.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                @SuppressLint("SetTextI18n")
                override fun onPageSelected(position: Int) {
                    try {
                        if (!it[position].backgroundImg.isNullOrEmpty())
                            mBlurBmpUtil.returnBlurBitmap(this@OnLineClickListActivity, it[position].backgroundImg) { bmp ->
                                mBinding.rvHeader.imgBg.setImageBitmap(bmp)
                            }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    if (!it[position].name.isNullOrEmpty()) {
                        mBinding.tvTitle.text = "网红" + it[position].name
                        mBinding.rvHeader.tvName.text = it[position].name

                        //设置粗体和字体大小
                        val positionstr = (position + 1).toString()
                        val textBoldAndSize = TextFontUtil.setTextBoldAndSize(positionstr, 19, 0, positionstr.length)
                        mBinding.rvHeader.tvHeaderNum.text = textBoldAndSize.append("/" + it.size)
                        mBinding.tvTitle.visibility = View.VISIBLE
                    } else {
                        mBinding.tvTitle.visibility = View.GONE
                    }
                    mModel.mCurrPage = 1
                    mModel.channelCode = onLineClickClassifyAdapter.menus[position].channelCode
                    mModel.getOnLineClickList()

                }

            })
        })

        //列表
        mBinding.recyOnlineClick.visibility = View.GONE
        mBinding.recyOnlineClick.apply {
            layoutManager = LinearLayoutManager(this@OnLineClickListActivity)
            adapter = onLineClickListAdapter
            onLineClickListAdapter.emptyViewShow = false
        }
        mModel.onLineClickBean.observe(this, Observer {
            pageDealed(it)
        })

        //上拉刷新
        mBinding.swprefreshCountryHappiness.setOnRefreshListener {
//            mBinding.swprefreshCountryHappiness.isRefreshing = true
            mModel.mCurrPage = 1
            mModel.getOnLineClickList()
        }
        //下拉加载
        onLineClickListAdapter?.setOnLoadMoreListener {
            mModel.mCurrPage++
            mModel.getOnLineClickList()
        }
    }

    /**
     *数据处理
     */
    private fun pageDealed(it: MutableList<OnLineClickBean>?) {
        dissMissLoadingDialog()
        if (!mBinding.recyOnlineClick.isVisible) {
            mBinding.recyOnlineClick.visibility = View.VISIBLE
        }
//        mBinding.swprefreshCountryHappiness.isRefreshing = false
        mBinding.swprefreshCountryHappiness.finishRefresh()
        if (mModel.mCurrPage == 1) {
            mBinding.recyOnlineClick.smoothScrollToPosition(0)
            onLineClickListAdapter.clear()
        }
        if (!it.isNullOrEmpty()) {
            onLineClickListAdapter.add(it)
        } else {
            if (mModel.mCurrPage == 1) {
                onLineClickListAdapter.emptyViewShow = true
            }
        }
        if (it.isNullOrEmpty() || it.size < mModel.mPageSize) {
            onLineClickListAdapter.loadEnd()
        } else {
            onLineClickListAdapter.loadComplete()
        }
    }

    override fun initData() {
        showLoadingDialog()
        mModel.getChannelSubset()
    }


}
