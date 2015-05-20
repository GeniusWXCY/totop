package com.genius.totop.model;

public class VisitInfo {

    public String imei;

    public String id;

    public String area;
    /**
     * 维度
     */
    public double latitude;

    /**
     * 经度
     */
    public double longitude;

    public VisitInfo(String imei,String id,String area, double latitude,double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.area = area;
        this.id = id;
        this.imei = imei;
    }
}
