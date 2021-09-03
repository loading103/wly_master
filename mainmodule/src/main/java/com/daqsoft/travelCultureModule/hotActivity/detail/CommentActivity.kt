package com.daqsoft.travelCultureModule.hotActivity.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.RatingBar
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.AppUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.MainActivityCommentBinding
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.base.ResourceType
import com.daqsoft.provider.bean.EmoticonsBean
import com.daqsoft.provider.businessview.adapter.EmoticonsEditBigAdapter
import com.daqsoft.provider.businessview.event.UpdateOrderCommentStatus
import com.daqsoft.provider.businessview.fragment.ProviderAddEmoticonsFragment
import com.daqsoft.provider.network.comment.CommentRepository
import com.daqsoft.provider.utils.DividerTextUtils
import com.jakewharton.rxbinding2.view.RxView
import me.nereo.multi_image_selector.MultiFileSelectorActivity
import me.nereo.multi_image_selector.bean.Constrant
import me.nereo.multi_image_selector.bean.Image
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.toast
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/**
 * @Description 评论页面
 * @ClassName   CommentActivity
 * @Author      PuHua
 * @Time        2020/1/15 15:38
 */
@Route(path = MainARouterPath.MAIN_COMMENT_ADD)
class CommentActivity : TitleBarActivity<MainActivityCommentBinding, CommentMV>() {

    @JvmField
    @Autowired
    var id: String = ""

    @JvmField
    @Autowired
    var type: String = ""

    @JvmField
    @Autowired
    var orderId: String? = ""
    var star: Int = 5

    // 已选择的表情数目
    var selectEmoticonsDatas: MutableList<EmoticonsBean> = mutableListOf()

    /**
     * 表情包大图显示控件
     */
    val emoticonsEditBigAdapter: EmoticonsEditBigAdapter by lazy {
        EmoticonsEditBigAdapter().apply {
            onEmoticonsEditListener = object : EmoticonsEditBigAdapter.OnEmoticonsEditListener {
                override fun onDeleteAllEmoticons() {
                    mBinding.rvEmoticons.visibility = View.GONE
                }

                override fun onDeleteEmoticons(position: Int) {
                    if (position in selectEmoticonsDatas.indices) {
                        selectEmoticonsDatas.removeAt(position)
                    }
                }

            }
            emptyViewShow = false
        }
    }

    private val addEmoticonsFrag: ProviderAddEmoticonsFragment by lazy {
        ProviderAddEmoticonsFragment().apply {
            onSelectResultListener = object : ProviderAddEmoticonsFragment.OnSelectResultListener {
                override fun onResultEmoticons(datas: MutableList<EmoticonsBean>) {
                    selectEmoticonsDatas.clear()
                    emoticonsEditBigAdapter.clear()
                    if (datas.isNotEmpty()) {
                        selectEmoticonsDatas.addAll(datas)
                        emoticonsEditBigAdapter.add(selectEmoticonsDatas)
                        mBinding.rvEmoticons.visibility = View.VISIBLE
                    } else {
                        mBinding.rvEmoticons.visibility = View.GONE
                    }

                }

            }
        }
    }

    override fun getLayout(): Int = R.layout.main_activity_comment

    override fun setTitle(): String = getString(R.string.home_activity_comment_submit)

    override fun injectVm(): Class<CommentMV> = CommentMV::class.java

