package com.daqsoft.itinerary.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author： 邓益千
 * @Create by：   2020/5/18 20:38
 * @Description：
 */
public class ItineraryDetailBean implements Parcelable {

    private int id;
    private String name;
    private String travelType;
    private String personalTags;
    private String fitTags;
    private String personalTagsNames;
    private String fitTagsNames;
    private String longitude;
    private String latitude;
    private int processDay;
    private String processStart;
    private String processEnd;
    private int scenicCount;
    private int hotelCount;
    private int diningCount;
    private int venueCount;
    private int regionCount;
    private String regionCountStr;
    private String distance;
    private boolean myselfFlag;
    private int userId;
    private String sourceType;
    private String travelCityName;
    private String travelCityRegion;
    private List<AgendaBean> dayList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTravelType() {
        return travelType;
    }

    public void setTravelType(String travelType) {
        this.travelType = travelType;
    }

    public String getPersonalTags() {
        return personalTags;
    }

    public void setPersonalTags(String personalTags) {
        this.personalTags = personalTags;
    }

    public String getFitTags() {
        return fitTags;
    }

    public void setFitTags(String fitTags) {
        this.fitTags = fitTags;
    }

    public String getPersonalTagsNames() {
        return personalTagsNames;
    }

    public void setPersonalTagsNames(String personalTagsNames) {
        this.personalTagsNames = personalTagsNames;
    }

    public String getFitTagsNames() {
        return fitTagsNames;
    }

