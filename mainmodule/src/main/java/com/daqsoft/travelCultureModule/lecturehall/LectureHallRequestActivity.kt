package com.daqsoft.travelCultureModule.lecturehall

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityLectureAllRequestBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.travelCultureModule.lecturehall.viewmodel.LectureHallReqViewModel

/**
 * @Description
 * @ClassName   LectureHallRequestActivity
 * @Author      luoyi
 * @Time        2020/6/17 17:52
 */
@Route(path = MainARouterPath.LECTURE_HALL_REQ)
class LectureHallRequestActivity : TitleBarActivity<ActivityLectureAllRequestBinding, LectureHallReqViewModel>() {

    @JvmField
    @Autowired
    var id: String = "0"

    override fun getLayout(): Int {
        return R.layout.activity_lecture_all_request
    }

    override fun setTitle(): String {
        return "提问题"
    }

    override fun injectVm(): Class<LectureHallReqViewModel> {
        return LectureHallReqViewModel::class.java
    }

    override fun initView() {
        initViewEvent()

        mModel.postLectureReqLd.observe(this, Observer {
            if (it) {
                ToastUtils.showMessage("问题已提交，请等待管理员审核")
                finish()
            } else {
                ToastUtils.showMessage("提交问题失败，请稍后再试~")
            }
        })
    }

    private fun initViewEvent() {
        mBinding.tvConfirm.onNoDoubleClick {
            var content = mBinding.edtReqContent.text.toString()
            if (content.isNullOrEmpty()) {
                ToastUtils.showMessage("请输入问题内容")
                return@onNoDoubleClick
            }
            mModel.potLectureHallReq(id, content)
        }
        mBinding.edtReqContent.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s!=null) {
                    var strs: String? = s.toString()
                    if(!strs.isNullOrEmpty()){
                        var lenth = strs.length
                        if(lenth<=200){
                            mBinding.tvReqNum.text="${lenth}/200"
                        }
                    }
                }
            }
        })
    }

    override fun initData() {
    }
}