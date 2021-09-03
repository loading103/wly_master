package com.daqsoft.usermodule.ui.userInoformation

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import android.view.*
import android.widget.FrameLayout
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.daqsoft.baselib.base.BaseObserver
import com.daqsoft.baselib.base.BaseResponse
import com.daqsoft.baselib.base.BaseViewModel
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.extend.excute
import com.daqsoft.baselib.utils.*
import com.daqsoft.provider.ARouterPath
import com.daqsoft.provider.SPKey
import com.daqsoft.provider.bean.ConstellationBean
import com.daqsoft.provider.bean.UploadBean
import com.daqsoft.provider.network.net.UserApi
import com.daqsoft.provider.network.net.UserRepository
import com.daqsoft.usermodule.R
import com.daqsoft.usermodule.databinding.UsermoduleActivityAuthenticateCommitBinding
import com.daqsoft.usermodule.uitls.SelectImageUtils
import com.daqsoft.widget.HintDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.usermodule_activity_authenticate_commit.*
import me.nereo.multi_image_selector.bean.Image
import me.nereo.multi_image_selector.upload.FileUpload
import me.nereo.multi_image_selector.utils.BitmapUtils
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.jetbrains.anko.toast
import java.io.File


/**
 * 实名认证提交界面
 *
 * @param id 如果是编辑状态请重新传入id
 *
 * @author 周俊蒙
 * @version 1.0.0
 * @date 2019-11-18
 * @since JDK 1.8.0_191
 */