    override fun initView() {
        mBinding.rvComments.setPicNumber(9)
        mBinding.rvComments.setMaxVideoNumer(1)
        mBinding.rvComments.init(this, true, false)
        this.initEvent()
        type?.let {
            when (it) {
                ResourceType.CONTENT_TYPE_ACTIVITY -> {
                    mBinding.tvActivityLevel.visibility = View.VISIBLE
                    mBinding.rbarActivity.visibility = View.VISIBLE
                    mBinding.rbarActivity.visibility = View.VISIBLE
                    mBinding.tvActivityLevelTip.visibility = View.VISIBLE
                    mBinding.lineActivityLevel.visibility = View.VISIBLE
                }
                else -> {
                    mBinding.tvActivityLevel.visibility = View.GONE
                    mBinding.rbarActivity.visibility = View.GONE
                    mBinding.lineActivityLevel.visibility = View.GONE
                    mBinding.tvActivityLevelTip.visibility = View.GONE
                }
            }
        }
        mBinding.rbarActivity.run {
            setOnRatingBarChangeListener(object : RatingBar.OnRatingBarChangeListener {
                override fun onRatingChanged(
                    ratingBar: RatingBar?,
                    rating: Float,
                    fromUser: Boolean
                ) {
                    when (rating) {
                        3.0f -> {
                            mBinding.tvActivityLevelTip.visibility = View.VISIBLE
                            mBinding.tvActivityLevelTip.text = "一般"

                        }
                        4.0f -> {
                            mBinding.tvActivityLevelTip.text = "满意"
                            mBinding.tvActivityLevelTip.visibility = View.VISIBLE
                        }
                        5.0f -> {
                            mBinding.tvActivityLevelTip.text = "非常满意"
                            mBinding.tvActivityLevelTip.visibility = View.VISIBLE
                        }
                        else -> {
                            mBinding.tvActivityLevelTip.visibility = View.GONE
                        }
                    }
                    star = rating.toInt()
                }


            })
        }
    }

    override fun initData() {
        mBinding.countWords = "0/200"
        if (!orderId.isNullOrEmpty()) {
            mBinding.tvLabel.text = "评价订单"
        }
        mModel.countWord.observe(this, Observer {
            mBinding.countWords = it
        })
        mModel.emoticonsLd.observe(this, Observer {
            if (it == null || it?.datas.isNullOrEmpty()) {
                mBinding.imgAddEmoticons.visibility = View.GONE
            } else {
                mBinding.imgAddEmoticons.visibility = View.VISIBLE
            }
        })
        mModel.getEmoticons()
    }

    @SuppressLint("CheckResult")
    private fun initEvent() {
        mBinding.rvEmoticons.run {
            layoutManager = GridLayoutManager(
                this@CommentActivity,
                5,
                GridLayoutManager.VERTICAL,
                false
            )
            adapter = emoticonsEditBigAdapter
        }
        mBinding.imgAddEmoticons.onNoDoubleClick {
            showAddEmoticons()
        }
        mBinding.imgAddImages.onNoDoubleClick {
            mBinding.rvComments.showSelectImages()
        }
        mBinding.etContent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mModel.getWordCount(mBinding.etContent.text.toString())
            }

        })
        RxView.clicks(mBinding.addCommentBtn).throttleFirst(1, TimeUnit.SECONDS).subscribe {
            run {
                val content = mBinding.etContent.text.toString()
                if (content.isNullOrEmpty()) {
                    toast("请输入评论内容!")
                } else {
                    if (AppUtils.isLogin()) {
                        var emoticonsIds: String = if (selectEmoticonsDatas.isNotEmpty()) {
                            var emoticons: MutableList<String> = mutableListOf()
                            for (i in selectEmoticonsDatas.indices) {
                                emoticons.add(selectEmoticonsDatas[i].id.toString())
                            }
                            DividerTextUtils.convertMuCommaString(emoticons)
                        } else {
                            ""
                        }
                        mModel.urls = mBinding.rvComments.path
                        if (!mModel.urls.isNullOrEmpty()) {
                            var imags = mModel.urls.split(",")
                            var isHadUnUploadFile = false
                            for (item in imags) {
                                if (!item.isNullOrEmpty() && !item.startsWith("http")) {
                                    isHadUnUploadFile = true
                                    break
                                }
                            }
                            if (isHadUnUploadFile) {
                                ToastUtils.showMessage("您还有未上传完成的图片或者视频，请等待上传完成~")
                                return@run
                            }
                        }
                        mModel.publishComment(id, type, content, orderId, star, emoticonsIds)
                    } else {
                        ToastUtils.showUnLoginMsg()
                        ARouter.getInstance().build(ARouterPath.UserModule.USER_LOGIN_ACTIVITY)
                            .navigation()
                    }
                }
            }
        }
    }

    private fun showAddEmoticons() {
        addEmoticonsFrag.selectEmoticons.clear()
        addEmoticonsFrag.selectEmoticons.addAll(selectEmoticonsDatas)
        if (!addEmoticonsFrag.isAdded) {
            addEmoticonsFrag.show(supportFragmentManager, "add_emoticons_frag")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constrant.ADD_IMAGE) {
            // 選擇图片视频上传
            if (data != null && data!!.hasExtra(MultiFileSelectorActivity.EXTRA_RESULT)) {
                var list: ArrayList<Image> =
                    data!!.getParcelableArrayListExtra(MultiFileSelectorActivity.EXTRA_RESULT)
                mBinding.rvComments.onActivityResult(list)
            }
//                }
        } else if (requestCode == Constrant.ADD_VIDEO) {
            // 選擇图片视频上传
            if (data != null && data!!.hasExtra(MultiFileSelectorActivity.EXTRA_RESULT)) {
                var list: ArrayList<Image> =
                    data!!.getParcelableArrayListExtra(MultiFileSelectorActivity.EXTRA_RESULT)
                if (list.size > 0) {
                    mBinding.rvComments.insertAtFirst(list[0].apply {
                        type = 1
                    })
                }
            }
        }
    }
}

