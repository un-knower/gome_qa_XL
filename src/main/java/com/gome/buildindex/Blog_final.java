package com.gome.buildindex;

/**
 * Created by lixiang-ds3 on 2017/7/17.
 */
public class Blog_final {



    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getSkuid() {
        return skuid;
    }

    public void setSkuid(String skuid) {
        this.skuid = skuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }



    public Blog_final(String catId, String skuid, String title, String attr) {
        this.catId = catId;
        this.skuid = skuid;
        this.title = title;
        this.attr = attr;
    }


    @Override
    public String toString() {
        return "Blog_final{" +
                "catId='" + catId + '\'' +
                ", skuid='" + skuid + '\'' +
                ", title='" + title + '\'' +
                ", attr='" + attr + '\'' +
                '}';
    }

    private String catId;
    private String skuid;
    private String title;
    private String attr;


}
