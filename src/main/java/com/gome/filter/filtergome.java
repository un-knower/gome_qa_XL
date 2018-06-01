package com.gome.filter;


import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Created by lixiang-ds3 on 2017/6/22.
 */
public class filtergome {

    private static Client client = null;
    private static org.elasticsearch.common.settings.Settings settings = null;
    private static SearchRequestBuilder builder = null;

    public static void main(String[] args) {
        //如果集群名是默认的elasticsearch没有改变，settings可以不哟红设置
        settings = org.elasticsearch.common.settings.Settings.builder().put("cluster.name", "ai-application").put("client.transport.sniff", true).build();
        InetSocketAddress isa = new InetSocketAddress("10.112.167.19", 9300);
        client = TransportClient.builder().settings(settings).build().addTransportAddress(new InetSocketTransportAddress(isa));

        builder = client.prepareSearch("news_info").setTypes("news_info").setSearchType(SearchType.DEFAULT)
                .setFrom(0).setSize(10000);


        SearchResponse response = builder.execute().actionGet();

        SearchHits hits = response.getHits();
//        System.out.println(hits.totalHits());
        if (hits.totalHits() > 0) {
            for (SearchHit hit : hits) {
                String attr = hit.getSource().get("title").toString();
                if(attr.contains("国 美")){
                    String _id = hit.getId().toString();
                    System.out.println(_id);
                    try{
                        client.prepareDelete("news_info","news_info",_id).get();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
               System.out.println(attr);

            }
        }
    }
}
