package com.daqsoft.provider.view

import androidx.databinding.BindingAdapter

/**
 * itemView设置内容
 */
@BindingAdapter("rightContent")
fun setContent(itemView: ItemView, content: String?) {
   itemView.content = content
}