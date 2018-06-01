package com.gome.process; /**
 * Created by lixiang-ds3 on 2016/12/11.
 */


import com.gome.buildindex.Blog_attr;
import com.gome.buildindex.JsonUtil;
import com.gome.buildindex.ResultQA;
import com.gome.search.searchAttr;
import com.gome.search.searchCatId;
import com.gome.util.RedisUtils;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.elasticsearch.common.settings.ImmutableSettings;

//商品咨询
public class ElasticSearch_consultation {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearch_consultation.class);


    static JiebaSegmenter segmenter = new JiebaSegmenter();

    private static SearchRequestBuilder builder = null;
    private static SearchRequestBuilder builder2 = null;
    private static SimpleDateFormat sf = null;
    public static  Client client = null;
    private static searchCatId cat = null;
    private static Set<String> biaodian = ESUtil.categoryIds;
    private static searchAttr attr = null;
    private static Jedis jedis = null;
    static{
        sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        client = ESUtil.getClient ();
        builder = client.prepareSearch("chatbot_attr").setTypes("chats").setSearchType(SearchType.DEFAULT)
                .setFrom(0).setSize(1000);
        builder2 = client.prepareSearch("chatbot_product").setTypes("chats").setSearchType(SearchType.DEFAULT)
                .setFrom(0).setSize(1000);

        cat = searchCatId.getInstance();

        jedis= RedisUtils.getJedis();

        attr = searchAttr.getInstance();
    }

    //自动问答
    public static  ResultQA  queryAnswer(String uid, String key, String pid_skuid) {

        String date = sf.format(new Date());
        logger.info("current time : {}, gome QA queryAnswer key:{}.", date, key);
        String catId = cat.Search(pid_skuid);
   //sku_ 周磊 黄勇标识
        if(catId == "" || catId == null){
            try {
                catId = jedis.get("sku_" + pid_skuid);
            }catch(Exception e){
                return new ResultQA("0x001", key, "未查到商品相关信息，麻烦您到商品页面查看商品介绍。");
            }
        }


//        if(catId.equals("")){
//            try {
//                List<ResultQA> result = ElasticSearch_sent2vec.queryAnswer(key, 0);
//                return result.get(0);
//            }catch(Exception e){
//                return new ResultQA("0x001", key, "未查到商品相关信息，麻烦您到商品页面查看商品介绍。");
//            }
//        }
        logger.info("current skuid : {}, current catId:{}.", pid_skuid, catId);
        //标点过滤
        for (String bd : biaodian) {
            key = key.replace(bd, "");
        }
        try {
            //去除html标签
            String regEx_html = "<[^>]+>";
            Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            Matcher m_html = p_html.matcher(key);
            key = m_html.replaceAll("");
            int count = 0;

            if (!attr.Search(key)) {
                List<ResultQA> list = ElasticSearch_sent2vec.queryAnswer(uid, key, 0);
                if(list != null){
                    try{
                        return list.get(0);
                    }catch(Exception e){
                        return new ResultQA("0x001", key, "好好学习，天天向上，小美会努力学习的，主人要等我哟");
                    }
                }else {
                    return new ResultQA("0x001", key, "讨厌，人家没听懂啦，换个说法试试好吗！");
                }
            } else {
                List<SegToken> seg = segmenter.process(key, JiebaSegmenter.SegMode.SEARCH);
                String in = "";
                for (SegToken segToken : seg) {
                    in += segToken.word.getToken() + " ";
                }
                in = in.trim();
                in = in.replace("大容量","大 容量");
                in = in.replace("商品","");
                in = in.replace("这件","");
                in = in.replace("的","");
                in = in.replace("该","");
                in = in.replace("是","");
                in = in.replace("什么","");
                in = in.replace("这","");
                in = in.replace("个","");
                in = in.toLowerCase();
                System.out.println(in);
                BoolQueryBuilder qb = QueryBuilders.boolQuery().should(new QueryStringQueryBuilder(in).field("attr"));
                builder.setQuery(qb);
                SearchResponse response = builder.execute().actionGet();

                SearchHits hits = response.getHits();

                if (hits.totalHits() > 0) {
                    for (SearchHit hit : hits) {
                        String attr = hit.getSource().get("attr").toString();
                        String ans = hit.getSource().get("ans").toString();
                        String Id = hit.getSource().get("catId").toString();

                        Double score = Double.parseDouble(String.valueOf(hit.getScore()));

                        if(score > 0.5 && Id.equals(catId)){
                            Map<String, String> map = ElasticSearch_consultation.searchProduct(pid_skuid);
                            String answer = "";
                            ans = ans.replace("$(ID)", pid_skuid);
//                            System.out.println(ans);
                            for(Map.Entry<String, String> entry : map.entrySet()){
                                String k = entry.getKey();
                                k = k.trim();
                                String v = entry.getValue();
                                ans = ans.replace("$(" + k + ")", v);
                            }
                            if(ans.contains("$(")) {
                                logger.info("current exception: {}.", "信息中含有$(");
                                return new ResultQA("0x001", key, "未查到商品相关信息，麻烦您到商品页面查看商品介绍。");
                            }else {
                                return new ResultQA("0x001", key, ans, "-3", "-4");
                        }
                        }
                    }
                }else{
                    logger.info("current exception: {}.", "未检索到信息");
                    return new ResultQA("0x001", key, "未查到商品相关信息，麻烦您到商品页面查看商品介绍。");
                }
            }
            } catch(Exception e){
                 logger.info("current exception: {}.", e.getMessage());
                 return new ResultQA("0x001", key, "未查到商品相关信息，麻烦您到商品页面查看商品介绍。");
            }
        logger.info("current exception: {}.", "没有答案返回默认");
        return new ResultQA("0x001", key, "未查到商品相关信息，麻烦您到商品页面查看商品介绍。");
    }

    public static Map<String, String> searchProduct(String pid_skuid){
        BoolQueryBuilder qb2 = QueryBuilders.boolQuery().must(new QueryStringQueryBuilder(pid_skuid).field("skuId"));
        builder2.setQuery(qb2);
        SearchResponse response2 = builder2.execute().actionGet();
        Map<String, String> map = new HashMap<String, String>();
        SearchHits search = response2.getHits();
        if(search.totalHits() > 0){
            String result = search.getAt(0).getSource().get("attr").toString();
            String[] attr_value = result.split(" ");
            for(String str : attr_value){
                String[] info = str.split(":");
                try {
                    map.put(info[0], info[1]);
                }catch(Exception e){
                    continue;
                }
            }
        }
        return map;
    }

    public static Boolean deleteIndex(String _id){
        String date = sf.format(new Date());
        logger.info("current time: {}, gome QA deleteIndex id:{}.", date, _id);

        try{
            client.prepareDelete("chatbot_attr","chats",_id).get();
        }catch(Exception e){
            return false;
        }
        return true;
    }

    public static Boolean addQuestionAnswer(String id, String question, String answer) {
        String date = sf.format(new Date());

        logger.info("current time: {}, gome QA addQuestionAnswer question and answer: {}.", date, question+"####"+answer);
        try {
//            List<SegToken> seg = segmenter.process(question, JiebaSegmenter.SegMode.INDEX);
//            String in = "";
//            for (SegToken segToken : seg) {
//                in += segToken.word.getToken() + " ";
//            }
//            in = in.trim();
            question = question.toLowerCase();
            String data = JsonUtil.model2Json_attr(new Blog_attr(id, question, answer));
            client.prepareIndex("chatbot_attr", "chats").setSource(data).execute().actionGet();

        }catch(Exception e){
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
            ResultQA list = ElasticSearch_consultation.queryAnswer("000","品牌", "9134561074-1123501577");
            System.out.println(list.getAnswer() + list.getCat1() + list.getCat2());

//        ElasticSearch_consultation.deleteIndex("AVv1qdtFJT1kO0YtV7dZ");
//        ElasticSearch_consultation.addQuestionAnswer("cat10000092", "硬盘 容量", "商品（$(ID)）硬盘容量是$(hard_capacity)，太棒啦~");
    }
}