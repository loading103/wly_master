package com.daqsoft.baselib.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;

import androidx.core.view.ViewCompat;

/**
 *
 *
 */
public class SwipeRefreshLayout extends androidx.swiperefreshlayout.widget.SwipeRefreshLayout
{
    public SwipeRefreshLayout(Context context)
    {
        super(context);
    }

    public SwipeRefreshLayout(Context context, AttributeSet attrs)
    {
        super(context,attrs);
    }

    @Override
    public boolean canChildScrollUp()
    {
        View target=getChildAt(0);
        if(target instanceof AbsListView)
        {
            final AbsListView absListView=(AbsListView)target;
            return absListView.getChildCount()>0
                    &&(absListView.getFirstVisiblePosition()>0||absListView.getChildAt(0)
                    .getTop()<absListView.getPaddingTop());
        }
        else
            return ViewCompat.canScrollVertically(target,-1);
    }
}
