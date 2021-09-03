package me.nereo.multi_image_selector.pic;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.List;

import cc.shinichi.library.view.photoview.PhotoView;
import me.nereo.multi_image_selector.R;


/**
 * 功能
 *
 * @author 严博
 * @version 1.0.0
 * @date 2019-1-30.9:54
 * @since JDK 1.8
 */

public class MyImageVpAdapter extends PagerAdapter {
    public static final String TAG = MyImageVpAdapter.class.getSimpleName();
    private List<String> imageUrls;
    private AppCompatActivity activity;

    public MyImageVpAdapter(List<String> imageUrls, AppCompatActivity activity) {
        this.imageUrls = imageUrls;
        this.activity = activity;
    }
    @Override
    public int getCount() {
        return imageUrls != null ? imageUrls.size() : 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        String url = imageUrls.get(position);
        final PhotoView photoView = new PhotoView(activity);
        Glide.with(activity)
                .load(url)
                .placeholder(R.drawable.icon_state_img_loading)
                .into(photoView);
        container.addView(photoView);

        photoView.setMaximumScale(100);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                activity.finish();
            }
        });
        return photoView;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
