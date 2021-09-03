package com.daqsoft.travelCultureModule.story

import android.content.Intent
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainAddStoryFragmentBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.HomeTopicBean
import com.daqsoft.provider.network.home.bean.ItemAddressBean
import com.daqsoft.provider.view.LabelsView
import com.daqsoft.travelCultureModule.story.vm.WriteStoryFragmentViewModel
import me.nereo.multi_image_selector.MultiFileSelectorActivity
import me.nereo.multi_image_selector.bean.Constrant
import me.nereo.multi_image_selector.bean.Image
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textColor
import java.util.regex.Pattern


/**
 * @Description 写故事页面
 * @ClassName   WriteStoryFragment
 * @Author      PuHua
 * @Time        2020/2/19 9:43
 *
 * 更新者     邓益千
 * 更新日期   2020年4月27日
 * 更新内容   获取上个页面传过来的话题
 */
class WriteStoryFragment : BaseFragment<MainAddStoryFragmentBinding, WriteStoryFragmentViewModel>() {

    companion object {
        val ADDLOCATION = 0x0004
        val ADDTAG = 1
    }

    /**上个页面带过来的话题*/
    private var topic: HomeTopicBean? = null

    override fun getLayout(): Int = R.layout.main_add_story_fragment

    override fun injectVm(): Class<WriteStoryFragmentViewModel> = WriteStoryFragmentViewModel::class.java


//    var uploadUtil: UploadUtil? = null

    override fun initView() {
        arguments?.let {
            topic = it.getParcelable<HomeTopicBean>("topic")
        }

        mBinding.vm = mModel
        mModel.storyType = "story"
        mBinding.rvImages.setPicNumber(20)
        mBinding.rvImages.maxPicVideoNum = 1
        mBinding.rvImages.init(activity, true,true)
        mBinding.tvPublish.setOnClickListener {
            if (!isNotAllFill()) {
                mModel.urls = mBinding.rvImages.path
                mModel.content = mBinding.etContent.text.toString()
                mModel.topicId = labelSelected.joinToString(",")
                var resourceUrl: String? = mBinding.edtStoryLink.text.toString()
                if (!resourceUrl.isNullOrEmpty()) {
                    mModel.sourceUrl = resourceUrl
                }
                mModel.publishStory(1)
            }
        }

        mBinding.tvLocation.onNoDoubleClick {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_STORY_ADDRESS)
                .navigation(activity, ADDLOCATION)
        }

        mBinding.tvLocationLabel.setOnClickListener {
            ARouter.getInstance()
                .build(MainARouterPath.MAIN_STORY_ADDRESS)
                .navigation(activity, ADDLOCATION)
        }

        mBinding.tvAddTag.setOnClickListener {

            ARouter.getInstance()
                .build(MainARouterPath.ADD_STORY_TAG)
                .navigation(activity, ADDTAG)
        }

