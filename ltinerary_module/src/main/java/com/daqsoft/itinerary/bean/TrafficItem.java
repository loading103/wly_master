package com.daqsoft.itinerary.bean;

import com.daqsoft.provider.Constants;

/**
 * @Author： 邓益千
 * @Create by：   2020/6/12 14:55
 * @Description： 交通Item
 */
public class TrafficItem implements Comparable<TrafficItem>{

    private Integer number = 0;
    private String type;
    private String startName;
    private String startTime;
    private String endName;
    private String endTime;
    private String consumeTime;
    private String distance;
    private String code;
    private String line;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        switch (type){
            case Constants.autoBus:
                setNumber(0);
                break;
            case Constants.bus:
                setNumber(1);
                break;
            case Constants.train:
                setNumber(2);
                break;
            case Constants.aviation:
                setNumber(3);
                break;
            case Constants.selfDrive:
                setNumber(4);
                break;
        }
    }

    public String getStartName() {
        return startName;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndName() {
        return endName;
    }

    public void setEndName(String endName) {
        this.endName = endName;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getConsumeTime() {
        return consumeTime;
    }

    public void setConsumeTime(String consumeTime) {
        this.consumeTime = consumeTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }


    @Override
    public int compareTo(TrafficItem trafficItem) {
        return number.compareTo(trafficItem.number);
    }
}
