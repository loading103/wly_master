package com.daqsoft.travelCultureModule.clubActivity.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ListView;


public class InScrollViewListView extends ListView {
   public InScrollViewListView(Context context) {
       super(context);
   }

   public InScrollViewListView(Context context, AttributeSet attrs) {
       super(context, attrs);
   }

   public InScrollViewListView(Context context, AttributeSet attrs, int defStyleAttr) {
       super(context, attrs, defStyleAttr);
   }

   @TargetApi(Build.VERSION_CODES.LOLLIPOP)
   public InScrollViewListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
       super(context, attrs, defStyleAttr, defStyleRes);
   }

   @Override
   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
       int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2, // 设计一个较大的值和AT_MOST模式
               MeasureSpec.AT_MOST);
       super.onMeasure(widthMeasureSpec, newHeightMeasureSpec);//再调用原方法测量
   }
}
