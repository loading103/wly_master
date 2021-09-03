package me.nereo.multi_image_selector;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

import me.nereo.multi_image_selector.pic.MyImageVpAdapter;
import me.nereo.multi_image_selector.pic.PhotoViewPager;

/**
 * 图片预览的界面
 *
 * @author 严博
 * @version 1.0.0
 * @date 2018-7-11.13:37
 * @since JDK 1.8
 */
public class BigImgActivity extends AppCompatActivity {
    private PhotoViewPager mViewPager;
    private int currentPosition;
    private MyImageVpAdapter adapter;
    private TextView mTvImageCount;
    private List<String> Urls;
    private boolean   ispl;//是不是评论界面跳转过来的
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_big_img);
        super.onCreate(savedInstanceState);
        mViewPager = (PhotoViewPager) findViewById(R.id.view_pager_photo);
        mTvImageCount = (TextView) findViewById(R.id.tv_img_num);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intent = getIntent();
        currentPosition = intent.getIntExtra("position", 0);
        Urls = intent.getStringArrayListExtra("imgList");
        ispl = intent.getBooleanExtra("ispl",false);
        adapter = new MyImageVpAdapter(Urls, this);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(currentPosition, false);
        mTvImageCount.setText(currentPosition+1 + "/" + Urls.size());
        if(ispl){
            mTvImageCount.setBackground(getResources().getDrawable(R.drawable.shape_gray_tag1));
        }
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPosition = position;
                mTvImageCount.setText(currentPosition + 1 + "/" + Urls.size());
            }
        });
    }


}
