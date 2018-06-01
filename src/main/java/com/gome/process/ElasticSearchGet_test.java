package com.gome.process; /**
 * Created by lixiang-ds3 on 2016/12/11.
 */


import com.gome.buildindex.Blog_pc;
import com.gome.buildindex.JsonUtil;
import com.gome.buildindex.ResultQA;
import com.gome.buildindex.ResultRto;
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

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.elasticsearch.common.settings.ImmutableSettings;


public class ElasticSearchGet_test {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchGet_test.class);


    private static JiebaSegmenter segmenter = new JiebaSegmenter();

    private static SearchRequestBuilder builder = null;
    private static SimpleDateFormat sf = null;
    public static  Client client = null;
    private static Set<String> biaodian = ESUtil.categoryIds;

    protected static List<String> default_ans = Arrays.asList("我怎么不知道呢", "果果永远是主人的小伙伴，我们都要开开心心哦！",
            "北鼻你要一直开心哦，果果相信你是最棒的。", "嘿嘿", "Darling~ gome love you！", "mua~亲亲",
            "您对果果最好了~", "24小时不休，有我在不怕~~~", "那是什么", "我很愿意和您在一起共度接下来的美好时光喔~~~", "好好学习，天天向上，果果会努力学习的，主人要等我哟",
            "哇，天气真不错呢，主人我们要放松心情哦", "亲，你说什么呢？", "爱你！么么哒！", "讨厌，人家没听懂啦，换个说法试试好吗！",
            "果果没懂，我会好好学习的，如有问题请<a target=\"_blank\" href=\"http://chat5.gome.com.cn/live800/chatClient/chatbox.jsp?companyID=3&customerID=3&info=userId%3D42562675198%26loginname%3D960557418_n%26grade%3D5%26name%3D960557418_n%26memo%3D%26hashCode%3Dae236153492c725039bad57c570c5324%26timestamp%3D1483603337358&page=0&enterurl=http://www.gome.com.cn/&areaCode=11010500%257C%25E5%258C%2597%25E4%25BA%25AC%25E5%25B8%2582%25E6%25B5%25B7%25E6%25B7%2580%25E5%258C%25BA%25E6%259B%2599%25E5%2585%2589%25E8%25A1%2597%25E9%2581%2593%257C11010000%257C11000000%257C110105025&shopname=%25E5%259C%25A8%25E7%25BA%25BF%25E5%25AE%25A2%25E6%259C%258D&back=1&fromChaterInfoBox=1&skillId=173&subject=%E9%85%8D%E9%80%81%E5%B8%AE%E5%8A%A9\"><strong><span style=\"color: rgb(51,102,255);\">点击这里</span></strong></a>联系人工客服哦~ ;"
    );
    static{
        sf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        client = ESUtil.getClient ();
        builder = client.prepareSearch("chatbot_pc").setTypes("chats").setSearchType(SearchType.DEFAULT)
                .setFrom(0).setSize(1000);
    }

    //自动问答
    public static List<ResultQA>  queryAnswer(String key, int flag) {

        String date = sf.format(new Date());
        logger.info("current time : {}, gome QA queryAnswer key:{}.", date, key);
        List<ResultQA> result = new ArrayList<ResultQA>();
        //标记是否有返回
        Boolean su = false;
        //标点过滤
        for(String bd : biaodian){
            key = key.replace(bd, "");
        }
        try {

            List<SegToken> seg = segmenter.process(key, JiebaSegmenter.SegMode.SEARCH);
            String in = "";
            for(SegToken segToken : seg){
                in += segToken.word.getToken() + " ";
            }
            in = in.trim();
            System.out.println(in);

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
                        String question = hit.getSource().get("question").toString();
                        String ans = hit.getSource().get("answer").toString();

                        question = question.trim();
                        System.out.println(in + "----"+question + "--------" + hit.getScore());
                        float score = hit.getScore();
                        if(score > 0.5) {
                            if(in.equals(question)){
                                String answer = "";
                                if(ans.contains("%%%%%")){
                                    String[] random = ans.split("%%%%%");
                                    int index = (int) (Math.random() * random.length);
                                    answer = random[index];
                                }else {
                                    answer = ans;
                                }

                                ResultQA qa = new ResultQA(hit.getId(), question, answer);
                                result.add(qa);
                                return result;
                            }
                            similar.add(question);
                            map.put(question, ans);
                        }
                    }

                    if(map.size() == 0 || similar.size() == 0){

                        ResultQA qa = new ResultQA("0x001", "未匹配问题", default_ans.get((int) (Math.random() * default_ans.size())));
                        result.add(qa);
                        return result;
                    }

                    for(String sim : similar){
                        if(sim.contains(in)){
                            ResultQA qa = new ResultQA("0x001", sim, map.get(sim));
                            result.add(qa);
                            return result;
                        }
                        que_score.put(sim, Sent2vecCal.cosineSimilarity(in, sim));
                        System.out.println(sim + "--------------" + Sent2vecCal.cosineSimilarity(in, sim));
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
                        if(++count == 5){
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

                        ResultQA qa = new ResultQA(count+"",question , answer);
                        result.add(qa);
                    }
                    su = true;
                } else {
                    ResultQA qa = new ResultQA("0x001", "未匹配问题", default_ans.get((int) (Math.random() * default_ans.size())));
                    result.add(qa);
                    return result;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(su){
            return result;
        }else {
            ResultQA qa = new ResultQA("0x001", "未匹配问题", default_ans.get((int) (Math.random() * default_ans.size())));
            result.add(qa);
            return result;
        }
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

    //添加索引
    public static Boolean addQuestionAnswer(String cat, String question, String answer) {
        String date = sf.format(new Date());

        logger.info("current time: {}, gome QA addQuestionAnswer question and answer: {}.", date, question+"####"+answer);
        try {
            List<SegToken> seg = segmenter.process(question, JiebaSegmenter.SegMode.INDEX);
            String in = "";
            for (SegToken segToken : seg) {
                in += segToken.word.getToken() + " ";
            }
            in = in.trim();

            String data = JsonUtil.model2Json_pc(new Blog_pc(cat, question, answer));
            client.prepareIndex("chatbot_pc", "chats").setSource(data).execute().actionGet();

        }catch(Exception e){
            return false;
        }

        return true;
    }

    //删除索引
    public static Boolean deleteIndex(String _id){
        String date = sf.format(new Date());
        logger.info("current time: {}, gome QA deleteIndex id:{}.", date, _id);

        try{
            client.prepareDelete("chatbot_pc","chats",_id).get();
        }catch(Exception e){
            return false;
        }
        return true;
    }
    public static void main(String[] args){
//        ElasticSearchGet_test test = new ElasticSearchGet_test();
//        List<ResultQA> list = test.queryAnswer("什么是自营红券？",0);
//        for(ResultQA re : list){
//            System.out.println(re.getId() + "\t" + re.getQuestion() + "\t" + re.getAnswer());
//        }
//            ElasticSearchGet_test.deleteIndex("AV1Zodcz4opwxjZAjpGe");
//         System.out.println(ElasticSearch_sent2vec.queryHint("我要退货"));
    //     ElasticSearchGet_test.addQuestionAnswer("会员专区##会员介绍##成长值","什么 是 成长 值 ?",   "话说，成长值是国美在线会员通过注册登录、购物、评价、晒单等行为所获得的奖励分数。会员累积的成长值总额与购物天数两个因素共同决定国美在线会员的级别。成长值存在增加和减少两种变更情况。");
         //  System.out.println(segmenter.process("ansj中文分词在这里如果你遇到什么问题都可以联系我.我一定尽我所能.", JiebaSegmenter.SegMode.SEARCH));

    }
}