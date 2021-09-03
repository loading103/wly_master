package com.daqsoft.travelCultureModule.itrobot

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.daqsoft.baselib.base.TitleBarActivity
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.UIHelperUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ActivityItRobotBinding
import com.daqsoft.provider.MainARouterPath
import com.daqsoft.provider.bean.ItRobotBean
import com.daqsoft.provider.bean.ItRobotDataBean
import com.daqsoft.travelCultureModule.itrobot.adapter.ItRobotKeyAdapter
import com.daqsoft.travelCultureModule.itrobot.adapter.ItRobotsAdapter
import com.daqsoft.travelCultureModule.itrobot.view.RecoderInfoDialog
import com.daqsoft.travelCultureModule.itrobot.viewmodel.ItRobotViewModel
import com.iflytek.cloud.*
import com.iflytek.cloud.ui.RecognizerDialog
import com.iflytek.cloud.ui.RecognizerDialogListener
import com.tbruyelle.rxpermissions2.RxPermissions
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.lang.Exception


/**
 * @Description 智能机器人
 * @ClassName   ItRobotActivity
 * @Author      luoyi
 * @Time        2020/5/18 9:22
 */
@Route(path = MainARouterPath.IT_ROBOT_PAGE)
class ItRobotActivity : TitleBarActivity<ActivityItRobotBinding, ItRobotViewModel>() {

    /**
     * 机器人适配器
     */
    private var itRobotsAdapter: ItRobotsAdapter? = null

    /**
     * 机器人关键字
     */
    private var itRobotKeyAdapter: ItRobotKeyAdapter? = null
    /**
     * 机器人消息集合
     */
    private var mItRobotDatas: MutableList<ItRobotDataBean> = mutableListOf()
    /**
     * 机器人信息
     */
    private var itRobtoInfoBean: ItRobotBean? = null

    /**
     *   语音听写对象
     */
    private var mIat: SpeechRecognizer? = null
    /**
     * 语音识别弹窗
     */
    private var mRecoderInfoDialog: RecoderInfoDialog? = null
    /**
     * 权限
     */
    private var rxpermissons: RxPermissions? = null

    // 用HashMap存储听写结果
    private val mIatResults: HashMap<String, String> = LinkedHashMap()

    private var isLongClicked: Boolean = false
    override fun getLayout(): Int {
        return R.layout.activity_it_robot
    }

    override fun setTitle(): String {
        return "智能机器人"
    }

    override fun injectVm(): Class<ItRobotViewModel> {
        return ItRobotViewModel::class.java
    }

    override fun initPageParams() {
        // 初始科大讯飞 语言识别sdk
        SpeechUtility.createUtility(this@ItRobotActivity, SpeechConstant.APPID + "=5ec78143");
        isInitImmerBar = false
    }

    override fun initView() {
        rxpermissons = RxPermissions(this)
        itRobotsAdapter = ItRobotsAdapter(this@ItRobotActivity, mItRobotDatas)
        mBinding.recyItRobotMessage.layoutManager = LinearLayoutManager(
            this@ItRobotActivity,
            LinearLayoutManager.VERTICAL, false
        )
        mBinding.recyItRobotMessage.adapter = itRobotsAdapter

        itRobotKeyAdapter = ItRobotKeyAdapter()
        itRobotKeyAdapter?.emptyViewShow = false
        mBinding.recyItRobotKeys.layoutManager = LinearLayoutManager(
            this@ItRobotActivity,
            LinearLayoutManager.HORIZONTAL, false
        )
        mBinding.recyItRobotKeys.adapter = itRobotKeyAdapter

        initViewEvent()
        initViewModel()
    }

