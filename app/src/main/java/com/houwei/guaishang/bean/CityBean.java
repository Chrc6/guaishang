package com.houwei.guaishang.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2014/6/16 0016.
 */

public class CityBean  implements Comparable<CityBean>,Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -9120121066697493924L;
	private String cityid;
    private String cityname;
    //首字母
    private String firstWord;

	public String getFirstWord() {
		return firstWord;
	}

	public void setFirstWord(String firstWord) {
		this.firstWord = firstWord;
	}

	@Override
	public int compareTo(CityBean arg0) {
	     return this.firstWord.compareTo(arg0.firstWord);
	}
    
    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getCityid() {
        return cityid;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
    }


}
