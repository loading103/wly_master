package com.daqsoft.baselib.widgets.click

import android.view.View

fun View.onNoDoubleClick(action: () -> Unit){
     setOnClickListener(object : NoDoubleClickListener(){
         override fun onNoDoubleClick(v: View?) {
             action()
         }
     })
}