        mBinding.lvLabels.setOnLabelClickListener(object : LabelsView.OnLabelClickListener {
            override fun onLabelClick(label: TextView?, data: Any?, position: Int) {
                data as HomeTopicBean
                if (mBinding.lvLabels.selectLabels.contains(position)) {
                    labelSelected.add(data.id)
                } else {
                    labelSelected.remove(data.id)
                }
            }

        })
        mBinding.tvSave.onNoDoubleClick {
            if (!isNotAllFill()) {
                mModel.urls = mBinding.rvImages.path
                mModel.content = mBinding.etContent.text.toString()
                mModel.topicId = labelSelected.joinToString(",")
                var resourceUrl: String? = mBinding.edtStoryLink.text.toString()
                if (!resourceUrl.isNullOrEmpty()) {
                    mModel.sourceUrl = resourceUrl
                }
                mModel.publishStory(3)
            }
        }
        mModel.toast.observe(this, Observer {
            toast(it)
        })
    }

    /**
     * 检查必须是否已经填完
     */
    private fun isNotAllFill(): Boolean {
        if (mBinding.etContent.text.isNullOrBlank()) {
            toast("请输入故事内容")
            return true
        }
        if (!AppUtils.isLogin()) {
            toast("您还没登录,请先登录！")
            ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY).navigation()
            return true
        }
        return false
    }

    var labelSelected = mutableListOf<String>()
    var tagName: String? = null
    override fun initData() {
        mModel.getTopicList()
        val idStr = arguments?.getString("id")
        if (!idStr.isNullOrEmpty()) {
            mModel.id = idStr
            mModel.getMineStoryDetail(idStr)
        }


        mModel.homeTopicBean.observe(this, Observer {
            if (topic != null) {
                var isContains = false
                for (bean in it) {
                    if (bean.id == topic!!.id) {
                        isContains = true
                    }
                }
                if (!isContains) {
                    it.add(topic!!)
                }
            }

            if (it.size > 0) {
                mBinding.lvLabels.setLabels(it) { label, position, data -> "#${data!!.name}#" }
                if (topic != null) {
                    for (index in it.indices) {
                        if (it[index].id == topic!!.id) {
                            mBinding.lvLabels.setSelects(index) //默认选中话题
                            labelSelected.add(topic!!.id)
                            break
                        }
                    }
                }

                // 显示选中的话题
                if (labelSelected.size > 0) {
                    val positions = mutableListOf<Int>()
                    for (index in it.indices) {
                        for (label in labelSelected) {
                            if (it[index].id == label) {
                                positions.add(index)
                            }
                        }
                    }
                    mBinding.lvLabels.setSelects(positions)
                }
                mBinding.tvTopicLabel.visibility = View.VISIBLE
            } else {
                mBinding.tvTopicLabel.visibility = View.GONE
            }

        })

        mModel.storyDetail.observe(this, Observer {
            if (it != null) {
                mModel.id = it.id
                mBinding.etTitle.setText(it.title)

                mBinding.etContent.setText(it.content.replace("</br>", "\n"))
                if (!it.tagName.isNullOrEmpty()) {
                    val c = "#${it.tagName}#"
                    mBinding.tvAddTag.text = c
                    tagName = c
                    mBinding.tvAddTag.textColor = resources.getColor(R.color.colorPrimary)
                }
                if (!it.topicInfo.isNullOrEmpty()) {
                    for (item in it.topicInfo) {
                        labelSelected.add(item.topicId)
                    }
                }
                if (!it.longitude.isNullOrEmpty() && !it.latitude.isNullOrEmpty()) {
                    mModel.address.set(
                        ItemAddressBean(
                            0,
                            "",
                            "",
                            "",
                            longitude = it.longitude.toDouble(),
                            latitude = it.latitude.toDouble(),
                            address = "",
                            image = "",
                            summary = "",
                            jumpUrl = "",
                            orderStatus = "",
                            floorPrice = "",
                            region = "",
                            siteId = 0,
                            status = false
                        )
                    )
//                   mBinding.tvLocationLabel.visibility = View.GONE
//                   mBinding.tvLocation.visibility = View.VISIBLE
                }

                if (!it.images.isNullOrEmpty()) {
                    var images = mutableListOf<Image>()
                    if (!it.video.isNullOrEmpty()) {
                        images.add(Image(it.video, "", 0))
                    }
                    for (item in it.images) {
                        images.add(Image(item, "", 0))
                    }
                    mBinding.rvImages.onActivityResult(images as java.util.ArrayList<Image>?)
                }
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (requestCode == ADDTAG) {

                if (data.hasExtra("tag")) {
                    val tagName = data.getStringExtra("tag")
                    val c = "#$tagName#"
                    mBinding.tvAddTag.text = c
                    val p = Pattern.compile("#.+?#")
                    val text = mBinding.etContent.text.toString()
                    var ss = if (!text.isNullOrEmpty()) {
                        val m = p.matcher(text)
                        if (m.matches()) {
                            SpannableString(m.replaceAll(c))
                        } else {
                            SpannableString(c + text)
                        }
                    } else {
                        SpannableString(c)
                    }
                    ss.setSpan(
                        ForegroundColorSpan(resources.getColor(R.color.colorPrimary)), 0,
                        c.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    mBinding.etContent.setText(ss)
                    mBinding.etContent.setSelection(mBinding.etContent.text.length)
                }
            } else if (requestCode == ADDLOCATION) {
                if (data.hasExtra("address")) {
                    val address = data.getParcelableExtra<ItemAddressBean>("address")
                    mModel.address.set(address)
                    mBinding.tvLocationLabel.visibility = View.GONE
                    mBinding.tvLocation.visibility = View.VISIBLE
                }
            } else if (requestCode == Constrant.ADD_IMAGE) {
//                if (resultCode == Constrant.ADD_IMAGE) {

                // 選擇图片视频上传
                if (data != null && data!!.hasExtra(MultiFileSelectorActivity.EXTRA_RESULT)) {
                    var list: ArrayList<Image> =
                        data!!.getParcelableArrayListExtra(MultiFileSelectorActivity.EXTRA_RESULT)
                    mBinding.rvImages.onActivityResult(list)
                }
//                }
            } else if (requestCode == Constrant.ADD_VIDEO) {
//                if(resultCode==Constrant.ADD_VIDEO){
                // 選擇图片视频上传
                if (data != null && data!!.hasExtra(MultiFileSelectorActivity.EXTRA_RESULT)) {
                    var list: ArrayList<Image> =
                        data!!.getParcelableArrayListExtra(MultiFileSelectorActivity.EXTRA_RESULT)
                    if (list.size > 0) {
                        mBinding.rvImages.insertAtFirst(list[0].apply {
                            type = 1
                        })

                    }
//                        mBinding.rvImages.onActivityResult(list)
                }
//                }

            }
        }

    }
}