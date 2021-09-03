package com.daqsoft.provider.businessview.view

import android.content.Context
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.KeyboardUtils
import com.daqsoft.baselib.utils.ToastUtils
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.QuestionSubmitBean
import com.daqsoft.provider.bean.SubmitBean
import com.daqsoft.provider.bean.SubmitChooseBean
import com.daqsoft.provider.businessview.adapter.QuestionSubmitAdapter
import com.daqsoft.provider.databinding.LayoutProviderQuestionBinding


/**
 * 单选多选问答题
 */
class ProviderQuestionChooseView : FrameLayout {

    var mContext: Context? = null

    var binding: LayoutProviderQuestionBinding? = null
    /**
     * 答题列表
     */
    var list: MutableList<QuestionSubmitBean> = mutableListOf()



    private val mAdapter by lazy { QuestionSubmitAdapter() }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_provider_question, this, false)
        binding?.recyclerProviderDetailsStory?.apply {
            this?.adapter = mAdapter
            setHasFixedSize(true)
            setItemViewCacheSize(100)

            addOnScrollListener(object :RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    KeyboardUtils.hideSoftInput( binding?.recyclerProviderDetailsStory as  View)

                }
            })
            this?.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    val position = parent.getChildAdapterPosition(view)
                    if(position==mAdapter.datas.size){
                        outRect.bottom = Utils.dip2px(context!!, 30f).toInt()
                    }

                }
            })
        }
        addView(binding!!.root)
    }

    /**
     * 设置数据
     */
    fun setNewData(data: MutableList<QuestionSubmitBean>) {
        list.clear()
        list.addAll(data)
        mAdapter.datas=list
        mAdapter.notifyDataSetChanged()
    }



    /**
     * 设置引导语
     */
    fun setShowSubject(showSubmit:Boolean) {
        mAdapter?.setShowSubject(showSubmit)
    }



    /**
     * 设置引导语
     */
    fun setGuideBody(topTitle:String,title:String) {
        mAdapter.guidebody=topTitle
        mAdapter.title=title
        mAdapter.notifyItemChanged(0)
    }

    /**
     * 提交数据时候返回值
     */
    fun getChooseDatas(): MutableList<SubmitBean>? {
        if(mAdapter.datas==null && mAdapter.datas.size<1){
            return null
        }
        var  chooseRoots= mutableListOf<SubmitBean>()

        mAdapter.datas.forEachIndexed { index, data ->
            var  chooseRoot=SubmitBean()
            var  choosebeans= mutableListOf<SubmitChooseBean>()
            when(data.type) {
                "SINGLE" -> {
                    data.chooseList.forEachIndexed { index1, view1 ->
                        if(view1.haveChoosed){
                            var  bean=SubmitChooseBean()
                            bean.id=view1.chooseId.toInt()
                            choosebeans.add(bean)
                        }
                    }
                }
                "MULTIPLE" -> {
                    data.chooseList.forEachIndexed { index1, view1 ->
                        if(view1.haveChoosed){
                            var  bean=SubmitChooseBean()
                            bean.id=view1.chooseId.toInt()
                            choosebeans.add(bean)
                        }
                    }
                }
                "QA" -> {
//                    data.chooseList.forEachIndexed { index1, view1 ->
                        if(!TextUtils.isEmpty(data.content)){
                            var  bean=SubmitChooseBean()
                            bean.answerBody=data.content
                            bean.id=data.questionId.toInt()
                            choosebeans.add(bean)
                        }
//                    }
                }
            }
            chooseRoot.id=data.questionId.toInt()
            chooseRoot.chooseList?.addAll(choosebeans)
            chooseRoots.add(chooseRoot)

        }
        chooseRoots.forEachIndexed { index, bean ->
            if(bean.chooseList!=null && bean.chooseList.size==0){
                ToastUtils.showMessage("您第${index+1}题尚未填写")
                (binding?.recyclerProviderDetailsStory?.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(index+1, 0)
                return null
            }
        }
        return chooseRoots
    }
}