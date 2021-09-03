package com.daqsoft.travelCultureModule.selfdrivingtours

import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.daqsoft.baselib.base.BaseFragment
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.extend.excute

import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.FragmentSelfDrivingToursBinding
import com.daqsoft.travelCultureModule.branches.BranchScaleTransformer
import com.daqsoft.travelCultureModule.onLineClick.bean.OnLineClickClassifyBean
import com.daqsoft.travelCultureModule.onLineClick.bean.Subset
import com.daqsoft.travelCultureModule.onLineClick.net.OnLineClickRepository
import com.daqsoft.travelCultureModule.onLineClick.util.BlurBmpUtil
import java.util.HashMap

/**
 * desc :自驾游Fragment
 * @author 江云仙
 *
 * @date 2020/4/21 11:01
 */
class SelfDrivingToursFragment :
    BaseFragment<FragmentSelfDrivingToursBinding, SelfDrivingToursFragment.SelfDrivingToursViewModel>() {
    /**
     *自驾游适配器
     */
    private val selfDriveToursAdapter by lazy { SelfDriveToursAdapter() }
    private val mBlurBmpUtil by lazy { BlurBmpUtil() }
    override fun getLayout(): Int {
        return R.layout.fragment_self_driving_tours
    }

    override fun injectVm(): Class<SelfDrivingToursViewModel> =
        SelfDrivingToursViewModel::class.java

    override fun initView() {
        mModel.homeLineList.observe(this, Observer {
            if (it.isNotEmpty()) {
                mBinding.xSelfDrivingTours.visibility = View.VISIBLE
                selfDriveToursAdapter.menus.addAll(it)
                selfDriveToursAdapter.notifyDataSetChanged()
                try {
                    if (!it[0].backgroundImg.isNullOrEmpty())
                        mBlurBmpUtil.returnBlurBitmap(activity, it[0].backgroundImg) { bmp ->
                            mBinding.imgBg.setImageBitmap(bmp)
                        }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (!it[0].name.isNullOrEmpty()) {
                    mBinding.tvTitle.text = it[0].name
                    mBinding.tvTitle.visibility = View.VISIBLE
                } else {
                    mBinding.tvTitle.visibility = View.GONE
                }
                if (!it[0].summary.isNullOrEmpty()) {
                    mBinding.tvIntroduce.text = it[0].summary
                    mBinding.tvIntroduce.visibility = View.VISIBLE
                } else {
                    mBinding.tvIntroduce.visibility = View.GONE
                }
//                if (!it[0].contentType.isNullOrEmpty()) {
//                    mBinding.selfDrivingLabels.setLabels(it[0].contentType.split(","))
//                    mBinding.selfDrivingLabels.visibility = View.VISIBLE
//                } else {
//                    mBinding.selfDrivingLabels.visibility = View.GONE
//                }
            } else {
                mBinding.xSelfDrivingTours.visibility = View.GONE
            }

            // 自驾游分类
            mBinding.xSelfDrivingTours.setAdapter(selfDriveToursAdapter)
            mBinding.xSelfDrivingTours.setPageTransformer(BranchScaleTransformer())
            //页面切换
            mBinding.xSelfDrivingTours.addOnPageChangeListener(object :
                ViewPager.OnPageChangeListener {
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
                        if (it[position].name.isNotEmpty()) {
                            mBlurBmpUtil.returnBlurBitmap(
                                activity,
                                it[position].backgroundImg
                            ) { bmp ->
                                mBinding.imgBg.setImageBitmap(bmp)
                            }
                            if (!it[position].name.isNullOrEmpty()) {
                                mBinding.tvTitle.text = it[position].name
                                mBinding.tvTitle.visibility = View.VISIBLE
                            } else {
                                mBinding.tvTitle.visibility = View.GONE
                            }
                            if (!it[position].summary.isNullOrEmpty()) {
                                mBinding.tvIntroduce.text = it[position].summary
                                mBinding.tvIntroduce.visibility = View.VISIBLE
                            } else {
                                mBinding.tvIntroduce.visibility = View.GONE
                            }
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

            })
        })


    }

    override fun initData() {
        mModel.getContentList()
    }

    class SelfDrivingToursViewModel : BaseViewModel() {
        /**
         * 获取自驾游列表内容
         */
        var homeLineList = MutableLiveData<MutableList<Subset>>()

        fun getContentList() {
            val param = HashMap<String, Any>()
            param["channelCode"] = "zjy"
            OnLineClickRepository.service.getChannelSubset(param)
                .excute(object : BaseObserver<OnLineClickClassifyBean>(mPresenter) {
                    override fun onSuccess(response: BaseResponse<OnLineClickClassifyBean>) {
                        homeLineList.postValue(response.data?.subset)

                    }
                })
        }

    }

}
