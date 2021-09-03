package com.daqsoft.volunteer.volunteer

import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.bean.UploadBean
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.provider.utils.SelectImageUtils
import com.daqsoft.volunteer.R
import com.daqsoft.volunteer.bean.VolunteerBriefInfoBean
import com.daqsoft.volunteer.databinding.ActivityVolunteerCardBinding
import com.daqsoft.volunteer.net.VolunteerRepository
import com.daqsoft.volunteer.utils.QRCodeUtils
import com.daqsoft.volunteer.utils.StringUtils
import com.daqsoft.volunteer.volunteer.vm.VolunteerUpdateInformationVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.net.URLEncoder

/**
 *@package:com.daqsoft.volunteer.volunteer
 *@date:2020/6/30 9:14
 *@author: caihj
 *@des:志愿者证
 **/
@Route(path = ARouterPath.VolunteerModule.VOLUNTEER_CARD)
class VolunteerCardActivity:TitleBarActivity<ActivityVolunteerCardBinding,VolunteerCardVM>() {
    override fun getLayout(): Int = R.layout.activity_volunteer_card

    override fun setTitle(): String = getString(R.string.volunteer_card_title)

    override fun injectVm(): Class<VolunteerCardVM> = VolunteerCardVM::class.java

    override fun initView() {
        mModel.volunteer.observe(this, Observer {
            dissMissLoadingDialog()
            mBinding.tvName.text = it.name
            mBinding.tvSex.text = it.sex
            mBinding.tvSn.text = it.sn
            if(it.head.isNotEmpty()){
                mBinding.llUpload.visibility = View.GONE
                mBinding.ivHead.visibility = View.VISIBLE
                Glide.with(this).load(it.head).into(mBinding.ivHead)
            }
            val qrcode = QRCodeUtils.createQRCodeBitmap(it.sn,Utils.dip2px(this,150f).toInt(),Utils.dip2px(this,150f).toInt())
            if(qrcode != null){
                Glide.with(this).load(qrcode).into(mBinding.ivQrcode)
            }
            val phone = "028-123456"
            val tel = getString(R.string.volunteer_tel) + phone
            mBinding.tvPhone.text = StringUtils.setTextColor(tel,resources.getColor(R.color.colorPrimary),tel.length - phone.length,tel.length)
        })

        mBinding.llUpload.onNoDoubleClick {
            headUrlUpdate()
        }
        mModel.headLocal.observe(this, Observer {
            val file = File(it)
            mModel.upLoadFile(file)
        })
        mModel.updateStatus.observe(this, Observer {
            dissMissLoadingDialog()
            if(it){
                mModel.getVolunteerInfo()
            }
        })
    }

    override fun initData() {
        showLoadingDialog()
        mModel.getVolunteerInfo()
    }
    /**
     * 跳转相机请求码
     */
    private val REQUEST_CAMERA_CODE = 0
    /**
     * 跳转相册请求码
     */
    private val REQUEST_ALBUM_CODE = 1

    /**
     * 底部头像选择弹框
     */
    private val popupWindow by lazy {
        SelectImageUtils.initPictureSelectPop(this, REQUEST_CAMERA_CODE, REQUEST_ALBUM_CODE)
    }

    private fun headUrlUpdate() {
        // 头像
        popupWindow.showAtLocation(mBinding.root, Gravity.BOTTOM, 0, 0)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_ALBUM_CODE) {
            // 相册
            if (data == null) {
                return
            }
            mModel.headLocal.postValue(
                SelectImageUtils.getFileFromAlbum(this, data)
                    ?: ""
            )
        }
        if (requestCode == REQUEST_CAMERA_CODE) {
            // 相机
            val file = SelectImageUtils.getFileFromCamera()
            if (file?.exists() == false) {
                return
            }
            mModel.headLocal.postValue(file?.absolutePath ?: "")
        }
    }


}

class VolunteerCardVM:VolunteerUpdateInformationVM(){
    var volunteer = MutableLiveData<VolunteerBriefInfoBean>()

    /**
     * 获取志愿者完整信息
     */
    fun getVolunteerInfo(){
        VolunteerRepository.service.getVolunteerInfo().excute(object : BaseObserver<VolunteerBriefInfoBean>(){
            override fun onSuccess(response: BaseResponse<VolunteerBriefInfoBean>) {
                volunteer.postValue(response.data)
            }
        })
    }

    /**
     * 头像
     */
    val head = MutableLiveData<String>()
    val headLocal = MutableLiveData<String>()

    /**
     * 上传图片
     * @param file 图片文件
     * @param index 上传图片的张数
     */
    fun upLoadFile(file: File) {
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "Filedata",
                URLEncoder.encode(file.name,"utf-8"),
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
            )
            .addFormDataPart("key", SPUtils.getInstance().getString(SPUtils.Config.OSS_KEY))
            .build()

        UserRepository().userService
            .upLoad(builder.parts())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<UploadBean> {
                override fun onComplete() {
                    Log.e("uploadFile:", "onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    Log.e("uploadFile:", "onSubscribe")

                }

                override fun onNext(t: UploadBean) {
                    Log.e("uploadFile:", "onNext  ${t.fileUrl}")
                    head.value = t.url
                    updateVolunteerInfo(
                        headUrl,
                        head.value!!
                    )
                }

                override fun onError(e: Throwable) {
                    Log.e("uploadFile:", "onError" + e)

                }

            })
    }

}