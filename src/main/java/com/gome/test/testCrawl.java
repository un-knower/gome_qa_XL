package com.gome.test;

import com.gome.util.CrawlGomeInfo;

/**
 * Created by lixiang-ds3 on 2017/9/5.
 */
public class testCrawl {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String keywords = "洗衣机";
        CrawlGomeInfo crawlGomeInfo = new CrawlGomeInfo();
        System.out.println(crawlGomeInfo.getGomeProductInfo(keywords));
    }
}
