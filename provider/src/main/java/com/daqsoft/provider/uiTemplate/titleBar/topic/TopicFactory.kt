package com.daqsoft.provider.uiTemplate.titleBar.topic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.provider.R
import com.daqsoft.provider.uiTemplate.TemplateType
import com.daqsoft.provider.uiTemplate.titleBar.topic.holder.TopicViewHolderOne
import com.daqsoft.provider.uiTemplate.titleBar.topic.holder.TopicViewHolderTwo

class TopicFactory : TopicImpl {

    companion object {
        /**
         * 精品线路样式1
         */
        var TOPIC_STYLE_ONE = "1"

        /**
         * 精品线路样式1
         */
        var TOPIC_STYLE_TWO = "2"
    }

    override fun create(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TemplateType.TOPIC_STYLE_ONE -> {
                TopicViewHolderOne(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_topic_style, parent, false
                    )
                )
            }
            TemplateType.TOPIC_STYLE_TWO -> {
                TopicViewHolderTwo(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_topic_style, parent, false
                    )
                )
            }
            else -> {
                TopicViewHolderOne(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_topic_style, parent, false
                    )
                )
            }
        }
    }

    override fun getItemViewType(style: String): Int {
        return when (style) {
            TOPIC_STYLE_ONE -> {
                TemplateType.TOPIC_STYLE_ONE
            }
            TOPIC_STYLE_TWO -> {
                TemplateType.TOPIC_STYLE_TWO
            }
            else -> {
                TemplateType.TOPIC_STYLE_ONE
            }
        }
    }
}