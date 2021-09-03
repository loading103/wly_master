package com.daqsoft.travelCultureModule.story

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemAddStrategyContentBinding
import com.daqsoft.mainmodule.databinding.LayoutAddGraphTitleBinding
import com.daqsoft.mainmodule.databinding.MainAddStategyFragmentBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.HomeStoryBean
import com.daqsoft.provider.bean.HomeTopicBean
import com.daqsoft.provider.bean.StrategyDetail
import com.daqsoft.provider.view.BaseDialog
import com.daqsoft.provider.view.BasePopupWindow
import com.daqsoft.travelCultureModule.story.vm.WriteStoryFragmentViewModel
import com.jakewharton.rxbinding2.view.RxView
import me.nereo.multi_image_selector.BigImgActivity
import me.nereo.multi_image_selector.MultiFileSelectorActivity
import me.nereo.multi_image_selector.bean.Constrant
import me.nereo.multi_image_selector.bean.Image
import me.nereo.multi_image_selector.bean.ImgBean
import me.nereo.multi_image_selector.upload.FileUpload
import me.nereo.multi_image_selector.upload.FileUpload.FileBack
import me.nereo.multi_image_selector.upload.UploadUtil
import me.nereo.multi_image_selector.video.PlayerActivity
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.textColor
import java.util.concurrent.TimeUnit

/**
 * @Description 写攻略页面
 * @ClassName   WriteStoryFragment
 * @Author      PuHua
 * @Time        2020/2/19 9:43
 */
