package com.daqsoft.provider.rxCommand

import android.annotation.SuppressLint
import android.view.View
import androidx.databinding.BindingAdapter
import com.daqsoft.provider.view.ItemInputView
import com.daqsoft.provider.view.ItemView
import com.jakewharton.rxbinding2.view.RxView
import java.util.concurrent.TimeUnit

/**
 * itemView点击事件不带参数
 */
@SuppressLint("CheckResult")
@BindingAdapter( "onClickCommand" )
fun clickCommand(view: View, clickCommand: ReplyCommand?) {
    if (clickCommand != null) {
        RxView.clicks(view)
            // 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe { o -> clickCommand!!.run() }
    }
}
/**
 * itemView点击事件不带参数
 */
@SuppressLint("CheckResult")
@BindingAdapter( "onLongClickCommand" )
fun longClickCommand(view: View, onLongClickCommand: ReplyCommand?) {
    if (onLongClickCommand != null) {
        RxView.longClicks(view)
            // 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe { o -> onLongClickCommand!!.run() }
    }
}

/**
 * itemview点击事件带参数
 */
@SuppressLint("CheckResult")
@BindingAdapter( "onClickViewCommand" )
fun clickViewCommand(view: ItemView, clickCommand: ResponseCommand<ItemView>) {
    if (clickCommand != null) {
        RxView.clicks(view)
            // 1秒内不可重复点击或仅响应一次事件
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe { o -> clickCommand!!.accept(view) }
    }
}

/**
 * 设置itemview的内容
 */
@SuppressLint("CheckResult")
@BindingAdapter( "rightContent" )
fun rightContent(view: ItemInputView,content:String) {
  view.content = content
}

/**
 * 设置itemview的内容
 */
@SuppressLint("CheckResult")
@BindingAdapter( "leftLabel" )
fun leftLabel(view: ItemInputView,content:String) {
    view.setmLabel(content)
}

