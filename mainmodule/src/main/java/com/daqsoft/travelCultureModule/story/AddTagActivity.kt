package com.daqsoft.travelCultureModule.story

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityAddStoryTagsBinding
import com.daqsoft.mainmodule.databinding.ActivityStoryTagsBinding
import com.daqsoft.mainmodule.databinding.ItemSearchTagBinding
import com.daqsoft.mainmodule.databinding.ItemStoryTagBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.adapter.ViewPagerAdapter
import com.daqsoft.provider.bean.HomeStoryTagBean
import com.daqsoft.provider.network.home.HomeRepository
import com.daqsoft.provider.view.LabelsView
import com.daqsoft.travelCultureModule.story.vm.AddStoryTagActivityViewModel
import com.jakewharton.rxbinding2.view.RxView
import me.nereo.multi_image_selector.utils.BitmapUtils
import java.util.concurrent.TimeUnit

/**
 * @Description 标签墙页面
 * @ClassName   StoryTagActivity
 * @Author      PuHua
 * @Time        2020/1/19 9:31
 */
@Route(path = MainARouterPath.ADD_STORY_TAG)
class AddTagActivity : TitleBarActivity<ActivityAddStoryTagsBinding, AddStoryTagActivityViewModel>() {


    override fun getLayout(): Int = R.layout.activity_add_story_tags

    override fun setTitle(): String = getString(R.string.story_add_tag)

    /**
     * 保存搜索的内容
     */
    var serchContent = ""

    override fun injectVm(): Class<AddStoryTagActivityViewModel> = AddStoryTagActivityViewModel::class.java

    /**
     * 标签的适配器
     */
    var tagAdapter = object : RecyclerViewAdapter<ItemSearchTagBinding, HomeStoryTagBean>(
        R.layout
            .item_search_tag
    ) {
        @SuppressLint("CheckResult")
        override fun setVariable(
            mBinding: ItemSearchTagBinding,
            position: Int,
            item: HomeStoryTagBean
        ) {
            var ss = SpannableString("#" + item.name + "#")
            var s = ss.indexOf(serchContent)
            ss.setSpan(
                ForegroundColorSpan(resources.getColor(R.color.colorPrimary)), s,
                s + serchContent.length, Spanned
                    .SPAN_EXCLUSIVE_EXCLUSIVE
            )
            mBinding.tvTag.text = ss

            mBinding.root.onNoDoubleClick {
                returnTag(item)
            }
        }

    }

    /**
     * 返回值
     */
    fun returnTag(item: HomeStoryTagBean) {
        val intent = Intent()
        intent.putExtra("tag", item.name)
        setResult(1, intent)
        finish()
    }


    override fun initView() {
        mBinding.lvLabels.clearAllSelect()
        mBinding.llHot.visibility = View.VISIBLE
        mBinding.clSearchContent.visibility = View.GONE


        mModel.storyTagList.observe(this, Observer {

            mBinding.lvLabels.isVisible = it.isNotEmpty()

            mBinding.lvLabels.setLabels(
                it
            ) { label, position, data -> "#" + data?.name + "#" }
        })

        mModel.storyTagRecordList.observe(this, Observer {

            mBinding.noRecord = it.size == 0

            mBinding.lvUse.setLabels(
                it,
                object : com.daqsoft.provider.view.LabelsView.LabelTextProvider<String> {
                    override fun getLabelText(
                        label: TextView?,
                        position: Int,
                        data: String?
                    ): CharSequence {
                        data?.let {
                            return "#$data#";
                        }
                        return ""
                    }
                })
        })

        /**
         * 设置当焦点变化时页面的隐现状态
         */
        mBinding.etSearch.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
//                    mBinding.llHot.visibility = View.GONE
//                    mBinding.clSearchContent.visibility = View.VISIBLE
                } else {
                    mBinding.llHot.visibility = View.VISIBLE
                    mBinding.clSearchContent.visibility = View.GONE
                }
            }

        mBinding.ivAddTag.setOnClickListener {
            returnTag(HomeStoryTagBean("", "", serchContent, ""))
        }
        mBinding.lvUse.setOnLabelClickListener { label, data, position ->
            returnTag(HomeStoryTagBean("", "", data.toString(), ""))
        }
        mBinding.tvCancel.setOnClickListener {
            mBinding.llHot.visibility = View.VISIBLE
            mBinding.clSearchContent.visibility = View.GONE
//            mBinding.etSearch.isCursorVisible = false
            mBinding.etSearch.clearFocus()
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                mBinding.etSearch.focusable = View.NOT_FOCUSABLE
//            }
            BitmapUtils.hideInputWindow(this@AddTagActivity)
        }
        mModel.searchTagList.observe(this, Observer {

            if (it.size == 0) {
                mBinding.notFound = true
                mBinding.tvSearchTag.text = serchContent
                mBinding.tvSearchNotice.text =
                    getString(R.string.home_tag_notice_hint, serchContent)
            } else {
                mBinding.notFound = false
                tagAdapter.clear()
                tagAdapter.add(it)
                tagAdapter.notifyDataSetChanged()
            }
        })
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvTags.layoutManager = layoutManager
        mBinding.rvTags.adapter = tagAdapter

        mBinding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                serchContent = s.toString()
                if (!serchContent.isNullOrEmpty()) {
                    mModel.getStoryTagList(serchContent)
                    mBinding.llHot.visibility = View.GONE
                    mBinding.clSearchContent.visibility = View.VISIBLE
                } else {
                    mBinding.llHot.visibility = View.VISIBLE
                    mBinding.clSearchContent.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        mBinding.lvLabels.setOnLabelClickListener { label, data, position -> returnTag(data as HomeStoryTagBean) }

    }

    override fun initData() {
        mModel.getHotStoryTagList()

        mModel.getStoryTagRecordList()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}