class CommentMV : BaseViewModel() {
    // 输入的文本
    var countWord = MutableLiveData<String>()
    var urls = ""

    /**
     * 获取输入的字数然后生成字符串显示
     */
    fun getWordCount(words: String) {
        countWord.postValue("${words.length}/200")
    }

    /**
     * 发表评论
     * @param id 评论对象的id
     * @param content 评论内容
     * @param image 图片
     * @param video 视频
     */
    fun publishComment(
        id: String,
        type: String,
        content: String,
        orderId: String?,
        star: Int = 5,
        emoticonsIds: String
    ) {

        var param: HashMap<String, String> = HashMap()
        var imageUrls = ""
        var video = ""
        if (urls != "") {
            var imags = urls.split(",")
            val videoType = arrayListOf<String>("mp4", "avi", "wmv", "MP4", "AVI", "WMV")

            for (item in imags) {
                var videoStr = item.substring(item.lastIndexOf(".") + 1, item.length)
                if (videoType.contains(videoStr)) {
                    video += "$item,"
                } else {
                    imageUrls += "$item,"
                }
            }
            if (!imageUrls.isNullOrEmpty()) {
                imageUrls = imageUrls.substring(0, imageUrls.lastIndexOf(","))
                param["image"] = imageUrls
            }
            if (!video.isNullOrEmpty()) {
                video = video.substring(0, video.lastIndexOf(","))
                param["video"] = video
            }
        }
        var currOrderId: String? = ""
        if (orderId.isNullOrEmpty()) {
            currOrderId = ""
            param["orderId"] = currOrderId
        }
        param["resourceId"] = id
        param["resourceType"] = type
        param["content"] = content
        if (!type.isNullOrEmpty() && type == ResourceType.CONTENT_TYPE_ACTIVITY) {
            param["star"] = star.toString()
        }
        if (!emoticonsIds.isNullOrEmpty()) {
            param["emoticonsIds"] = emoticonsIds
        }

        CommentRepository.service.postAddComment(
            param
        )
            .excute(object : BaseObserver<Any>() {
                override fun onSuccess(response: BaseResponse<Any>) {
                    toast.postValue(response.message)
                    if (response.code == 0) {
                        if (!orderId.isNullOrEmpty()) {
                            EventBus.getDefault().post(UpdateOrderCommentStatus().apply {
                                updateOrderId = orderId
                            })
                        }else{
                            EventBus.getDefault().post(UpdateOrderCommentStatus())
                        }
                        finish.postValue(true)
                    }
                }
            })
    }

    var currEmotionPage: Int = 1
    var emoTionPageSize: Int = 10

    var emoticonsLd: MutableLiveData<BaseResponse<EmoticonsBean>> = MutableLiveData()

    fun getEmoticons() {
        mPresenter?.value?.loading = false
        CommentRepository.service.getEmotList(currEmotionPage, emoTionPageSize).excute(object :
            BaseObserver<EmoticonsBean>(mPresenter) {
            override fun onSuccess(response: BaseResponse<EmoticonsBean>) {
                emoticonsLd.postValue(response)
            }

            override fun onFailed(response: BaseResponse<EmoticonsBean>) {
                emoticonsLd.postValue(null)
            }

        })
    }

}