    private fun initViewEvent() {
        mBinding.edtInputContent.setOnEditorActionListener(object :
            TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    var text = mBinding.edtInputContent.text.toString()
                    if (text.isNullOrEmpty()) {
                        return false
                    }
                    addRobotReuqest(text)
                    UIHelperUtils.hideKeyboard(mBinding?.edtInputContent)
                    mBinding.edtInputContent.setText("")
                    return true
                } else {
                    return false
                }
            }

        })
        itRobotKeyAdapter?.onItemCLickListener = object : ItRobotKeyAdapter.OnItemCLickListener {
            override fun onItemClick(key: String) {
                addRobotReuqest(key)
            }

        }
        mBinding.vAudioItRobot.onNoDoubleClick {
            //            }
            mBinding.vToRecoder.visibility = View.GONE
            mBinding.vItrobotRecoderAudio.visibility = View.VISIBLE
        }
        mBinding.vItrobotRecoderAudio.onNoDoubleClick {
            mBinding.vToRecoder.visibility = View.VISIBLE
            mBinding.vItrobotRecoderAudio.visibility = View.GONE
        }
        mBinding.tvToRecoder.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                mBinding.tvToRecoder.setText("松开结束")
                rxpermissons?.request(Manifest.permission.RECORD_AUDIO)!!.subscribe {
                    if (it) {
                        isLongClicked = true
                        showRecoderDialog()
                    } else {
                        mBinding.tvToRecoder.setText("按住说两句")
                        mModel.toast.postValue("非常抱歉，请打开录音权限才能使用录音功能")
                    }
                }
                return false
            }

        })

        mBinding.tvToRecoder.setOnTouchListener(object : View.OnTouchListener {

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event != null && event.action == MotionEvent.ACTION_UP && isLongClicked) {
                    isLongClicked = false
                    mBinding.tvToRecoder.setText("按住说两句")
                }
                return false
            }

        })


    }

    /**
     * 添加问题
     */
    private fun addRobotReuqest(text: String) {
        var bean = ItRobotDataBean()
        bean.answer = text
        bean.sourceType = 0
        mItRobotDatas.add(bean)
        itRobotsAdapter?.notifyItemInserted(mItRobotDatas.size)
        mBinding.recyItRobotMessage?.smoothScrollToPosition(mItRobotDatas.size - 1)
        mModel.getItRobotAnswer(text)
    }

    private fun initViewModel() {
        // 机器人信息
        mModel.itRobotInfoLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            if (it != null) {
                // 处理机器人 欢迎语
                var bean = ItRobotDataBean()
                bean.answer = it.welcome
                bean.sourceType = 1
                bean.resourceType = "WORDS"
                mItRobotDatas.add(bean)
                itRobotsAdapter?.robotHeaderUrl = it.logo
                itRobotsAdapter?.notifyItemInserted(mItRobotDatas.size)
                mBinding.recyItRobotMessage.post {
                    mBinding.recyItRobotMessage.smoothScrollToPosition(mItRobotDatas.size)
                }
                itRobtoInfoBean = it
                if (!it.backgroundImage.isNullOrEmpty()) {
                    Glide.with(this@ItRobotActivity)
                        .load(it.backgroundImage)
                        .into(mBinding.imgRobotBg)
                }
                if (!it.candidateWord.isNullOrEmpty()) {
                    var keys = it.candidateWord.split(",")
                    if (!keys.isNullOrEmpty()) {
                        itRobotKeyAdapter?.add(keys as MutableList<String>)
                        mBinding.recyItRobotKeys.visibility = View.VISIBLE
                    }
                } else {
                    mBinding.recyItRobotKeys.visibility = View.GONE
                }

            }

        })
        // 错误返回
        mModel.mError.observe(this, Observer {
            dissMissLoadingDialog()
        })
        // 问答返回
        mModel.itRobotAnswerLiveData.observe(this, Observer {
            dissMissLoadingDialog()
            if (it != null) {
                // 机器人
                it.sourceType = 1
                if (it.answer.isNullOrEmpty() && it.data.isNullOrEmpty()) {
                    // 没有找到内容得时候
                    if (itRobtoInfoBean != null)
                        it.answer = resources.getString(
                            R.string.it_robot_empty_tip,
                            itRobtoInfoBean?.name
                        )
                }

                mItRobotDatas.add(it)
                // 投诉 新增 投诉消息
                if (it.type == "complaint") {
                    var bean = ItRobotDataBean()
                    bean.answer = "点击投诉"
                    bean.sourceType = 1
                    bean.type = "complaint_req"
                    mItRobotDatas.add(bean)
                }
                itRobotsAdapter?.notifyItemInserted(mItRobotDatas.size)
                scrollTo(itRobotsAdapter!!.itemCount - 1)
            }

        })

    }

    private fun scrollTo(pos: Int) {
        mBinding.recyItRobotMessage.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mBinding.recyItRobotMessage.scrollToPosition(pos)
                mBinding.recyItRobotMessage.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    override fun initData() {
        showLoadingDialog()
        mModel.getItRobotInfo()
    }

    private fun showRecoderDialog() {
        if (mRecoderInfoDialog == null) {
            mRecoderInfoDialog = RecoderInfoDialog(this@ItRobotActivity)
        }
        if (mRecoderInfoDialog != null) {
            mRecoderInfoDialog?.updateText("准备中...")
            mRecoderInfoDialog?.show()
            startRecognizer()
        }
    }

    /**
     * 开启语音识别
     */
    @Synchronized
    private fun startRecognizer() {
        if (mIat == null) {
            if(!initRecoginzer()){
                return
            }

        }
        //开始识别并设置监听器
        var res = mIat!!.startListening(object : RecognizerDialogListener, RecognizerListener {
            override fun onVolumeChanged(p0: Int, p1: ByteArray?) {
            }

            override fun onResult(results: RecognizerResult?, p1: Boolean) {
                var result: String? = ""
                if (results != null) {
                    val text: String = JsonParser.parseIatResult(results.resultString)

                    var sn: String? = null
                    // 读取json结果中的sn字段
                    try {
                        val resultJson = JSONObject(results.resultString)
                        sn = resultJson.optString("sn")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                    mIatResults[sn!!] = text

                    val resultBuffer = StringBuffer()
                    for (key in mIatResults.keys) {
                        resultBuffer.append(mIatResults[key])
                    }
                    result = resultBuffer.toString()
                }
                if (p1) {
                    // 最后一次 返回，处理识别结果
                    mRecoderInfoDialog?.dismiss()
                    if (mIatResults.isNullOrEmpty() || result.isNullOrEmpty()) {
                        ToastUtils.showMessage("非常抱歉，未识别到您说的语音，请稍后再试~")
                    } else {
                        showLoadingDialog()
                        addRobotReuqest(result)
                    }
                }

            }

            override fun onBeginOfSpeech() {
                mRecoderInfoDialog?.updateText("录制中...")
            }

            override fun onEvent(p0: Int, p1: Int, p2: Int, p3: Bundle?) {
            }

            override fun onEndOfSpeech() {
                mRecoderInfoDialog?.updateText("正在识别...")
            }

            override fun onError(p0: SpeechError?) {
                mRecoderInfoDialog?.updateText("语音识别器异常")
                ToastUtils.showMessage("" + p0)
                mRecoderInfoDialog?.dismiss()
            }
        })
        if (res != ErrorCode.SUCCESS) {
            ToastUtils.showMessage("非常抱歉，开启语音识别失败，请稍后再试")
            mRecoderInfoDialog?.dismiss()
        } else {

        }
    }

    /**
     * 初始化语音识别器
     */
    private fun initRecoginzer()  :Boolean {
        var mInitListener = InitListener {
            Timber.e("code=${it}")
        }
        try {
            mIat = SpeechRecognizer.createRecognizer(this@ItRobotActivity, mInitListener);
            //以下为dialog设置听写参数
            mIat?.setParameter(SpeechConstant.CLOUD_GRAMMAR, null);
            mIat?.setParameter(SpeechConstant.SUBJECT, null);
            //设置返回结果格式，目前支持json,xml以及plain 三种格式，其中plain为纯听写文本内容
            mIat!!.setParameter(SpeechConstant.RESULT_TYPE, "json");
            //此处engineType为“cloud”
            mIat!!.setParameter(SpeechConstant.ENGINE_TYPE, "cloud");
            //设置语音输入语言，zh_cn为简体中文
            mIat!!.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            //设置结果返回语言
            mIat!!.setParameter(SpeechConstant.ACCENT, "mandarin");
            // 设置语音前端点:静音超时时间，单位ms，即用户多长时间不说话则当做超时处理
            //取值范围{1000～10000}
            mIat!!.setParameter(SpeechConstant.VAD_BOS, "4000");
            //设置语音后端点:后端点静音检测时间，单位ms，即用户停止说话多长时间内即认为不再输入，
            //自动停止录音，范围{0~10000}
            mIat!!.setParameter(SpeechConstant.VAD_EOS, "1000");
            //设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
            mIat!!.setParameter(SpeechConstant.ASR_PTT, "0");
            return true
        }catch (e: Exception){
            Timber.e("code=语音初始化失败")
            return false
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            rxpermissons = null
            mRecoderInfoDialog = null
            if (mIat != null) {
                mIat?.cancel()
                mIat?.destroy()
                mIat = null
            }
        } catch (e: Exception) {
        }

    }
}