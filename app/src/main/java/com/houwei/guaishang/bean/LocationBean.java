package com.houwei.guaishang.bean;

import java.io.Serializable;

/**
 * 位置信息结果集
 */
@SuppressWarnings("serial")
public class LocationBean implements Serializable{

    //地址
    private String address = "";
    private String city = "";
    private String district="";
    //经度
    private double latitude;
    //纬度
    private double longitude;

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
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
        return address == null || "".equals(address)?"未知":address;
    }

    public void setAddress(String address) {
        this.address = address == null ? "":address;
    }

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city  = city == null ? "":city;
	}
}