@Route(path = ARouterPath.UserModule.AUTHENTICATE_COMMIT_ACTIVITY)
class AuthenticateCommitActivity : TitleBarActivity<UsermoduleActivityAuthenticateCommitBinding,
        AuthenticateCommitActivityVm>(), View.OnClickListener {

    @JvmField
    @Autowired
    var id: String? = null

    @JvmField
    @Autowired
    var name: String = ""

    @JvmField
    @Autowired
    var card: String = ""

    @JvmField
    @Autowired
    var cardup: String = ""

    @JvmField
    @Autowired
    var carddown: String = ""

    /**
     * 是否上传正面
     */
    var isUp = false
    /**
     * 是否上传反面
     */
    var isDown = false

    override fun getLayout(): Int = R.layout.usermodule_activity_authenticate_commit

    override fun setTitle(): String = getString(R.string.user_real_name)

    override fun injectVm(): Class<AuthenticateCommitActivityVm> =
        AuthenticateCommitActivityVm::class.java

    override fun initView() {
        mBinding.view = this
        mBinding.vm = mModel
    }


    override fun initData() {
        if(!card.isNullOrEmpty()){
            mModel.cardType.set(card)
//            mBinding.mCommitTv.text="修改认证信息"
            mBinding.mCommitTv.text="提交"
        }else{
            mModel.cardType.set("身份证")
            mBinding.mCommitTv.text="提交"
        }
        if(!name.isNullOrEmpty()){
            mModel.name.set(name)
        }
        if(!card.isNullOrEmpty()){
            mModel.idCard.set(card)
        }
        if(!cardup.isNullOrEmpty()){
            mModel.idCardUpLocal.postValue(cardup)
            mModel.idCardUp=cardup
        }
        if(!carddown.isNullOrEmpty()){
            mModel.idCardDownLocal.postValue(carddown)
            mModel.idCardDown=carddown
        }

//        mModel.getCertTypeList()
    }

    override fun finish() {
        if (!isFinish) {
            HintDialog(this)
                .setTitle(getString(R.string.user_quite_real_user))
                .setMessage(getString(R.string.user_quite_real_user_hint))
                .setNegativeButton(getString(R.string.cancel)) {
                }
                .setPositiveButton(getString(R.string.user_quite)) {
                    super.finish()
                }
                .show()
        } else {
            super.finish()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mCommitTv -> {
                // 提交
                if (mModel.validCheck()) {
                    if (mModel.idCardDown.isNullOrEmpty()) {
                        toast("文件上传中，请稍后")
                    } else if (mModel.idCardUp.isNullOrEmpty()) {
                        toast("文件上传中，请稍后")
                    } else {
                        mModel.setRealNameInfo(id)
                    }
                }
            }
            R.id.mTypeTv -> {
//                typeSelector.show()
            }
            R.id.mIdCardNegativeIv -> {
                BitmapUtils.hideInputWindow(this)
                imgSelectPosition = 0
                Utils.setWindowBackGroud(this, 0.8f)
                selectImg.showAtLocation(
                    mBinding.root,
                    Gravity.BOTTOM,
                    0,
                    UIHelperUtils.getNavigationBarHeight(this)
                )
            }
            R.id.mIdCardPositiveIv -> {
                BitmapUtils.hideInputWindow(this)
                imgSelectPosition = 1
                Utils.setWindowBackGroud(this, 0.8f)
                selectImg.showAtLocation(
                    mBinding.root,
                    Gravity.BOTTOM,
                    0,
                    UIHelperUtils.getNavigationBarHeight(this)
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA_CODE) {
            // 相机
            val fileFromCamera = SelectImageUtils.getFileFromCamera()
            if (fileFromCamera?.exists() == false) {
                return
            }
            if (imgSelectPosition == 0) {
                // 反面
                mModel.idCardDownLocal.value = fileFromCamera?.absolutePath
                uploadFile(mModel.idCardDownLocal.value!!, 0)
            } else if (imgSelectPosition == 1) {
                // 正面
                mModel.idCardUpLocal.value = fileFromCamera?.absolutePath
                uploadFile(mModel.idCardUpLocal.value!!, 1)
            }


        } else if (requestCode == REQUEST_ALBUM_CODE) {
            // 相册
            val fileFromAlbum = SelectImageUtils.getFileFromAlbum(this, data)
            if (fileFromAlbum.isNullOrEmpty()) {
                return
            }
            if (imgSelectPosition == 0) {
                // 反面
                mModel.idCardDownLocal.value = fileFromAlbum
                uploadFile(mModel.idCardDownLocal.value!!, 0)
            } else if (imgSelectPosition == 1) {
                // 正面
                mModel.idCardUpLocal.value = fileFromAlbum
                uploadFile(mModel.idCardUpLocal.value!!, 1)
            }


        }

    }

    /**
     * 上传文件
     */
    fun uploadFile(path: String, type: Int) {
        var list = arrayListOf<String>()
        list.add(path)
        FileUpload.uploadFilePath(UserApi.FILE_UPLOAD, this, list, object : FileUpload
        .UploadFileBack {
            override fun result(value: String?) {
                if (type == 1) {
                    mModel.idCardUp = value
                } else {
                    mModel.idCardDown = value
                }

            }

            override fun resultList(value: MutableList<String>?) {
            }

        })
    }


    /**
     * 证件类型
     */
    private val typeSelector by lazy {
        OptionsPickerBuilder(this) { o1, o2, o3, v ->
            mModel.cardType.set(mModel.typeList.value?.get(o1)?.value)
            mTypeTv.text = mModel.typeList.value?.get(o1)?.name
        }
            .build<ConstellationBean>()
    }

    private val selectImg by lazy {
        SelectImageUtils.initPictureSelectPop(this, REQUEST_CAMERA_CODE, REQUEST_ALBUM_CODE)
    }

    var isFinish = false

    /**
     * 图片选择位置
     */
    private var imgSelectPosition = 0
    private val REQUEST_CAMERA_CODE = 10000
    private val REQUEST_ALBUM_CODE = 10001
    override fun notifyData() {
        mModel.emptyPosition.observe(this, Observer {
            when (it) {
                0 -> {
                    typeSelector.show()
                }
                1 -> {
                    mRealNameEt.requestFocus()
                }
                2 -> {
                    mIDCardNumEt.requestFocus()
                }
                3, 4 -> {
                    Utils.setWindowBackGroud(this, 0.7f)
                    selectImg.showAtLocation(mBinding.root, Gravity.BOTTOM, 0, 0)
                }
            }
        })
        mModel.typeList.observe(this, Observer {
            mModel.cardType.set(mModel.typeList.value?.get(0)?.value)
            mTypeTv.text = mModel.typeList.value?.get(0)?.name
            typeSelector.setPicker(it)
        })
        mModel.finish.observe(this, Observer {
            toast("提交成功!")
            SPUtils.getInstance().put(SPKey.REALNAMESTATUS, 4)
            isFinish = true
            finish()
        })
    }

}

class AuthenticateCommitActivityVm : BaseViewModel() {
    /**
     * 证件类型数据
     */
    val typeList = MutableLiveData<MutableList<ConstellationBean>>()

    /**
     * 姓名
     */
    val name = ObservableField<String>("")
    /**
     * 身份证号码
     */
    val idCard = ObservableField<String>("")
    /**
     * 证件类型
     */
    var cardType = ObservableField<String>("")
    /**
     * 正面
     */
    var idCardUp: String? = null
    /**
     * 反面
     */
    var idCardDown: String? = null
    /**
     * 未填写内容的位置
     */
    val emptyPosition = MutableLiveData<Int>()
    /**
     * 本地正面图片地址
     */
    val idCardUpLocal = MutableLiveData<String>()
    /**
     * 本地反面图片地址
     */
    val idCardDownLocal = MutableLiveData<String>()

    /**
     * 获取证件类型
     */
    fun getCertTypeList() {
        UserRepository().userService
            .getCertTypeList()
            .excute(object : BaseObserver<ConstellationBean>(mPresenter) {
                override fun onSuccess(response: BaseResponse<ConstellationBean>) {
                    if (!response.datas.isNullOrEmpty()) {
                        typeList.value = response.datas
                    }
                }
            })
    }

    /**
     * 合格性检测
     */
    fun validCheck(): Boolean {
        if (cardType.get()?.isNullOrEmpty() == true) {
            emptyPosition.postValue(0)
            return false
        }

        if (name.get()?.isNullOrEmpty() == true) {
            emptyPosition.postValue(1)
            return false
        }

        if (idCard.get()?.isNullOrEmpty() == true) {
            emptyPosition.postValue(2)
            return false
        }

        if (idCardDownLocal.value.isNullOrEmpty()) {
            emptyPosition.postValue(3)
            return false
        }
        if (idCardUpLocal.value.isNullOrEmpty()) {
            emptyPosition.postValue(4)
            return false
        }
        return true
    }

    /**
     * 实名认证保存接口
     */
    fun setRealNameInfo(id: String?) {
        mPresenter.value?.loading = true
        UserRepository().userService
            .setRealNameInfo(
                name.get() ?: "",
                SPKey.ID_CARD,
                SM4Util.encryptByEcb(idCard.get() ?: ""),
                idCardUp ?: "",
                idCardDown ?: "",
                id
            )
            .excute(object : BaseObserver<Any>(mPresenter) {
                override fun onSuccess(response: BaseResponse<Any>) {
                    finish.postValue(true)
                }

                override fun onFailed(response: BaseResponse<Any>) {
                    if(response.message?.contains("服务器繁忙")!!){
                        ToastUtils.showMessage("请输入正确得证件号码")
                    }
                }
            })
    }

    /**
     * 图片上传张数
     */
    var picCount = 0

    /**
     * 上传图片
     * @param file 图片文件
     * @param index 上传图片的张数
     * @param oriented 0：反面 1：正面
     */
    fun upLoadFile(file: File, index: Int, oriented: Int, id: String?) {
        val builder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "Filedata",
                file.name,
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
            )
            .build()

        UserRepository().userService.upLoad(builder.parts())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : io.reactivex.Observer<UploadBean> {
                override fun onComplete() {
                    picCount++
                    if (index <= picCount) {
                        setRealNameInfo(id)
                    }
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: UploadBean) {
                    if (oriented == 0) {
                        idCardDown = t.fileUrl
                    } else if (oriented == 1) {
                        idCardUp = t.fileUrl
                    }
                }

                override fun onError(e: Throwable) {
                }

            })
    }
}