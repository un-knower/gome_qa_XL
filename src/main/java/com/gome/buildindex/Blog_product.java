package com.gome.buildindex;

/**
 * Created by lixiang-ds3 on 2017/4/24.
 */
public class Blog_product {

    private String skuId;
    private String attr;

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getAttr() {
        return attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public Blog_product(String skuId, String attr) {
        this.skuId = skuId;
        this.attr = attr;
    }

    @Override
    public String toString() {
        return "Blog_product{" +
                "skuId='" + skuId + '\'' +
                ", attr='" + attr + '\'' +
                '}';
    }

}
