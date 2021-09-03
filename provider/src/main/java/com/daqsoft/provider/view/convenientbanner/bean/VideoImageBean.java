package com.daqsoft.provider.view.convenientbanner.bean;

/**
 * @/**
  * @des  视频和图片公用bean
  * @author luoyi
  * @Date 2020/3/31 13:50
  */
public class VideoImageBean {

    /**
     * 视频资源地址
     */
    public String videoUrl;

    /**
     * 图片资源地址
     */
    public String imageUrl;

    /**
     * 0 图片 1视频 2 720
     */
    public int type;
    /**
     * 0 不显示状态 1直播 2 回放
     */
    public String VideoType;
    /**
     * 图片名称
     */
    public String name;


    public String getVideoType() {
        return VideoType;
    }

    public void setVideoType(String videoType) {
        VideoType = videoType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
