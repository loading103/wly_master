package com.daqsoft.itinerary.bean;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

/**
 * @Author： 邓益千
 * @Create by：   2020/6/12 19:21
 * @Description：
 */
public class PlayItems implements Parcelable {

    private int resourceId;
    private String resourceType;
    private int dataId;
    private boolean isSelected;
    private boolean isJump;
    private int hour;

    public PlayItems(int resourceId, String resourceType, int dataId) {
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.dataId = dataId;
    }

    public PlayItems(int resourceId, String resourceType, int dataId, boolean isSelected) {
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.dataId = dataId;
        this.isSelected = isSelected;
    }

    public PlayItems(int resourceId, String resourceType, int dataId, boolean isSelected, boolean isJump) {
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.dataId = dataId;
        this.isSelected = isSelected;
        this.isJump = isJump;
    }

    public PlayItems(int resourceId, String resourceType, int dataId, boolean isSelected, int hour, boolean isJump) {
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.dataId = dataId;
        this.isSelected = isSelected;
        this.isJump = isJump;
        this.hour = hour;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return resourceId == ((PlayItems)obj).resourceId;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isJump() {
        return isJump;
    }

    public void setJump(boolean jump) {
        isJump = jump;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.resourceId);
        dest.writeString(this.resourceType);
        dest.writeInt(this.dataId);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isJump ? (byte) 1 : (byte) 0);
    }

    protected PlayItems(Parcel in) {
        this.resourceId = in.readInt();
        this.resourceType = in.readString();
        this.dataId = in.readInt();
        this.isSelected = in.readByte() != 0;
        this.isJump = in.readByte() != 0;
    }

    public static final Parcelable.Creator<PlayItems> CREATOR = new Parcelable.Creator<PlayItems>() {
        @Override
        public PlayItems createFromParcel(Parcel source) {
            return new PlayItems(source);
        }

        @Override
        public PlayItems[] newArray(int size) {
            return new PlayItems[size];
        }
    };


}
