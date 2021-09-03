package com.daqsoft.usermodule.ui.complaint

import android.content.Intent
import android.view.Gravity
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.baselib.adapter.RecyclerViewAdapter
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.MineComplaintDetailsBean
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.ActivityMineComplaintDetailsBinding
import com.daqsoft.usermodule.databinding.ItemMineComplaintImgBinding
import me.nereo.multi_image_selector.BigImgActivity
import me.nereo.multi_image_selector.video.PlayerActivity
import org.jetbrains.anko.toast
import java.util.*

/**
 * @Description 我的投诉详情页面
 * @ClassName   MineComplaintDetailsActivity
 * @Author      Huangxi
 * @Time        2020/2/28
 */
@Route(path = ARouterPath.UserModule.USER_COMPLAINT_DETAILS_ACTIVITY)
class MineComplaintDetailsActivity :
    TitleBarActivity<ActivityMineComplaintDetailsBinding, ComplaintDetailsViewModel>() {

    @JvmField
    @Autowired
    var id = ""
    /**
     * 满意度
     */
    var satisfied: Int = 1

    /**
     * 视频图片适配器
     */
    var imgAdapter = object : RecyclerViewAdapter<ItemMineComplaintImgBinding, String>(
        R.layout.item_mine_complaint_img
    ) {
        override fun setVariable(
            mBinding: ItemMineComplaintImgBinding,
            position: Int,
            data: String
        ) {
            if (data.endsWith(".mp4")) {
                mBinding.ivVideo.visibility = View.VISIBLE
            } else {
                mBinding.ivVideo.visibility = View.GONE
            }
            // 异步加载视频缩略图
            Glide.with(this@MineComplaintDetailsActivity)
                .setDefaultRequestOptions(
                    RequestOptions()
                        .frame(1000000)
                        .centerCrop()
                )
                .load(data)
                .placeholder(R.drawable.placeholder_img_fail_h158)
                .into(mBinding.itemImg)
            mBinding.itemImg.setOnClickListener {
                if (data.endsWith(".mp4")) {
                    val intent =
                        Intent(this@MineComplaintDetailsActivity, PlayerActivity::class.java)
                    intent.putExtra("title", "视频播放")
                    intent.putExtra("url", data)
                    startActivity(intent)
                } else {
                    val intent =
                        Intent(this@MineComplaintDetailsActivity, BigImgActivity::class.java)
                    intent.putExtra("position", position)
                    intent.putStringArrayListExtra(
                        "imgList",
                        mModel.details.value?.evidenceImages as ArrayList<String>?
                    )
                    startActivity(intent)
                }
            }
        }
    }

    override fun getLayout(): Int = R.layout.activity_mine_complaint_details

    override fun setTitle(): String = "投诉详情"

    override fun injectVm(): Class<ComplaintDetailsViewModel> =
        ComplaintDetailsViewModel::class.java

    override fun initView() {
        mBinding.tvGood.isSelected = true
        mBinding.model = mModel
        mBinding.recyclerDetailsImg.apply {
            layoutManager = GridLayoutManager(this@MineComplaintDetailsActivity, 4)
            adapter = imgAdapter
        }
        mBinding.tvGood.setOnClickListener {
            satisfied = 1
            mBinding.tvGood.isSelected = true
            mBinding.tvBad.isSelected = false
        }
        mBinding.tvBad.setOnClickListener {
            satisfied = 0
            mBinding.tvGood.isSelected = false
            mBinding.tvBad.isSelected = true
        }
        mBinding.tvCommitComplaintComment.setOnClickListener {
            mModel.getMineComplaintSatisfied(id, satisfied)
        }
    }

    override fun initData() {
        mModel.getComplaintDetails(id)
    }

    override fun notifyData() {
        super.notifyData()
        mModel.details.observe(this, Observer {
            var imgList = mutableListOf<String>()
            imgList.addAll(it.evidenceImages as MutableList<String>)
            imgList.addAll(it.evidenceVideo as MutableList<String>)
            imgAdapter.add(imgList)
            imgAdapter.notifyDataSetChanged()
        })
        mModel.result.observe(this, Observer {
            if (it.code == 0) {
                toast("满意度提交成功").setGravity(Gravity.CENTER, 0, 0)
                imgAdapter.clear()
                mModel.getComplaintDetails(id)
            } else {
                toast(it.message.toString()).setGravity(Gravity.CENTER, 0, 0)
            }
        })
    }

}

/**
 * @Description 我的投诉详情页面ViewModel
 * @ClassName   ComplaintDetailsViewModel
 * @Author      Huangxi
 * @Time        2020/2/28
 */
class ComplaintDetailsViewModel : BaseViewModel() {
    /**
     * 数据
     */
    var details = MutableLiveData<MineComplaintDetailsBean>()
    /**
     * 数据
     */
    var result = MutableLiveData<BaseResponse<Any>>()

    /**
     * 获取投诉详情
     */
    fun getComplaintDetails(id: String) {
        mPresenter.value?.loading = true
        UserRepository().userService.getMineComplaintDetails(id)
            .excute(object : BaseObserver<MineComplaintDetailsBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<MineComplaintDetailsBean>) {
                    if (response?.data != null) {
                        details.postValue(response.data)
                    }
                }

            })
    }

    /**
     * 投诉结果满意度处理
     */
    fun getMineComplaintSatisfied(id: String, satisfied: Int) {
        mPresenter.value?.loading = true
        UserRepository().userService.getMineComplaintSatisfied(id, satisfied).excute(object
            : BaseObserver<Any>(mPresenter) {
            override fun onSuccess(response: BaseResponse<Any>) {
                result.postValue(response)
            }
        })

    }

}
