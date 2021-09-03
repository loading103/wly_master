package com.daqsoft.itinerary.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author： 邓益千
 * @Create by：   2020/6/12 19:18
 * @Description：
 */
public class ItineraryLabelBean implements Parcelable {

    /**站点*/
    private String siteCode;

    /**城市名称*/
    private String cityName;

    /**出发地-城市编码*/
    private String cityRegion;

    /**开始日期*/
    private String travelStartTime;

    /**结束日期*/
    private String travelEndTime;

    /**适合景区的人群类型*/
    private String sceneryCrowd;

    /**适合场馆的人群类型*/
    private String venueCrowd;

    /**个性化标签 Name*/
    private String labelName;

    /**人群 Name*/
    private String crowdName;

    /**景区标签 Id*/
    private String scenicLabelsId;

    /**场馆标签 id*/
    private String venueLabelsId;

    /**交通方式*/
    private String travelType;

    private List<PlayItems> sourceParams =new ArrayList<>();

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityRegion() {
        return cityRegion;
    }

    public void setCityRegion(String cityRegion) {
        this.cityRegion = cityRegion;
    }

    public String getTravelStartTime() {
        return travelStartTime;
    }

    public void setTravelStartTime(String travelStartTime) {
        this.travelStartTime = travelStartTime;
    }

    public String getTravelEndTime() {
        return travelEndTime;
    }

    public void setTravelEndTime(String travelEndTime) {
        this.travelEndTime = travelEndTime;
    }

    public String getSceneryCrowd() {
        return sceneryCrowd;
    }

    public void setSceneryCrowd(String sceneryCrowd) {
        this.sceneryCrowd = sceneryCrowd;
    }

    public String getVenueCrowd() {
        return venueCrowd;
    }

    public void setVenueCrowd(String venueCrowd) {
        this.venueCrowd = venueCrowd;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getCrowdName() {
        return crowdName;
    }

    public void setCrowdName(String crowdName) {
        this.crowdName = crowdName;
    }

    public String getScenicLabelsId() {
        return scenicLabelsId;
    }

    public void setScenicLabelsId(String scenicLabelsId) {
        this.scenicLabelsId = scenicLabelsId;
    }

    public String getVenueLabelsId() {
        return venueLabelsId;
    }

    public void setVenueLabelsId(String venueLabelsId) {
        this.venueLabelsId = venueLabelsId;
    }

    public String getTravelType() {
        return travelType;
    }

    public void setTravelType(String travelType) {
        this.travelType = travelType;
    }

    public List<PlayItems> getSourceParams() {
        return sourceParams;
    }

    public void setSourceParams(List<PlayItems> sourceParams) {
        this.sourceParams = sourceParams;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.siteCode);
        dest.writeString(this.cityName);
        dest.writeString(this.cityRegion);
        dest.writeString(this.travelStartTime);
        dest.writeString(this.travelEndTime);
        dest.writeString(this.sceneryCrowd);
        dest.writeString(this.venueCrowd);
        dest.writeString(this.labelName);
        dest.writeString(this.crowdName);
        dest.writeString(this.scenicLabelsId);
        dest.writeString(this.venueLabelsId);
        dest.writeString(this.travelType);
        dest.writeTypedList(this.sourceParams);
    }

    public ItineraryLabelBean() {
    }

    protected ItineraryLabelBean(Parcel in) {
        this.siteCode = in.readString();
        this.cityName = in.readString();
        this.cityRegion = in.readString();
        this.travelStartTime = in.readString();
        this.travelEndTime = in.readString();
        this.sceneryCrowd = in.readString();
        this.venueCrowd = in.readString();
        this.labelName = in.readString();
        this.crowdName = in.readString();
        this.scenicLabelsId = in.readString();
        this.venueLabelsId = in.readString();
        this.travelType = in.readString();
        this.sourceParams = in.createTypedArrayList(PlayItems.CREATOR);
    }

    public static final Parcelable.Creator<ItineraryLabelBean> CREATOR = new Parcelable.Creator<ItineraryLabelBean>() {
        @Override
        public ItineraryLabelBean createFromParcel(Parcel source) {
            return new ItineraryLabelBean(source);
        }

        @Override
        public ItineraryLabelBean[] newArray(int size) {
            return new ItineraryLabelBean[size];
        }
    };
}
