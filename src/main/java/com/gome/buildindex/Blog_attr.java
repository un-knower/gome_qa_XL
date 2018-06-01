package com.gome.buildindex;

/**
 * Created by lixiang-ds3 on 2017/4/24.
 */
public class Blog_attr {

    private String catId;
    private String attr;
    private String ans;

    @Override
    public String toString() {
        return "Blog_attr{" +
                "catId='" + catId + '\'' +
                ", attr='" + attr + '\'' +
                ", ans='" + ans + '\'' +
                '}';
    }

    public Blog_attr(String catId, String attr, String ans) {
        this.catId = catId;
        this.attr = attr;
        this.ans = ans;
    }
    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

}
