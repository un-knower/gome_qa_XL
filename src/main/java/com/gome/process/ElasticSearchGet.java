package com.gome.process; /**
 * Created by lixiang-ds3 on 2016/12/11.
 */


import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;

import com.gome.buildindex.ResultRto;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ElasticSearchGet {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchGet.class);


    static JiebaSegmenter segmenter = new JiebaSegmenter();

    private static SearchRequestBuilder builder = null;
    private static SearchRequestBuilder builder2 = null;
    private static SimpleDateFormat sf = null;
    private static IDFCal idfCal = null;
    public static  Client client = null;
    static{
        sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        client = ESUtil.getClient ();
        builder = client.prepareSearch("chatbot_v1").setTypes("chats").setSearchType(SearchType.DEFAULT)
                .setFrom(0).setSize(1000);
        idfCal = new IDFCal();
    }




    //自动问答
    public static String  queryAnswer(String key, int flag) {

        String date = sf.format(new Date());
        logger.info("current time : {}, gome QA queryAnswer key:{}.", date, key);

        try {
            List<SegToken> seg = segmenter.process(key, JiebaSegmenter.SegMode.SEARCH);
            String in = "";
            for(SegToken segToken : seg){
                in += segToken.word.getToken() + " ";

            }
            in = in.trim();

            if(flag == 0) {
                BoolQueryBuilder qb = QueryBuilders.boolQuery().should(new QueryStringQueryBuilder(in).field("question"));
                builder.setQuery(qb);
                SearchResponse response = builder.execute().actionGet();

                SearchHits hits = response.getHits();
                Map<String, String> map = new HashMap<String, String>();
                Map<String, Double> que_score = new TreeMap<String, Double>();
                List<String> similar = new ArrayList<String>();

                if (hits.totalHits() > 0) {
                    for (SearchHit hit : hits) {

                        String ans = hit.getSource().get("answer").toString();
                        String question = hit.getSource().get("question").toString();

                        float score = hit.getScore();
                        if(score > 0.8) {
                            similar.add(question);
                            map.put(question, ans);
                        }
                    }

                    if(map.size() == 0 || similar.size() == 0){
                        return "请<a href=\"http://chat5.gome.com.cn/live800/chatClient/chatbox.jsp?companyID=3&customerID=3&info=userId%3D42562675198%26loginname%3D960557418_n%26grade%3D5%26name%3D960557418_n%26memo%3D%26hashCode%3Dae236153492c725039bad57c570c5324%26timestamp%3D1483603337358&page=0&enterurl=http://www.gome.com.cn/&areaCode=11010500%257C%25E5%258C%2597%25E4%25BA%25AC%25E5%25B8%2582%25E6%25B5%25B7%25E6%25B7%2580%25E5%258C%25BA%25E6%259B%2599%25E5%2585%2589%25E8%25A1%2597%25E9%2581%2593%257C11010000%257C11000000%257C110105025&shopname=%25E5%259C%25A8%25E7%25BA%25BF%25E5%25AE%25A2%25E6%259C%258D&back=1&fromChaterInfoBox=1&skillId=173&subject=%E9%85%8D%E9%80%81%E5%B8%AE%E5%8A%A9\"><strong><span style=\"color: rgb(51,102,255);\">点击这里</span></strong></a>" +
                                "联系人工客服哦~ ;";
                    }

                    for(String sim : similar){

                       que_score.put(sim, Sent2vecCal.cosineSimilarity(in, sim));
//                        System.out.println(sim + "--------------" + Sent2vecCal.cosineSimilarity(in, sim));
                    }

//                    System.out.println(ElasticSearchGet_sent2vec.queryHint(in));

                    List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(que_score.entrySet());
                    // 通过比较器来实现排序
                    Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
                        public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                            // 降序排序
                            return o2.getValue().compareTo(o1.getValue());
                        }
                    });

                    int count = 0;
                    for(Map.Entry<String, Double> entry : list){
                        if(++count == 2){
                            break;
                        }
                        String ans = map.get(entry.getKey());
                        if(ans.contains("%%%%")){
                            String[] random = ans.split("%%%%");
                            int index = (int) (Math.random() * random.length);
                            return random[index].replace("%", "");
                        }else {
                            return ans;
                        }
                    }

                } else {
                    return "请<a href=\"http://chat5.gome.com.cn/live800/chatClient/chatbox.jsp?companyID=3&customerID=3&info=userId%3D42562675198%26loginname%3D960557418_n%26grade%3D5%26name%3D960557418_n%26memo%3D%26hashCode%3Dae236153492c725039bad57c570c5324%26timestamp%3D1483603337358&page=0&enterurl=http://www.gome.com.cn/&areaCode=11010500%257C%25E5%258C%2597%25E4%25BA%25AC%25E5%25B8%2582%25E6%25B5%25B7%25E6%25B7%2580%25E5%258C%25BA%25E6%259B%2599%25E5%2585%2589%25E8%25A1%2597%25E9%2581%2593%257C11010000%257C11000000%257C110105025&shopname=%25E5%259C%25A8%25E7%25BA%25BF%25E5%25AE%25A2%25E6%259C%258D&back=1&fromChaterInfoBox=1&skillId=173&subject=%E9%85%8D%E9%80%81%E5%B8%AE%E5%8A%A9\"><strong><span style=\"color: rgb(51,102,255);\">点击这里</span></strong></a>" +
                            "联系人工客服哦~ ;";
                }
            }else if(flag == 1){
                BoolQueryBuilder qb = QueryBuilders.boolQuery().should(new QueryStringQueryBuilder(in).field("question"));
                builder2.setQuery(qb);
                SearchResponse response = builder2.execute().actionGet();

                SearchHits hits = response.getHits();

                if (hits.totalHits() > 0) {
                    for (SearchHit hit : hits) {

                        if (hit.getScore() > 1.0) {
                            return hit.getSource().get("answer").toString();
                        } else {
                            return "亲，您的问题暂时我回答不了哦~~，请联系人工客服";
                        }
                    }

                } else {
                    return "亲，您的问题暂时我回答不了哦~~，请联系人工客服";
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return "主人，果果可能没帮助到您，希望您不要不开心，果果会不断改进的~";
    }


    //添加索引
    public static Boolean addQuestionAnswer(String question, String answer) {
        String date = sf.format(new Date());

        logger.info("current time: {}, gome QA addQuestionAnswer question and answer: {}.", date, question+"####"+answer);
        try {
            List<SegToken> seg = segmenter.process(question, JiebaSegmenter.SegMode.INDEX);
            String in = "";
            for (SegToken segToken : seg) {
                in += segToken.word.getToken() + " ";
            }
            in = in.trim();

            Map map = new HashMap();
            map.put("id", (int) Math.random());
            map.put("question", in);
            map.put("answer", answer);
            map.put("pro", "1");


            client.prepareIndex("chatbot_v1", "chats").setSource(map).execute().actionGet();

        }catch(Exception e){
            return false;
        }

        return true;
    }

    //搜索提示
    public static List<ResultRto> queryHint(String key){
        String date = sf.format(new Date());
        logger.info("current time: {}, gome QA queryHint key:{}.", date, key);
        List<ResultRto> list = new ArrayList<ResultRto>();

        try {

            List<SegToken> seg = segmenter.process(key, JiebaSegmenter.SegMode.SEARCH);
            String in = "";
            for(SegToken segToken : seg){
                in += segToken.word.getToken() + " ";
            }

            BoolQueryBuilder qb = QueryBuilders.boolQuery().should(new QueryStringQueryBuilder(in).field("question"));
            builder.setQuery(qb);
            SearchResponse response = builder.execute().actionGet();

            SearchHits hits = response.getHits();
            int count = 0;
            if (hits.totalHits() > 0) {
                for (SearchHit hit : hits) {
                    // System.out.println("score:"+hit.getScore()+":\t"+hit.getSource());
                    if(++count > 5){
                        break;
                    }

                    String question = hit.getSource().get("question").toString();
                    System.out.println(question + "\t" + hit.getScore());
                    String id = hit.getId();

                    ResultRto result = new ResultRto(id, question);
                    list.add(result);
                }

            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    //删除索引
    public static Boolean deleteIndex(String _id){
        String date = sf.format(new Date());
        logger.info("current time: {}, gome QA deleteIndex id:{}.", date, _id);

        try{
            client.prepareDelete("chatbot_v1","chats",_id).get();
        }catch(Exception e){
            return false;
        }
        return true;
    }
    public static void main(String[] args){
//        System.out.println(ElasticSearch_sent2vec.queryAnswer("我要退货",0));
         System.out.println(ElasticSearchGet.queryHint("我要退货"));
         //ElasticSearchGet.addQuestionAnswer("何时安装",   "<p>如需查看具体内容，请<a href=\"http://help.gome.com.cn/question/5564.html\"><strong><span style=\"color: rgb(51,102,255);\">点击这里</span></strong></a>哦~</p>");

    }
}