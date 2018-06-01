package com.gome.process;

import com.gome.yibot.HttpRequestUtils;

/**
 * Created by Micro_Kun on 2018/1/15
 */
public class TestGet {
    public static void main(String[] args) {

        String uid = "100039572139";
        String url = String.format("http://10.112.167.8:9900/chatbot/queryOrderInfo?uid=%s&&token=123", uid);

        String result = HttpRequestUtils.sendGet(url);

        System.out.println(result);

    }
}