class WriteStrategyFragment :
    BaseFragment<MainAddStategyFragmentBinding, WriteStoryFragmentViewModel>() {
    /**
     * 段落标题输入框
     */
    var titleInputWindow: BasePopupWindow? = null
    var uploadUtil: UploadUtil? = null
    var cover: String? = null
    var tagName: String? = null

    var delposition = 0
    var homeStoryBean: HomeStoryBean? = null

    /**上个页面带过来的话题*/
    private var topic: HomeTopicBean? = null

    /**
     * 添加的内容的适配器
     */
    val contentAdapter = object : RecyclerViewAdapter<ItemAddStrategyContentBinding,
            StrategyDetail>(R.layout.item_add_strategy_content) {
        @SuppressLint("CheckResult")
        override fun setVariable(
            mBinding: ItemAddStrategyContentBinding,
            position: Int,
            item: StrategyDetail
        ) {
            RxView.clicks(mBinding.ivDelete)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    delposition = position
                    deleteDialog?.show()
                }
            RxView.clicks(mBinding.tvAddLocation)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    ARouter.getInstance()
                        .build(MainARouterPath.MAIN_STORY_ADDRESS)
                        .navigation(activity, WriteStoryFragment.ADDLOCATION)
                }
            if (item.contentType != "CONTENT") {
                mBinding.tvName.visibility = View.GONE
                mBinding.etContent.visibility = View.GONE
                mBinding.image.visibility = View.VISIBLE
                if (item.contentType == "IMAGE") {
//                    mBinding.tvAddLocation.visibility = View.VISIBLE
                    Glide.with(context!!).load(item.content).into(mBinding.image)
                    mBinding.image.onNoDoubleClick {
                        val intent = Intent(context!!, BigImgActivity::class.java)
                        intent.putExtra("position", position)
                        val list = mutableListOf<String>()
                        list.add(item.content)
                        intent.putStringArrayListExtra("imgList", ArrayList(list))
                        startActivity(intent)
                    }
                } else {
                    mBinding.ivVideoIcon.visibility = View.VISIBLE
                    mBinding.tvAddLocation.visibility = View.GONE

                    // 异步加载视频缩略图
                    Glide.with(context!!)
                        .setDefaultRequestOptions(
                            RequestOptions()
                                .frame(1000000)
                                .centerCrop()
                        )
                        .load(item.content)
                        .into(mBinding.image)
                    mBinding.image.onNoDoubleClick {
                        val intent = Intent(context!!, PlayerActivity::class.java)
                        intent.putExtra("title", "视频播放")
                        intent.putExtra("url", item.content)
                        startActivity(intent)
                    }
                }
                mBinding.ivDelete.visibility = View.VISIBLE


            } else {
                mBinding.tvName.text = item.title
            }
            mBinding.etContent.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    item.content = s.toString()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                }
            })
        }

    }


    override fun getLayout(): Int = R.layout.main_add_stategy_fragment

    override fun injectVm(): Class<WriteStoryFragmentViewModel> =
        WriteStoryFragmentViewModel::class.java

    var labelSelected = mutableListOf<String>()

    @SuppressLint("CheckResult")
    override fun initView() {
        arguments?.let {
            topic = it.getParcelable<HomeTopicBean>("topic")
        }
        uploadUtil = UploadUtil(activity)
        uploadUtil!!.setSingle(true)
        mBinding.vm = mModel
        mModel.storyType = "strategy"
//        uploadUtil = UploadUtil("http://file.geeker.com.cn/", 0, mBinding.rvImages, activity, true)
        mBinding.tvPublish.setOnClickListener {
            if (!isNotAllFill()) {
//                mModel.urls = uploadUtil!!.path
                mModel.strategyList = contentAdapter.getData()
                mModel.content = mBinding.etContent.text.toString()
                mModel.topicId = labelSelected.joinToString(",")
                var resourceUrl: String? = mBinding.edtStoryLink.text.toString()
                if (!resourceUrl.isNullOrEmpty()) {
                    mModel.sourceUrl = resourceUrl
                }
                mModel.publishStory()
            }
        }
        mBinding.tvSave.onNoDoubleClick {
            if (!isNotAllFill()) {
//                mModel.urls = uploadUtil!!.path
                mModel.content = mBinding.etContent.text.toString()
                mModel.strategyList = contentAdapter.getData()
                mModel.topicId = labelSelected.joinToString(",")
                var resourceUrl: String? = mBinding.edtStoryLink.text.toString()
                if (!resourceUrl.isNullOrEmpty()) {
                    mModel.sourceUrl = resourceUrl
                }
                mModel.publishStory(3)
            }
        }



        mBinding.tvAddTag.setOnClickListener {

            ARouter.getInstance()
                .build(MainARouterPath.ADD_STORY_TAG)
                .navigation(activity, WriteStoryFragment.ADDTAG)
        }

        mBinding.lvLabels.setOnLabelClickListener { label, data, position ->
            data as HomeTopicBean
            if (mBinding.lvLabels.selectLabels.contains(position)) {
                labelSelected.add(data.id)
            } else {
                labelSelected.remove(data.id)
            }
        }

        mBinding.rvStrategies.layoutManager = LinearLayoutManager(
            this.activity, RecyclerView.VERTICAL,
            false
        )
        contentAdapter.emptyViewShow = false
        mBinding.rvStrategies.adapter = contentAdapter
        RxView.clicks(mBinding.tvAddGraph)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                initTitleInputWindow()
                titleInputWindow!!.showAtLocation(mBinding.root, Gravity.BOTTOM, 0, 0)
            }
        RxView.clicks(mBinding.tvAddImageVideo)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                uploadUtil!!.showSelect(mBinding.rlAddCover, activity)
            }
