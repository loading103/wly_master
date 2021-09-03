package com.daqsoft.travelCultureModule.itrobot.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.daqsoft.baselib.utils.SPUtils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.mainmodule.R
import com.daqsoft.mainmodule.databinding.ItemListItRobotBinding
import com.daqsoft.mainmodule.databinding.ItemRequestWordItRobotBinding
import com.daqsoft.mainmodule.databinding.ItemRichItRobotBinding
import com.daqsoft.mainmodule.databinding.ItemWordItRobotBinding
import com.daqsoft.provider.bean.ItRobotDataBean
import com.daqsoft.provider.utils.HtmlUtils
import com.daqsoft.provider.utils.MenuJumpUtils
import org.jetbrains.anko.textColor

/**
 * @Description 智能机器人适配器
 * @ClassName   ItRobotsAdapter
 * @Author      luoyi
 * @Time        2020/5/22 10:17
 */
class ItRobotsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * 文本类型
     */
    var TYPE_WORDS: Int = 1001

    /**
     * 列表
     */
    var TYPE_LIST: Int = 1002

    /**
     * 富文本
     */
    var TYPE_RICH: Int = 1003

    /**
     * 问题
     */
    var TYPE_REQEUST_WORDS: Int = 1004

    /**
     * 数据集
     */
    private var datas: MutableList<ItRobotDataBean> = mutableListOf()

    private var mContext: Context? = null

    var robotHeaderUrl: String? = null

    constructor(context: Context, data: MutableList<ItRobotDataBean>) {
        this.datas = data
        this.mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_LIST -> {
                var binding: ItemListItRobotBinding =
                    DataBindingUtil.inflate<ItemListItRobotBinding>(
                        LayoutInflater.from(mContext!!),
                        R.layout.item_list_it_robot,
                        parent,
                        false
                    )
                return ListTypeViewHolder(binding)
            }
            TYPE_WORDS -> {
                var binding: ItemWordItRobotBinding =
                    DataBindingUtil.inflate<ItemWordItRobotBinding>(
                        LayoutInflater.from(mContext!!),
                        R.layout.item_word_it_robot,
                        parent,
                        false
                    )
                return WordsTypeViewHolder(binding)
            }
            TYPE_RICH -> {
                var binding: ItemRichItRobotBinding =
                    DataBindingUtil.inflate<ItemRichItRobotBinding>(
                        LayoutInflater.from(mContext!!),
                        R.layout.item_rich_it_robot,
                        parent,
                        false
                    )
                return RichTypeViewHolder(binding)
            }
            TYPE_REQEUST_WORDS -> {
                var binding: ItemRequestWordItRobotBinding =
                    DataBindingUtil.inflate<ItemRequestWordItRobotBinding>(
                        LayoutInflater.from(mContext!!),
                        R.layout.item_request_word_it_robot,
                        parent,
                        false
                    )
                return RequestWordsViewHolder(binding)
            }
            else -> {
                var binding: ItemWordItRobotBinding =
                    DataBindingUtil.inflate<ItemWordItRobotBinding>(
                        LayoutInflater.from(mContext!!),
                        R.layout.item_word_it_robot,
                        parent,
                        false
                    )
                return WordsTypeViewHolder(binding)
            }

        }

    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun getItemViewType(position: Int): Int {
        var item = datas[position]
        if (item.sourceType == 0) {
            return TYPE_REQEUST_WORDS
        } else {
            if (item.type == "resource") {
                return TYPE_LIST
            } else {
                var answer = item.answer
                if (!answer.isNullOrEmpty()) {

                }
                return TYPE_WORDS
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is WordsTypeViewHolder -> {
                bindWordUi(holder.binding, datas[position])
            }
            is ListTypeViewHolder -> {
                bindListTypeUi(holder.binding, datas[position])
            }
            is RichTypeViewHolder -> {
                // 预留得富文本处理
                bindRichTypeUi(holder.binding, datas[position])
            }
            is RequestWordsViewHolder -> {
                bindRequestWordsUI(holder.binding, datas[position])
            }
        }
    }

    /**
     * 绑定 富文本数据
     * @param binding
     * @param itRobotDataBean
     */
    private fun bindRichTypeUi(binding: ItemRichItRobotBinding?, itRobotDataBean: ItRobotDataBean) {

    }

    /**
     * 绑定 回答列表数据
     * @param binding
     * @param itRobotDataBean
     */
    private fun bindListTypeUi(binding: ItemListItRobotBinding?, itRobotDataBean: ItRobotDataBean) {
        if (itRobotDataBean != null && binding != null) {
            if (!itRobotDataBean.data.isNullOrEmpty()) {
                var adapter = ItRobotResourceAdapter(mContext!!)
                adapter.emptyViewShow = false
                binding.recyItRobotLs.layoutManager =
                    LinearLayoutManager(mContext!!, LinearLayoutManager.VERTICAL, false)
                binding.recyItRobotLs.adapter = adapter
                adapter.clear()
                adapter.add(itRobotDataBean.data)
                if (itRobotDataBean.resourceType.isNullOrEmpty()) {
                    binding.tvItRobotLookMore.visibility = View.GONE
                } else {
                    binding.tvItRobotLookMore.visibility = View.VISIBLE
                }
                binding.tvItRobotLookMore.onNoDoubleClick {
                    // 查看更多
                    MenuJumpUtils.jumpToResourceList(itRobotDataBean.resourceType)
                }
                Glide.with(binding.imgUserHeader)
                    .load(robotHeaderUrl)
                    .placeholder(R.mipmap.mine_profile_photo_default)
                    .into(binding.imgUserHeader)
            }
        }
    }

    /**
     * 绑定 回答关键字
     * @param binding
     * @param itRobotDataBean
     */
    private fun bindWordUi(binding: ItemWordItRobotBinding?, itRobotDataBean: ItRobotDataBean) {
        if (itRobotDataBean != null && binding != null) {
            if (itRobotDataBean.type == "complaint_req") {
                // 投诉
                binding.txtRequestWords.textColor =
                    mContext!!.resources.getColor(R.color.colorPrimary)
                binding.txtRequestWords.text = "" + itRobotDataBean.answer
                binding.txtRequestWords.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        MenuJumpUtils.jumpComplaintPage()
                    }
                })
            } else {
                binding.txtRequestWords.textColor = mContext!!.resources.getColor(R.color.color_333)
                binding.txtRequestWords.text = Html.fromHtml(itRobotDataBean.answer)
                binding.txtRequestWords.setOnClickListener(null)
            }
            Glide.with(binding.imgUserHeader)
                .load(robotHeaderUrl)
                .placeholder(R.mipmap.mine_profile_photo_default)
                .into(binding.imgUserHeader)
        }
        binding?.executePendingBindings()
    }

    /**
     * 绑定提问关键字
     * @param binding
     * @param itRobotDataBean
     */
    private fun bindRequestWordsUI(
        binding: ItemRequestWordItRobotBinding?,
        itRobotDataBean: ItRobotDataBean
    ) {
        if (itRobotDataBean != null && binding != null) {
            binding.txtRequestWords.text = itRobotDataBean.answer
            var header = SPUtils.getInstance().getString(SPUtils.Config.HEAD_PORTRAIT)
            Glide.with(binding.imgUserHeader)
                .load(header)
                .placeholder(R.mipmap.mine_profile_photo_default)
                .into(binding.imgUserHeader)
            binding.executePendingBindings()
        }
    }

    /**
     * 回答关键字
     */
    inner class WordsTypeViewHolder : RecyclerView.ViewHolder {
        var binding: ItemWordItRobotBinding? = null

        constructor(bind: ItemWordItRobotBinding) : super(bind.root) {
            this.binding = bind
        }
    }

    /**
     * 列表
     */
    inner class ListTypeViewHolder : RecyclerView.ViewHolder {
        var binding: ItemListItRobotBinding? = null

        constructor(bind: ItemListItRobotBinding) : super(bind.root) {
            this.binding = bind
        }
    }

    /**
     * 富文本
     */
    inner class RichTypeViewHolder : RecyclerView.ViewHolder {
        var binding: ItemRichItRobotBinding? = null

        constructor(bind: ItemRichItRobotBinding) : super(bind.root) {
            this.binding = bind
        }
    }

    /**
     * 提问关键字
     */
    inner class RequestWordsViewHolder : RecyclerView.ViewHolder {
        var binding: ItemRequestWordItRobotBinding? = null

        constructor(bind: ItemRequestWordItRobotBinding) : super(bind.root) {
            this.binding = bind
        }
    }
}