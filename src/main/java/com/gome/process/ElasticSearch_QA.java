package com.gome.process; /**
 * Created by lixiang-ds3 on 2016/12/11.
 */


import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gome.buildindex.Blog;
import com.gome.buildindex.JsonUtil;
import com.gome.buildindex.ResultQA;
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


public class ElasticSearch_QA {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearch_sent2vec.class);


    static JiebaSegmenter segmenter = new JiebaSegmenter();

    private static SearchRequestBuilder builder = null;
    private static SearchRequestBuilder builder2 = null;
    private static SimpleDateFormat sf = null;
    private static IDFCal idfCal = null;
    public static  Client client = null;
    private static Set<String> biaodian = ESUtil.categoryIds;

    protected static List<String> default_ans = Arrays.asList("那是什么", "好好学习，天天向上，小美会努力学习的，主人要等我哟",
            "亲，你说什么呢？", "讨厌，人家没听懂啦，换个说法试试好吗！"
    );

    static{
        sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        client = ESUtil.getClient ();
        builder = client.prepareSearch("chatbot_v1").setTypes("chats").setSearchType(SearchType.DEFAULT)
                .setFrom(0).setSize(1000);
        idfCal = new IDFCal();
    }

    //自动问答
    public static String  queryAnswer(String key) {
        String date = sf.format(new Date());
        logger.info("current time : {}, gome QA queryAnswer key:{}.", date, key);
        //标点过滤
        for(String bd : biaodian){
            key = key.replace(bd, "");
        }

        try {
            //去除html标签
            String regEx_html = "<[^>]+>";
            Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            Matcher m_html = p_html.matcher(key);
            key = m_html.replaceAll("");


            List<SegToken> seg = segmenter.process(key, JiebaSegmenter.SegMode.SEARCH);
            String in = "";
            for(SegToken segToken : seg){
                in += segToken.word.getToken() + " ";
            }
            in = in.trim();


            BoolQueryBuilder qb = QueryBuilders.boolQuery().should(new QueryStringQueryBuilder(in).field("question"));
            builder.setQuery(qb);
            SearchResponse response = builder.execute().actionGet();

                SearchHits hits = response.getHits();
                Map<String, String> map = new HashMap<String, String>();
                Map<String, Double> que_score = new TreeMap<String, Double>();
                List<String> similar = new ArrayList<String>();

                if (hits.totalHits() > 0) {
                    for (SearchHit hit : hits) {
                        String ans = "";
                        String question = "";
                        try {
                            ans = hit.getSource().get("answer").toString();
                            question = hit.getSource().get("question").toString();
                            question = question.trim();
                        }catch(NullPointerException e){
                            continue;
                        }
                        float score = hit.getScore();
                        if(score > 0.9) {
                            if(in.equals(question)){

                                String answer = "";
                                if(ans.contains("%%%%%")){
                                    String[] random = ans.split("%%%%%");
                                    int index = (int) (Math.random() * random.length);
                                    answer = random[index];
                                }else {
                                    answer = ans;
                                }

                                return answer;
                            }
                            similar.add(question);
                            map.put(question, ans);
                        }
                    }

                    if(map.size() == 0 || similar.size() == 0){
                        return default_ans.get((int) (Math.random() * default_ans.size()));
                    }

                    for(String sim : similar){
                        que_score.put(sim, Sent2vecCal.cosineSimilarity(in, sim));
                    }

//                    ElasticSearch_sent2vec.queryHint(in);
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
                        String question = entry.getKey();
                        question = question.replaceAll("\\s", "");
                        String ans = map.get(entry.getKey());
                        String answer = "";
                        if(ans.contains("%%%%%") || ans.contains("%%%%%")){
                            String[] random = ans.split("%%%%%");
                            int index = (int) (Math.random() * random.length);
                            answer = ans;
                        }else {
                            answer = ans;
                        }
                        return answer;
                    }
                } else {

                    return default_ans.get((int) (Math.random() * default_ans.size()));
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return default_ans.get((int) (Math.random() * default_ans.size()));
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

            String data = JsonUtil.model2Json(new Blog((int) Math.random(), in, answer, "1"));
            client.prepareIndex("chatbot_v1", "chats").setSource(data).execute().actionGet();

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

            String regEx_html = "<[^>]+>";
            Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            Matcher m_html = p_html.matcher(key);
            key = m_html.replaceAll("");

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
//                    System.out.println(question + "\t" + hit.getScore());
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

}