//        RxView.clicks(mBinding.tvContent)
//            .throttleFirst(1, TimeUnit.SECONDS)
//            .subscribe {
//                titleInputWindow!!.showAtLocation(mBinding.root, Gravity.BOTTOM, 0, 0)
//            }
        mBinding.tvAddCover.onNoDoubleClick {
            checkCoverPic()
        }
        mBinding.tvChangeCover.onNoDoubleClick {
            checkCoverPic()
        }
        mModel.toast.observe(this, Observer {
            toast(it)
        })
        initDeleteDialog()
    }

    private var deleteDialog: BaseDialog? = null

    private fun initDeleteDialog() {
        deleteDialog = BaseDialog(context)
        deleteDialog!!.contentView(R.layout.base_delete_dialog)
            .layoutParams(
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
            .gravity(Gravity.CENTER)
            .animType(BaseDialog.AnimInType.BOTTOM)
            .canceledOnTouchOutside(false)
        deleteDialog!!.findViewById<TextView>(R.id.tv_title).text = "删除图片"
        deleteDialog!!.findViewById<TextView>(R.id.tv_query).text = "删除"
        deleteDialog!!.findViewById<TextView>(R.id.tv_content).text = "确定从攻略中删除图片?"
        deleteDialog!!.findViewById<TextView>(R.id.tv_cancel).setOnClickListener {
            deleteDialog!!.dismiss()
        }
        deleteDialog!!.findViewById<TextView>(R.id.tv_query).setOnClickListener {
            contentAdapter.getData().removeAt(delposition)
            contentAdapter.notifyDataSetChanged()
            deleteDialog!!.dismiss()
        }
    }


    @SuppressLint("CheckResult", "WrongConstant")
    private fun initTitleInputWindow() {
        // 筛选
        val inflater = LayoutInflater.from(context)
        val addBinding: LayoutAddGraphTitleBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.layout_add_graph_title,
            null,
            false
        )

        if (titleInputWindow == null) {
            titleInputWindow = BasePopupWindow(
                addBinding.root, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true
            )
            titleInputWindow!!.softInputMode = PopupWindow.INPUT_METHOD_NEEDED
            titleInputWindow!!.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            // 设置背景
            titleInputWindow!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // 设置是否能够响应外部点击事件
            titleInputWindow!!.isOutsideTouchable = true
            // 设置能否响应点击事件
            titleInputWindow!!.isTouchable = true
            titleInputWindow!!.isFocusable = true
            titleInputWindow!!.setOnDismissListener {

                view!!.isSelected = false
            }
            addBinding.etTitle.text.clear()
            titleInputWindow!!.resetDarkPosition()
            RxView.clicks(addBinding.tvFinish)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe {
                    var title = addBinding.etTitle.text.toString()
                    val strategyDetail = StrategyDetail("", "CONTENT", "", "", "", title, "")

                    if (contentAdapter.getData().size == 0) {
                        mBinding.rvStrategies.visibility = View.VISIBLE
                    }
                    contentAdapter.addItem(strategyDetail)
                    titleInputWindow!!.dismiss()
                    titleInputWindow = null
//                    if(mBinding.tvContent.isShown){
//                        mBinding.tvContent.visibility = View.GONE
//                    }
                }

        }
    }


    /**
     * 检查必须是否已经填完
     */
    private fun isNotAllFill(): Boolean {

        if (mBinding.etTitle.text.isNullOrBlank()) {
            toast("请输入攻略标题!")
            return true
        } else if (mBinding.etContent.text.isNullOrBlank()) {
            toast("请输入攻略内容!")
            return true
        } else if (cover.isNullOrEmpty()) {
            toast("您还未添加封面，攻略不能发布!")
            return true
        }
        if (!tagName.isNullOrEmpty()) {
            mModel.title.set(tagName + mBinding.etTitle.text.toString())
        } else {
            mModel.title.set(mBinding.etTitle.text.toString())
        }
        mModel.content = mBinding.etContent.text.toString()
        mModel.cover.set(cover)
        if (!AppUtils.isLogin()) {
            toast("您还没登录,请先登录！")
            ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY).navigation()
            return true
        }
        return false
    }

    override fun initData() {
        mModel.storyType = "strategy"
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
            } else {
                mBinding.tvTopicLabel.visibility = View.GONE
            }

        })
        mModel.storyDetail.observe(this, Observer {
            if (it != null) {
                mModel.strategyId = it.id
                mBinding.tvAddCover.visibility = View.GONE
                mBinding.clCover.visibility = View.VISIBLE
                mBinding.itemUpload.visibility = View.GONE
                mBinding.mask.visibility = View.GONE
                cover = it.cover
                Glide.with(context!!).load(cover)
                    .into(mBinding.ivCover)
                mBinding.etTitle.setText(it.title)
                mBinding.etContent.setText(it.content.replace("</br>", "\n"))
                if (!it.strategyDetail.isNullOrEmpty()) {
                    mBinding.rvStrategies.visibility = View.VISIBLE
                    contentAdapter.add(it.strategyDetail as MutableList<StrategyDetail>)
                }
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

            }
        })
    }

    private fun checkCoverPic() {
        val intent = Intent(activity, MultiFileSelectorActivity::class.java)
        // 是否显示调用相机拍照
        intent.putExtra(MultiFileSelectorActivity.EXTRA_SHOW_CAMERA, true)
        // 最大图片选择数量
        intent.putExtra(MultiFileSelectorActivity.EXTRA_SELECT_COUNT, 1)
        // 设置模式 (支持 单选/MultiFileSelectorActivity.MODE_SINGLE 或者 多选/MultiFileSelectorActivity
        intent.putExtra(
            MultiFileSelectorActivity.EXTRA_SELECT_MODE,
            MultiFileSelectorActivity.MODE_SINGLE
        )
        intent.putExtra("TYPE", Constrant.ADD_COVER_IMAGE)
        // 默认选择
//        if (mImages != null && mImages.size() > 0) {
//            intent.putParcelableArrayListExtra(MultiFileSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, (ArrayList<? extends Parcelable>) mImages);
//        }

        startActivityForResult(intent, Constrant.ADD_COVER_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (requestCode == WriteStoryFragment.ADDTAG) {

                if (data.hasExtra("tag")) {
                    var tagNames = data.getStringExtra("tag")
                    val c = "#$tagNames#"
                    mBinding.tvAddTag.text = c
                    tagName = c
                    mBinding.tvAddTag.textColor = resources.getColor(R.color.colorPrimary)
                }
            } else if (requestCode == Constrant.ADD_COVER_IMAGE) {
                if (data != null && data!!.hasExtra(MultiFileSelectorActivity.EXTRA_RESULT)) {
                    var list: ArrayList<Image> =
                        data!!.getParcelableArrayListExtra(MultiFileSelectorActivity.EXTRA_RESULT)
                    if (list.size > 0) {
                        uploadCoverFile(list[0])
                    }
                }
            } else if (requestCode == Constrant.ADD_VIDEO || requestCode == Constrant.ADD_IMAGE) {
                if (data != null && data!!.hasExtra(MultiFileSelectorActivity.EXTRA_RESULT)) {
                    var list: ArrayList<Image> =
                        data!!.getParcelableArrayListExtra(MultiFileSelectorActivity.EXTRA_RESULT)
                    var type = "IMAGE"
                    if (requestCode == Constrant.ADD_VIDEO) {
                        type = "VIDEO"
                    }
                    if (list.size > 0) {
                        uploadContentFile(list, type)
                    }
                }
            }
        }

    }


    /**
     * 上传封面图
     */
    private fun uploadCoverFile(image: Image) {
        mBinding.tvAddCover.visibility = View.GONE
        mBinding.clCover.visibility = View.VISIBLE
        mBinding.pbCover.visibility = View.VISIBLE
        uploadUtil?.uploadFile(image, object : FileBack {
            override fun progress(progress: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun result(imgBean: ImgBean?) {
                if (imgBean != null) {
                    mBinding.itemUpload.visibility = View.GONE
                    mBinding.mask.visibility = View.GONE
                    if (imgBean.state == 0) {
                        cover = imgBean.url
                        Glide.with(context!!).load(cover)
                            .into(mBinding.ivCover)
                    } else {
                        mBinding.itemUpload.visibility = View.VISIBLE
                    }
                }
            }

        }, mBinding.pbCover)
    }

    private fun uploadContentFile(imgs: ArrayList<Image>, type: String) {
        uploadUtil?.uploadFiles(imgs, object : FileUpload.UploadFileBack {
            override fun result(value: String?) {
                if (!value.isNullOrEmpty()) {
                    val strategyDetail = StrategyDetail(value!!, type, "", "", "", "", "")
                    if (contentAdapter.getData().size == 0) {
                        mBinding.rvStrategies.visibility = View.VISIBLE
                    }
                    contentAdapter.addItem(strategyDetail)
                } else {
                    toast("上传失败,请稍后再试!")
                }
            }

            override fun resultList(value: MutableList<String>?) {
                print("value:$value")
            }

        })
    }
}