    public void setFitTagsNames(String fitTagsNames) {
        this.fitTagsNames = fitTagsNames;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public int getProcessDay() {
        return processDay;
    }

    public void setProcessDay(int processDay) {
        this.processDay = processDay;
    }

    public String getProcessStart() {
        return processStart;
    }

    public void setProcessStart(String processStart) {
        this.processStart = processStart;
    }

    public String getProcessEnd() {
        return processEnd;
    }

    public void setProcessEnd(String processEnd) {
        this.processEnd = processEnd;
    }

    public int getScenicCount() {
        return scenicCount;
    }

    public void setScenicCount(int scenicCount) {
        this.scenicCount = scenicCount;
    }

    public int getHotelCount() {
        return hotelCount;
    }

    public void setHotelCount(int hotelCount) {
        this.hotelCount = hotelCount;
    }

    public int getDiningCount() {
        return diningCount;
    }

    public void setDiningCount(int diningCount) {
        this.diningCount = diningCount;
    }

    public int getVenueCount() {
        return venueCount;
    }

    public void setVenueCount(int venueCount) {
        this.venueCount = venueCount;
    }

    public int getRegionCount() {
        return regionCount;
    }

    public void setRegionCount(int regionCount) {
        this.regionCount = regionCount;
    }

    public String getRegionCountStr() {
        return regionCountStr;
    }

    public void setRegionCountStr(String regionCountStr) {
        this.regionCountStr = regionCountStr;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public boolean isMyselfFlag() {
        return myselfFlag;
    }

    public void setMyselfFlag(boolean myselfFlag) {
        this.myselfFlag = myselfFlag;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getTravelCityName() {
        return travelCityName;
    }

    public void setTravelCityName(String travelCityName) {
        this.travelCityName = travelCityName;
    }

    public String getTravelCityRegion() {
        return travelCityRegion;
    }

    public void setTravelCityRegion(String travelCityRegion) {
        this.travelCityRegion = travelCityRegion;
    }

    public List<AgendaBean> getDayList() {
        return dayList;
    }

    public void setDayList(List<AgendaBean> dayList) {
        this.dayList = dayList;
    }

    public static class AgendaBean implements Parcelable {

        private int id;
        private String time;
        private String title;
        private String regionName;
        private String region;
        private String distance;
        private List<PlansBean> sourceList;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getRegionName() {
            return regionName;
        }

        public void setRegionName(String regionName) {
            this.regionName = regionName;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public List<PlansBean> getSourceList() {
            return sourceList;
        }

        public void setSourceList(List<PlansBean> sourceList) {
            this.sourceList = sourceList;
        }

        public static class PlansBean implements Parcelable {

            private int id;
            private int dayId;
            private int day;
            private int resourceId;
            private int type;
            private int changeIndex;
            private String resourceType;
            private String name;
            private String openTimeStart;
            private String openTimeEnd;
            private int adviceTime;
            private String adviceTimeStr;
            private String address;
            private String summary;
            private String notes;
            private String timeInterval;
            private String images;
            private String longitude;
            private String latitude;
            private int sourceStatus;
            private String region;
            private String resourceCode;
            private String sysCode;
            private String shopUrl;
            private String travelType;
            private String consumeTime;
            private String distance;
            private String line;
            private String time;

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public int getDay() {
                return day;
            }

            public void setDay(int day) {
                this.day = day;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public int getChangeIndex() {
                return changeIndex;
            }

            public void setChangeIndex(int changeIndex) {
                this.changeIndex = changeIndex;
            }

            public String getLine() {
                return line;
            }

            public void setLine(String line) {
                this.line = line;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getDayId() {
                return dayId;
            }

            public void setDayId(int dayId) {
                this.dayId = dayId;
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

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getOpenTimeStart() {
                return openTimeStart;
            }

            public void setOpenTimeStart(String openTimeStart) {
                this.openTimeStart = openTimeStart;
            }

            public String getOpenTimeEnd() {
                return openTimeEnd;
            }

            public void setOpenTimeEnd(String openTimeEnd) {
                this.openTimeEnd = openTimeEnd;
            }

            public int getAdviceTime() {
                return adviceTime;
            }

            public void setAdviceTime(int adviceTime) {
                this.adviceTime = adviceTime;
            }

            public String getAdviceTimeStr() {
                return adviceTimeStr;
            }

            public void setAdviceTimeStr(String adviceTimeStr) {
                this.adviceTimeStr = adviceTimeStr;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getSummary() {
                return summary;
            }

            public void setSummary(String summary) {
                this.summary = summary;
            }

            public String getNotes() {
                return notes;
            }

            public void setNotes(String notes) {
                this.notes = notes;
            }

            public String getTimeInterval() {
                return timeInterval;
            }

            public void setTimeInterval(String timeInterval) {
                this.timeInterval = timeInterval;
            }

            public String getImages() {
                return images;
            }

            public void setImages(String images) {
                this.images = images;
            }

            public String getLongitude() {
                return longitude;
            }

            public void setLongitude(String longitude) {
                this.longitude = longitude;
            }

            public String getLatitude() {
                return latitude;
            }

            public void setLatitude(String latitude) {
                this.latitude = latitude;
            }

            public int getSourceStatus() {
                return sourceStatus;
            }

            public void setSourceStatus(int sourceStatus) {
                this.sourceStatus = sourceStatus;
            }

            public String getRegion() {
                return region;
            }

            public void setRegion(String region) {
                this.region = region;
            }

            public String getResourceCode() {
                return resourceCode;
            }

            public void setResourceCode(String resourceCode) {
                this.resourceCode = resourceCode;
            }

            public String getSysCode() {
                return sysCode;
            }

            public void setSysCode(String sysCode) {
                this.sysCode = sysCode;
            }

            public String getShopUrl() {
                return shopUrl;
            }

            public void setShopUrl(String shopUrl) {
                this.shopUrl = shopUrl;
            }


            public String getTravelType() {
                return travelType;
            }

            public void setTravelType(String travelType) {
                this.travelType = travelType;
            }

            public String getConsumeTime() {
                return consumeTime;
            }

            public String getDistance() {
                return distance;
            }

            public void setDistance(String distance) {
                this.distance = distance;
            }

            public void setConsumeTime(String consumeTime) {
                this.consumeTime = consumeTime;
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.id);
                dest.writeInt(this.dayId);
                dest.writeInt(this.day);
                dest.writeInt(this.resourceId);
                dest.writeString(this.resourceType);
                dest.writeString(this.name);
                dest.writeString(this.openTimeStart);
                dest.writeString(this.openTimeEnd);
                dest.writeInt(this.adviceTime);
                dest.writeString(this.adviceTimeStr);
                dest.writeString(this.address);
                dest.writeString(this.summary);
                dest.writeString(this.notes);
                dest.writeString(this.timeInterval);
                dest.writeString(this.images);
                dest.writeString(this.longitude);
                dest.writeString(this.latitude);
                dest.writeInt(this.sourceStatus);
                dest.writeString(this.region);
                dest.writeString(this.resourceCode);
                dest.writeString(this.sysCode);
                dest.writeString(this.shopUrl);
                dest.writeString(this.travelType);
                dest.writeString(this.consumeTime);
                dest.writeString(this.distance);
            }

            public PlansBean() {
            }

            protected PlansBean(Parcel in) {
                this.id = in.readInt();
                this.day = in.readInt();
                this.dayId = in.readInt();
                this.resourceId = in.readInt();
                this.resourceType = in.readString();
                this.name = in.readString();
                this.openTimeStart = in.readString();
                this.openTimeEnd = in.readString();
                this.adviceTime = in.readInt();
                this.adviceTimeStr = in.readString();
                this.address = in.readString();
                this.summary = in.readString();
                this.notes = in.readString();
                this.timeInterval = in.readString();
                this.images = in.readString();
                this.longitude = in.readString();
                this.latitude = in.readString();
                this.sourceStatus = in.readInt();
                this.region = in.readString();
                this.resourceCode = in.readString();
                this.sysCode = in.readString();
                this.shopUrl = in.readString();
                this.travelType = in.readString();
                this.consumeTime = in.readString();
                this.distance = in.readString();
            }

            public static final Creator<PlansBean> CREATOR = new Creator<PlansBean>() {
                @Override
                public PlansBean createFromParcel(Parcel source) {
                    return new PlansBean(source);
                }

                @Override
                public PlansBean[] newArray(int size) {
                    return new PlansBean[size];
                }
            };
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.time);
            dest.writeString(this.title);
            dest.writeString(this.regionName);
            dest.writeString(this.region);
            dest.writeString(this.distance);
            dest.writeList(this.sourceList);
        }

        public AgendaBean() {
        }

        protected AgendaBean(Parcel in) {
            this.id = in.readInt();
            this.time = in.readString();
            this.title = in.readString();
            this.regionName = in.readString();
            this.region = in.readString();
            this.distance = in.readString();
            this.sourceList = new ArrayList<PlansBean>();
            in.readList(this.sourceList, PlansBean.class.getClassLoader());
        }

        public static final Creator<AgendaBean> CREATOR = new Creator<AgendaBean>() {
            @Override
            public AgendaBean createFromParcel(Parcel source) {
                return new AgendaBean(source);
            }

            @Override
            public AgendaBean[] newArray(int size) {
                return new AgendaBean[size];
            }
        };
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.travelType);
        dest.writeString(this.personalTags);
        dest.writeString(this.fitTags);
        dest.writeString(this.personalTagsNames);
        dest.writeString(this.fitTagsNames);
        dest.writeString(this.longitude);
        dest.writeString(this.latitude);
        dest.writeInt(this.processDay);
        dest.writeString(this.processStart);
        dest.writeString(this.processEnd);
        dest.writeInt(this.scenicCount);
        dest.writeInt(this.hotelCount);
        dest.writeInt(this.diningCount);
        dest.writeInt(this.venueCount);
        dest.writeInt(this.regionCount);
        dest.writeString(this.regionCountStr);
        dest.writeString(this.distance);
        dest.writeByte(this.myselfFlag ? (byte) 1 : (byte) 0);
        dest.writeInt(this.userId);
        dest.writeString(this.sourceType);
        dest.writeString(this.travelCityName);
        dest.writeString(this.travelCityRegion);
        dest.writeList(this.dayList);
    }

    public ItineraryDetailBean() {
    }

    protected ItineraryDetailBean(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.travelType = in.readString();
        this.personalTags = in.readString();
        this.fitTags = in.readString();
        this.personalTagsNames = in.readString();
        this.fitTagsNames = in.readString();
        this.longitude = in.readString();
        this.latitude = in.readString();
        this.processDay = in.readInt();
        this.processStart = in.readString();
        this.processEnd = in.readString();
        this.scenicCount = in.readInt();
        this.hotelCount = in.readInt();
        this.diningCount = in.readInt();
        this.venueCount = in.readInt();
        this.regionCount = in.readInt();
        this.regionCountStr = in.readString();
        this.distance = in.readString();
        this.myselfFlag = in.readByte() != 0;
        this.userId = in.readInt();
        this.sourceType = in.readString();
        this.travelCityName = in.readString();
        this.travelCityRegion = in.readString();
        this.dayList = new ArrayList<AgendaBean>();
        in.readList(this.dayList, AgendaBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<ItineraryDetailBean> CREATOR = new Parcelable.Creator<ItineraryDetailBean>() {
        @Override
        public ItineraryDetailBean createFromParcel(Parcel source) {
            return new ItineraryDetailBean(source);
        }

        @Override
        public ItineraryDetailBean[] newArray(int size) {
            return new ItineraryDetailBean[size];
        }
    };
}
