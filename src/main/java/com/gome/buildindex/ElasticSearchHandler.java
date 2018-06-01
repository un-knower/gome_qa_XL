package com.gome.buildindex; /**
 * Created by lixiang-ds3 on 2016/12/9.
 */
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.settings.ImmutableSettings;
//import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;


public class ElasticSearchHandler {

    public static void add_chatbot_v1() {
        try {
            /* 创建客户端 */
            // client startup
            Settings settings= Settings.settingsBuilder().put("client.transport.sniff", true).put("cluster.name","ai-application").build();
            Client esclient = TransportClient.builder().settings(settings).build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.112.167.19"), 9300));
            int m = 0;

            List<String> jsonData = DataFactory.getInitJsonData();

            for (int i = 0; i < jsonData.size(); i++) {
                System.out.println(m++);
                IndexResponse response = esclient.prepareIndex("chatbot_v1", "chats").setSource(jsonData.get(i)).get();
                if (response.isCreated()) {
                    System.out.println("创建成功!");
                }
            }
            esclient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void add_chatbot_attr() {
        try {
            /* 创建客户端 */
            // client startup
            Settings settings= Settings.settingsBuilder().put("client.transport.sniff", true).put("cluster.name","ai-application").build();
            Client esclient = TransportClient.builder().settings(settings).build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.112.167.19"), 9300));
            int m = 0;

            List<String> jsonData = DataFactory.getInitJsonData_attr();

            for (int i = 0; i < jsonData.size(); i++) {
                System.out.println(m++);
                IndexResponse response = esclient.prepareIndex("chatbot_attr", "chats").setSource(jsonData.get(i)).get();
                if (response.isCreated()) {
                    System.out.println("创建成功!");
                }
            }
            esclient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void add_chatbot_product() {
        try {
            /* 创建客户端 */
            // client startup
            Settings settings= Settings.settingsBuilder().put("client.transport.sniff", true).put("cluster.name","ai-application").build();
            Client esclient = TransportClient.builder().settings(settings).build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.112.167.19"), 9300));
            int m = 0;

            List<String> jsonData = DataFactory.getInitJsonData_product();

            for (int i = 0; i < jsonData.size(); i++) {
                System.out.println(m++);
                IndexResponse response = esclient.prepareIndex("chatbot_product", "chats").setSource(jsonData.get(i)).get();
                if (response.isCreated()) {
                    System.out.println("创建成功!");
                }
            }
            esclient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void add_chatbot_final() {
        try {
            /* 创建客户端 */
            // client startup
            Settings settings= Settings.settingsBuilder().put("client.transport.sniff", true).put("cluster.name","ai-application").build();
            Client esclient = TransportClient.builder().settings(settings).build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.112.167.19"), 9300));
            int m = 0;

            List<String> jsonData = DataFactory.getInitJsonData_final();

            for (int i = 0; i < jsonData.size(); i++) {
                System.out.println(m++);
                IndexResponse response = esclient.prepareIndex("chatbot_final", "chats").setSource(jsonData.get(i)).get();
                if (response.isCreated()) {
                    System.out.println("创建成功!");
                }
            }
            esclient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void add_chatbot_pc() {
        try {
            /* 创建客户端 */
            // client startup
            Settings settings= Settings.settingsBuilder().put("client.transport.sniff", true).put("cluster.name","ai-application").build();
            Client esclient = TransportClient.builder().settings(settings).build().addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.112.167.19"), 9300));
            int m = 0;

            List<String> jsonData = DataFactory.getInitJsonData_pc();

            for (int i = 0; i < jsonData.size(); i++) {
                System.out.println(m++);
                IndexResponse response = esclient.prepareIndex("chatbot_v2", "chats").setSource(jsonData.get(i)).get();
                if (response.isCreated()) {
                    System.out.println("创建成功!");
                }
            }
            esclient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args){

       // add_chatbot_attr();
       //add_chatbot_product();
       // add_chatbot_final();
        add_chatbot_pc();
    }
}