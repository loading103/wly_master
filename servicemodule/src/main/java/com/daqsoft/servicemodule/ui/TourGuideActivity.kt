package com.daqsoft.servicemodule.ui

import android.annotation.SuppressLint
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.android.scenic.servicemodule.databinding.ActivityTourGuideBinding
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.UIHelperUtils
import com.daqsoft.provider.ARouterPath
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_tour_guide.*
import me.nereo.multi_image_selector.utils.BitmapUtils
import org.jetbrains.anko.toast
import java.util.concurrent.TimeUnit


/**
 * @Description 导游查询
 * @Author  江云仙
 * @Time     2020/4/1
 */
@Route(path = ARouterPath.ServiceModule.SERVICE_TOUR_GUIDE_ACTIVITY)
class TourGuideActivity : TitleBarActivity<ActivityTourGuideBinding, TourGuideViewModel>(),
    View.OnClickListener {
    /**
     * 性别 1：男 2：女
     */
    var sex= "gender_1"
    /**
     * 导游姓名
     */
    var name = ""

    override fun getLayout(): Int {
        return R.layout.activity_tour_guide
    }

    override fun setTitle(): String {
        return "导游查询"
    }

    override fun injectVm(): Class<TourGuideViewModel> = TourGuideViewModel::class.java

    @SuppressLint("CheckResult")
    override fun initView() {
        mBinding.tvSex.setOnClickListener(this)
        mBinding.imgClear.setOnClickListener(this)
        setSpanColor()
        // 点击查询
        RxView.clicks(mBinding.tvQuery)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                queryTourGuide()
            }
    }

    /**查询导游*/
    private fun queryTourGuide() {
        name = mBinding.etName.text.toString().trim()
//        if (name.isNullOrEmpty()) {
//            toast("请输入姓名").setGravity(Gravity.CENTER, 0, 0)
//            return
//        }
        if (sex == null) {
            toast("请选择性别").setGravity(Gravity.CENTER, 0, 0)
            return
        }
        //页面跳转
        ARouter.getInstance()
            .build(ARouterPath.ServiceModule.SERVICE_TOUR_GUIDE_LIST_ACTIVITY)
            .withString("name",name)
            .withString("gender",sex?:"gender_1")
            .navigation()

    }

    /**设置投诉电话字体颜色*/
    private fun setSpanColor() {
        val spannable = SpannableStringBuilder(resources.getString(R.string.service_tour_complaint_phone))
        spannable.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(this, R.color.ff9e05)
            ), 7, spannable.length
            , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tv_complaint_phone.text = spannable


    }

    override fun initData() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_sex -> {
                // 性别
                BitmapUtils.hideInputWindow(this)
                UIHelperUtils.showOptionsPicker(this, genderWindow)
            }
            R.id.img_clear -> {
                //清除文字内容
                if (mBinding.etName.text.toString().trim().isNotEmpty()) {
                    mBinding.etName.setText("")
                }

            }
        }
    }

    /**
     * 性别选择器
     */
    private val genderWindow by lazy {
        val gender = resources.getStringArray(R.array.service_gender)
        val pV = OptionsPickerBuilder(this, OnOptionsSelectListener { s1, s2, s3, v ->
            sex = if (s1==0){
                "gender_1"
            }else{
                "gender_2"
            }
            mBinding.tvSex.text = gender.get(s1)
        }).build<String>()
        pV.setPicker(gender.asList())
        pV
    }
}

/**
 * 导游查询viewModel
 * */
class TourGuideViewModel : BaseViewModel() {

}
