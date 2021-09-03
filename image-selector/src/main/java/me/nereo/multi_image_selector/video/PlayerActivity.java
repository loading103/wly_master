package me.nereo.multi_image_selector.video;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.ijk.IjkPlayerFactory;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.L;

import me.nereo.multi_image_selector.R;

/**
 * 视频播放
 *
 * @author 严博
 * @version 1.0.0
 * @date 2018.05.07
 * @since JDK 1.8
 */

public class PlayerActivity extends AppCompatActivity {

    /**
     * 视频播放view
     */
    private VideoView mVideoView;
    private TextView headerTitleTV;
    private ImageView headerBackIV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mVideoView = (VideoView) findViewById(R.id.player);
        headerBackIV = (ImageView) findViewById(R.id.img_close_page);
        headerBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Intent intent = getIntent();
        if (intent != null) {
            StandardVideoController controller = new StandardVideoController(this);
            boolean isLive = intent.getBooleanExtra("isLive", false);
            String title = intent.getStringExtra("title");
            if (isLive) {
                mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_16_9);
            } else {

            }
            controller.addDefaultControlComponent(title, isLive);
            mVideoView.setVideoController(controller);

            mVideoView.setUrl(intent.getStringExtra("url"));
            // 播放状态监听
            mVideoView.addOnStateChangeListener(mOnVideoViewStateChangeListener);
            // 使用IjkPlayer解码
            mVideoView.setPlayerFactory(IjkPlayerFactory.create());
            mVideoView.start();
        }
    }

    private VideoView.OnStateChangeListener mOnVideoViewStateChangeListener = new VideoView.OnStateChangeListener() {
        @Override
        public void onPlayerStateChanged(int playerState) {
            switch (playerState) {
                // 小屏
                case VideoView.PLAYER_NORMAL:
                    break;
                // 全屏
                case VideoView.PLAYER_FULL_SCREEN:
                    break;
            }
        }

        @Override
        public void onPlayStateChanged(int playState) {
            switch (playState) {
                case VideoView.STATE_IDLE:
                    break;
                case VideoView.STATE_PREPARING:
                    break;
                case VideoView.STATE_PREPARED:
                    // 需在此时获取视频宽高
                    int[] videoSize = mVideoView.getVideoSize();
                    L.d("视频宽：" + videoSize[0]);
                    L.d("视频高：" + videoSize[1]);
                    break;
                case VideoView.STATE_PLAYING:
                    break;
                case VideoView.STATE_PAUSED:
                    break;
                case VideoView.STATE_BUFFERING:
                    break;
                case VideoView.STATE_BUFFERED:
                    break;
                case VideoView.STATE_PLAYBACK_COMPLETED:
                    break;
                case VideoView.STATE_ERROR:
                    break;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.release();
    }


    @Override
    public void onBackPressed() {
        if (!mVideoView.onBackPressed()) {
            super.onBackPressed();
        }
    }

}
