package com.daqsoft.provider.businessview.adapter

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.provider.R
import com.daqsoft.provider.bean.QuestionSubmitBean
import com.daqsoft.provider.businessview.event.QuestSubmitEvent
import com.daqsoft.provider.databinding.*
import org.greenrobot.eventbus.EventBus

/**
 * 问卷调查
 */
class QuestionSubmitAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var guidebody:String=""
    var title:String=""
    var datas: MutableList<QuestionSubmitBean> = mutableListOf()

    var showSubmit:Boolean=false

    fun setShowSubject(showSubmit:Boolean) {
        this.showSubmit=showSubmit
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            CHOOSE_TYPE_SINGLE -> {
                var binding: ItemQuestionChooseSingleBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context!!),
                    R.layout.item_question_choose_single, parent, false)
                return ChooseSingleViewHolder(binding)
            }
            CHOOSE_TYPE_MULT -> {
                var binding: ItemQuestionChooseMultBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context!!),
                    R.layout.item_question_choose_mult, parent, false)
                return ChooseMultyViewHolder(binding)
            }
            CHOOSE_TYPE_INPUT -> {
                var binding: ItemQuestionChooseInputBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context!!),
                    R.layout.item_question_choose_input, parent, false)
                return ChooseAskViewHolder(binding)
            }
            CHOOSE_TYPE_PD -> {
                var binding: ItemQuestionChoosePdBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context!!),
                    R.layout.item_question_choose_pd, parent, false)
                return ChoosePdViewHolder(binding)
            }
            CHOOSE_TYPE_TEXT -> {
                var binding: ItemQuestionChooseTextBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context!!),
                    R.layout.item_question_choose_text, parent, false)
                return ChooseTextViewHolder(binding)
            }
            CHOOSE_TYPE_LAST -> {
                var binding: ItemQuestionChooseLastBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context!!),
                    R.layout.item_question_choose_last, parent, false)
                return ChooseLastViewHolder(binding)
            }

            else->{
                var binding: ItemQuestionChooseSingleBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context!!),
                    R.layout.item_question_choose_single, parent, false)
                return ChooseSingleViewHolder(binding)
            }
        }
    }


    override fun getItemCount(): Int {
        return datas.size+2
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            // 引导语
            is ChooseTextViewHolder -> {
                if(!TextUtils.isEmpty(guidebody)){
                    holder.binding?.tvContent?.text= guidebody
                    holder.binding?.tvContent?.visibility= View.VISIBLE
                }else{
                    holder.binding?.tvContent?.text=""
                    holder.binding?.tvContent?.visibility= View.GONE
                }
                if(!TextUtils.isEmpty(title)){
                    holder.binding?.tvTitle?.text= title
                    holder.binding?.tvTitle?.visibility= View.VISIBLE
                }else{
                    holder.binding?.tvTitle?.text=""
                    holder.binding?.tvTitle?.visibility= View.GONE
                }
            }
            // 单选
            is ChooseSingleViewHolder -> {
                holder.binding?.tvTitle?.text=getTitleText(position,datas[position-1])
                holder.binding?.datas=datas[position-1]
                setSigleChooseList(holder,position,datas[position-1])
            }
            // 多选
            is ChooseMultyViewHolder -> {
                holder.binding?.datas=datas[position-1]
                holder.binding?.tvTitle?.text=getTitleText(position,datas[position-1])
                setMultChooseList(holder,position,datas[position-1])
            }
            // 问答
            is ChooseAskViewHolder -> {
                holder.binding?.tvTitle?.text=getTitleText(position,datas[position-1])
                holder.binding?.datas=datas[position-1]
                if( datas[position-1].chooseList!=null &&  datas[position-1].chooseList.size>0 &&  !TextUtils.isEmpty(datas[position-1].chooseList[0].answerBody)){
                    holder.binding?.etContent?.setText(datas[position-1].chooseList[0].answerBody)
                    datas[position-1].content=datas[position-1].chooseList[0].answerBody
                }
                holder.binding?.etContent?.addTextChangedListener(object :TextWatcher{
                    override fun afterTextChanged(s: Editable?) {
                        datas[position-1].content=s.toString()
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }

                })
            }
            // 判断
            is ChoosePdViewHolder -> {
                holder.binding?.tvTitle?.text=getTitleText(position,datas[position-1])
                holder.binding?.datas=datas[position-1]
                holder.binding?.etContent?.addTextChangedListener(object :TextWatcher{
                    override fun afterTextChanged(s: Editable?) {
                        datas[position-1].content=s.toString()
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    }

                })
            }

            is  ChooseLastViewHolder -> {

                if(showSubmit){
                    holder.binding?.tvMine?.visibility=View.VISIBLE
                }else{
                    holder.binding?.tvMine?.visibility=View.GONE
                }
                holder.binding?.tvMine?.setOnClickListener {
                    EventBus.getDefault().post(QuestSubmitEvent())
                }
            }
        }
    }

    private fun getTitleText(position: Int, bean: QuestionSubmitBean): String? {
        if(position.toString().length==1){
            return   "0$position."+bean.title
        }else{
            return   "$position."+bean.title
        }
    }


    override fun getItemViewType(position: Int): Int {
        if(position==0){
            return CHOOSE_TYPE_TEXT
        }
        if(position==datas.size+1){
            return CHOOSE_TYPE_LAST
        }
        when(datas[position-1].type){
            "SINGLE"-> return CHOOSE_TYPE_SINGLE
            "MULTIPLE"-> return CHOOSE_TYPE_MULT
            "JUDGE"-> return CHOOSE_TYPE_PD
            "QA"-> return CHOOSE_TYPE_INPUT
            else-> return CHOOSE_TYPE_SINGLE
        }

    }

    companion object {
        /**
         * 单选
         */
        const val CHOOSE_TYPE_SINGLE = 1
        /**
         * 多选
         */
        const val CHOOSE_TYPE_MULT = 2

        /**
         * 问答
         */
        const val CHOOSE_TYPE_INPUT=3

        /**
         * 判断
         */
        const val CHOOSE_TYPE_PD=4
        /**
         * 顶部文字
         */
        const val CHOOSE_TYPE_TEXT= 5

        const val CHOOSE_TYPE_LAST=6
    }


    /**
     * 单选
     */
    inner class ChooseSingleViewHolder : RecyclerView.ViewHolder {
        var binding: ItemQuestionChooseSingleBinding? = null
        constructor(bind: ItemQuestionChooseSingleBinding) : super(bind.root) {
            this.binding = bind
        }
    }
    /**
     *多选
     */
    inner class ChooseMultyViewHolder : RecyclerView.ViewHolder {
        var binding: ItemQuestionChooseMultBinding? = null
        constructor(bind: ItemQuestionChooseMultBinding) : super(bind.root) {
            this.binding = bind
        }
    }
    /**
     *多选
     */
    inner class ChooseAskViewHolder : RecyclerView.ViewHolder {
        var binding: ItemQuestionChooseInputBinding? = null
        constructor(bind: ItemQuestionChooseInputBinding) : super(bind.root) {
            this.binding = bind
        }
    }

    /**
     *判断
     */
    inner class ChoosePdViewHolder : RecyclerView.ViewHolder {
        var binding: ItemQuestionChoosePdBinding? = null
        constructor(bind: ItemQuestionChoosePdBinding) : super(bind.root) {
            this.binding = bind
        }
    }

    /**
     *顶部文字
     */
    inner class ChooseTextViewHolder : RecyclerView.ViewHolder {
        var binding: ItemQuestionChooseTextBinding? = null
        constructor(bind: ItemQuestionChooseTextBinding) : super(bind.root) {
            this.binding = bind
        }
    }

    /**
     *顶部文字
     */
    inner class ChooseLastViewHolder : RecyclerView.ViewHolder {
        var binding: ItemQuestionChooseLastBinding? = null
        constructor(bind: ItemQuestionChooseLastBinding) : super(bind.root) {
            this.binding = bind
        }
    }


    /**
     * 单选选项处理
     */
    private fun setSigleChooseList(holder: ChooseSingleViewHolder, position: Int, bean: QuestionSubmitBean) {
        holder.binding?.recycleView.apply {
            var mAdapter = SingleChooseAdapter()
            holder.binding?.recycleView?.adapter=mAdapter
            mAdapter.setNewData(bean.chooseList)
        }
    }
    /**
     * 单选选项处理
     */
    private fun setMultChooseList(holder: ChooseMultyViewHolder, position: Int, bean: QuestionSubmitBean) {
        holder.binding?.recycleView.apply {
            var mAdapter = MultChooseAdapter()
            holder.binding?.recycleView?.adapter=mAdapter
            mAdapter.setNewData(bean.chooseList)
        }
    }

}



