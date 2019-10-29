package com.startimes.startmap.map;

/**
 * 用来记录用户当前位置
 *
 * @author jack
 * @version 1.0
 * @since 2019/9/4 16:50
 */
public class MapPlace {
    /**
     * 中国坐标系
     */
    public static final String GCJ = "GCJ02";
    /**
     * 国际通用坐标系
     */
    public static final String WGS = "WGS84";
    /**
     * 百度专用坐标系
     */
    public static final String BD = "BD09";

    /**
     * 坐标系类型
     */
    private String coordType;
    /**
     * 经度
     */
    private double longitude;
    /**
     * 纬度
     */
    private double latitude;
    /**
     * 地址
     */
    private String address;

    /**
     * 这三个参数必须传递
     *
     * @param coordType 坐标系类型
     * @param longitude 经度
     * @param latitude  纬度
     */
    public MapPlace(String coordType, double longitude, double latitude) {
        this.coordType = coordType;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getCoordType() {
        return coordType;
    }

    public void setCoordType(String coordType) {
        this.coordType = coordType;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
