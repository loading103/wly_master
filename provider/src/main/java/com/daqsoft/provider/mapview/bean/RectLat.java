package com.daqsoft.provider.mapview.bean;

/**
 * 矩阵经纬度
 * @author MouJunFeng
 * @time 2018-3-14.
 * @since JDK 1.8
 * @version 1.0.0
 */

public class RectLat {
    /**
     * 左
     */
    private double left;
    /**
     * 上
     */
    private double top;
    /**
     * 右
     */
    private double right;
    /**
     * 下
     */
    private double bottom;

    public double getLeft() {
        return left;
    }

    public void setLeft(double left) {
        this.left = left;
    }

    public double getTop() {
        return top;
    }

    public void setTop(double top) {
        this.top = top;
    }

    public double getRight() {
        return right;
    }

    public void setRight(double right) {
        this.right = right;
    }

    public double getBottom() {
        return bottom;
    }

    public void setBottom(double bottom) {
        this.bottom = bottom;
    }
}
