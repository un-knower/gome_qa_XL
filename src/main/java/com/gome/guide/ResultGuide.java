package com.gome.guide;

/**
 * Created by lixiang-ds3 on 2017/7/12.
 */
public class ResultGuide {


    public String getCatid() {
        return catid;
    }

    public void setCatid(String catid) {
        this.catid = catid;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCore() {
        return core;
    }

    public void setCore(String core) {
        this.core = core;
    }


    @Override
    public String toString() {
        return "ResultGuide{" +
                "catid='" + catid + '\'' +
                ", brand='" + brand + '\'' +
                ", core='" + core + '\'' +
                '}';
    }

    public ResultGuide(String catid, String brand, String core) {
        this.catid = catid;
        this.brand = brand;
        this.core = core;
    }

    private String catid;
    private String brand;
    private String core;


}
