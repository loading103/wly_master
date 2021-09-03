package com.daqsoft.itinerary.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.daqsoft.baselib.utils.ResourceUtils;
import com.daqsoft.baselib.utils.Utils;
import com.daqsoft.itinerary.R;

import java.util.List;

/**
 * @Author： 邓益千
 * @Create by：   2020/6/6 14:19
 * @Description：
 */
public class StringListPopupWindow<T> extends PopupWindow {

    private Context mContext;
    private int normalColor = Color.parseColor("#333333");
    private int selectedColor = normalColor;
    private StringAdapter adapter;
    private OnPopupItemClickListener itemClickListener;

    private int height = 0;

    public StringListPopupWindow(Context context) {
        super(context);
        this.mContext = context;
        initWindow();
    }

    public void setDataList(List<T> dataList) {
        adapter.setDataList(dataList);
    }

    public void setItemClickListener(OnPopupItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setSelectedColor(int color){
        this.selectedColor = color;
    }

    public void setNormalColor(int color){
        this.normalColor = color;
    }

    private void initWindow(){
        setWidth(-1);
        setHeight(height);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.argb(80,0,0,0)));

        RecyclerView recyclerView = new RecyclerView(mContext);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new StringAdapter();
        recyclerView.setAdapter(adapter);

        setContentView(recyclerView);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        if (height == 0){
            int[] screen = new int[2];
            parent.getLocationOnScreen(screen);
            height = parent.getContext().getResources().getDisplayMetrics().heightPixels;
            setHeight(height - y );
            update();
        }
    }

    private class StringAdapter<T extends StringList> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private List<T> dataList;
        private TextView tempView;

        public void setDataList(List<T> dataList){
            this.dataList = dataList;
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return dataList == null ? 0 : dataList.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.itinerary_item_string,parent,false);
            return new RecyclerView.ViewHolder(view){};
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            TextView textView = (TextView) holder.itemView;
            Object obj = dataList.get(position);
            if (obj instanceof String){
                textView.setText(dataList.get(position)+"");
            } else {
                textView.setText(dataList.get(position).getText());
            }

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tempView != null){
                        tempView.setTextColor(normalColor);
                    }
                    tempView = (TextView) view;
                    tempView.setTextColor(selectedColor);
                    if (itemClickListener != null){
                        itemClickListener.onItemClick(position);
                    }
                    dismiss();
                }
            });
        }
    }

    public interface StringList{
        String getText();
    }

    public interface OnPopupItemClickListener{
        void onItemClick(int position);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
