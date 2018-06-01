package com.gome.process;
/**
 * Created by lixiang-ds3 on 2016/12/11.
 *
 */

import com.alibaba.fastjson.JSONObject;
import com.gome.analysis.segment.GomeSegmenter;
import com.gome.buildindex.Blog_pc;
import com.gome.buildindex.JsonUtil;
import com.gome.buildindex.ResultQA;
import com.gome.buildindex.ResultRto;
import com.gome.filter.filterPunc;
import com.gome.guide.predictSentiment;
import com.gome.util.Config;
import com.gome.util.JSONUtils;
import com.gome.yibot.HttpRequestUtils;
import com.huaban.analysis.jieba.JiebaSegmenter;
import httpclient.httpclient.CrawlGomeInfo;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.library.DicLibrary;
import org.ansj.splitWord.analysis.ToAnalysis;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.gome.yibot.HttpRequestUtils.getAnswer;


public class ElasticSearch_sent2vec {
      //写日志的
    private static final Logger logger = LoggerFactory.getLogger(ElasticSearch_sent2vec.class);

     //结巴分词
    static JiebaSegmenter segmenter = new JiebaSegmenter();
     //
    private static SearchRequestBuilder builder = null;
    private static SearchRequestBuilder builder2 = null;
    private static SimpleDateFormat sf = null;
    private static IDFCal idfCal = null;
    public static Client client = null;
    private static Set<String> biaodian = ESUtil.categoryIds;

    private static ElasticSearch_sent2vec singleton;
    // 用户词典
    private static Set<String> set = new HashSet<String>();
    private static final String MAIN_DICT = "/key_word3.txt";
   //问题的 多对一映射
    private static Map<String, String> query_sim = new HashMap<String, String>();
    private static final String KeyWord_DICT = "/query_sim4.txt";
    //物流问题
    private static final String WULIUCUN = "/wuliuCUN.txt";
    private static Set<String> set_wuliu = new HashSet<String>();
   //安全回答
    private static Map<String, String> query_ans = new HashMap<String, String>();
    private static final String CHAT_DICT = "/chat.txt";
     //用来判断 知否进行导购的 其中一个判断条件
    private static Set<String> query_set = new HashSet<String>();
    private static final String QUERY_DICT = "/query_high.txt";
   //停用词
    private static final String STOP_WORD = "/stop_word.txt";
    private static Set<String> stop_word = new HashSet<String>();
    // 物流售前
    private static final String WULIU_SQ = "/wuliuSQ.txt";
    private static Set<String> set_SQ = new HashSet<String>();
    private static GomeSegmenter seg = null;
    private static List<String> listWuLiu = Arrays.asList("时候到货" ,"时候能到" ,"时候送达");
    //安全答案
    protected static List<String> default_ans = Arrays.asList("那是什么", "小美还在思考，怎么解决亲的问题，给小美一点时间哦，小美一直都在努力学习... ",
            "亲，你说什么呢？", "讨厌，人家没听懂啦，换个说法试试好吗！", "亲，您的反馈遇到了什么问题呢？","亲，人家没看懂啦，可以试着用一句话描述你的问题哟～"
    );

    private static final String safeUrl = "小美没有找到相关物流信息，您可以去<a target=\"_blank\" href=\"http://myhome.gome.com.cn/member/myOrder?intcmp=sy-public01004&flag=a0\"><strong><span style=\"color: rgb(51,102,255);\">我的订单</span></strong></a>查看";
    private static final  String peopleHelp = "为了尽快解决您的问题，请<a target=\"_blank\" href=\"http://chat5.gome.com.cn/live800/chatClient/chatbox.jsp?companyID=3&customerID=3&info=userId%3D42562675198%26loginname%3D960557418_n%26grade%3D5%26name%3D960557418_n%26memo%3D%26hashCode%3Dae236153492c725039bad57c570c5324%26timestamp%3D1483603337358&page=0&enterurl=http://www.gome.com.cn/&areaCode=11010500%257C%25E5%258C%2597%25E4%25BA%25AC%25E5%25B8%2582%25E6%25B5%25B7%25E6%25B7%2580%25E5%258C%25BA%25E6%259B%2599%25E5%2585%2589%25E8%25A1%2597%25E9%2581%2593%257C11010000%257C11000000%257C110105025&shopname=%25E5%259C%25A8%25E7%25BA%25BF%25E5%25AE%25A2%25E6%259C%258D&back=1&fromChaterInfoBox=1&skillId=173&subject=%E9%85%8D%E9%80%81%E5%B8%AE%E5%8A%A9\"><strong><span style=\"color: rgb(51,102,255);\">点击这里</span></strong></a> 联系人工客服哦~";
    // 静态代码块，只加载一次，属于程序优化
    static {
        ElasticSearch_sent2vec.getInstance();
        sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        client = ESUtil.getClient();
        builder = client.prepareSearch(Config.esdatabase).setTypes(Config.estable).setSearchType(SearchType.DEFAULT)
                .setFrom(0).setSize(1000);
         //ansj 分词添加用户词典
        for (String str : set) {
            DicLibrary.insert("dic", str);
        }
        //国美分词？
        seg = GomeSegmenter.getInstance();

        //智能导购加载   ？？看
        ElasticSearch_guide.getInstance();
    }
//构造函数
    private ElasticSearch_sent2vec() {
        this.loadDict();
        this.loadDict2();
//      this.loadDict3();
        this.loadDict4();
        this.loadDict5();
        this.loadDict6();
        this.loadDict7();
    }
    //synchronized 同步代码块，同一时间，只能有一个线程得到执行   ，  单例模式，
    //声明一个对象
    public static ElasticSearch_sent2vec getInstance() {
        if (singleton == null) {
            synchronized (ElasticSearch_sent2vec.class) {
                if (singleton == null) {
                    singleton = new ElasticSearch_sent2vec();
                    return singleton;
                }
            }
        }
        return singleton;
    }
//读取文件中数据
    public void loadDict() {

        InputStream is = this.getClass().getResourceAsStream(MAIN_DICT);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            long s = System.currentTimeMillis();
            while (br.ready()) {
                String line = br.readLine();
                line = line.trim();
                set.add(line);
            }
        } catch (IOException e) {
            System.err.println(String.format(Locale.getDefault(), "%s load failure!", MAIN_DICT));
        } finally {
            try {
                if (null != is)
                    is.close();
            } catch (IOException e) {
                System.err.println(String.format(Locale.getDefault(), "%s close failure!", MAIN_DICT));
            }
        }
    }
//
    public void loadDict2() {

        InputStream is = this.getClass().getResourceAsStream(KeyWord_DICT);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            long s = System.currentTimeMillis();
            while (br.ready()) {
                String line = br.readLine();
                line = line.trim();
                String[] input = line.split("\t");
                String in = input[0];
                String out = input[1];

                in = in.trim();
                out = out.trim();

                query_sim.put(in, out);
            }
        } catch (IOException e) {
            System.err.println(String.format(Locale.getDefault(), "%s load failure!", KeyWord_DICT));
        } finally {
            try {
                if (null != is)
                    is.close();
            } catch (IOException e) {
                System.err.println(String.format(Locale.getDefault(), "%s close failure!", KeyWord_DICT));
            }
        }
    }

    public void loadDict3() {

        InputStream is = this.getClass().getResourceAsStream(CHAT_DICT);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            long s = System.currentTimeMillis();
            while (br.ready()) {
                String line = br.readLine();
                line = line.trim();
                String[] input = line.split("\t");
                String in = input[0];
                String out = input[1];

                in = in.trim();
                out = out.trim();

                query_ans.put(in, out);
            }
        } catch (IOException e) {
            System.err.println(String.format(Locale.getDefault(), "%s load failure!", CHAT_DICT));
        } finally {
            try {
                if (null != is)
                    is.close();
            } catch (IOException e) {
                System.err.println(String.format(Locale.getDefault(), "%s close failure!", CHAT_DICT));
            }
        }
    }

    public void loadDict4() {

        InputStream is = this.getClass().getResourceAsStream(QUERY_DICT);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            long s = System.currentTimeMillis();
            while (br.ready()) {
                String line = br.readLine();
                line = line.trim();
                line = line.toLowerCase();
                query_set.add(line);
            }
        } catch (IOException e) {
            System.err.println(String.format(Locale.getDefault(), "%s load failure!", QUERY_DICT));
        } finally {
            try {
                if (null != is)
                    is.close();
            } catch (IOException e) {
                System.err.println(String.format(Locale.getDefault(), "%s close failure!", QUERY_DICT));
            }
        }
    }


    public void loadDict5() {

        InputStream is = this.getClass().getResourceAsStream(STOP_WORD);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            long s = System.currentTimeMillis();
            while (br.ready()) {
                String line = br.readLine();
                line = line.trim();
                stop_word.add(line);
            }
        } catch (IOException e) {
            System.err.println(String.format(Locale.getDefault(), "%s load failure!", STOP_WORD));
        } finally {
            try {
                if (null != is)
                    is.close();
            } catch (IOException e) {
                System.err.println(String.format(Locale.getDefault(), "%s close failure!", STOP_WORD));
            }
        }
    }
//读取 物流问句
    public void loadDict6() {

        InputStream is = this.getClass().getResourceAsStream(WULIUCUN);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            long s = System.currentTimeMillis();
            while (br.ready()) {
                String line = br.readLine();
//                line = filterPunc.cleanRegx(line);
                line = line.trim();
                set_wuliu.add(line);
            }
        } catch (IOException e) {
            System.err.println(String.format(Locale.getDefault(), "%s load failure!", WULIUCUN));
        } finally {
            try {
                if (null != is)
                    is.close();
            } catch (IOException e) {
                System.err.println(String.format(Locale.getDefault(), "%s close failure!", WULIUCUN));
            }
        }
    }
    //读取 物流售前关键词
    public void loadDict7() {

        InputStream is = this.getClass().getResourceAsStream(WULIU_SQ);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            long s = System.currentTimeMillis();
            while (br.ready()) {
                String line = br.readLine();
                line = line.trim();
                set_SQ.add(line);
            }
        } catch (IOException e) {
            System.err.println(String.format(Locale.getDefault(), "%s load failure!", WULIU_SQ));
        } finally {
            try {
                if (null != is)
                    is.close();
            } catch (IOException e) {
                System.err.println(String.format(Locale.getDefault(), "%s close failure!", WULIU_SQ));
            }
        }
    }


    public static void orderQuery(){
        //保留这个类，用户抽象 返回物流信息


    }
    //推送商品  爬虫从网页上抓取
    public static List<ResultQA> crawlShopping(String query) {
        List<ResultQA> result = new ArrayList<ResultQA>();

        try {
            if (!query.equals("")) {
                //先过滤掉 一下特殊字符，因为一下文本中包含这些字符
                query = query.replace("%%%%", "");
                //这个是什么 类，用来做啥，抓取？
                CrawlGomeInfo crawlGomeInfo = new CrawlGomeInfo();
                String ans = "";
                ans = crawlGomeInfo.getGomeProductInfo(query);

                System.out.println("====:"+ans);
                if(ans == ""){
                    ResultQA qa = new ResultQA("0x001", "未匹配问题", "抱歉，亲，没有找到相关的商品，请您选择其他商品购买");
                    result.add(qa);
                    return result;
                }
                ans = ans.replace("\"", "'");
                ResultQA qa = new ResultQA("0x004", query, ans,"-7","-8");
                result.add(qa);
                return result;

            } else {
                ResultQA qa = new ResultQA("0x001", "未匹配问题", "?");
                result.add(qa);
                return result;
            }
        } catch (Exception e) {
            System.err.println("error:"+e.getMessage());
            ResultQA qa = new ResultQA("0x001", "未匹配问题", default_ans.get((int) (Math.random() * default_ans.size())));
            result.add(qa);
            return result;
        }

    }

    //自动问答                                key为用户的提问
    public static List<ResultQA> queryAnswer(String uid, String key, int flag) {
       // String original_query = key;

       // key = key.replaceFirst("<","");

        //获取时间
        String date = sf.format(new Date());
        //打印日志  打印时间和 用户query
        logger.info("current time : {}, gome QA queryAnswer key:{}.", date, key);
        //存储 答案 的 列表
        List<ResultQA> result = new ArrayList<ResultQA>();

        //推送商品    当用户点击了 某品牌下的一个 领域，比如海尔 有冰箱，洗衣机，点击了冰箱之后，就会调用 下面的 if语句
        if (key.startsWith("%%%%") && key.endsWith("%%%%")) {
            try {
                return crawlShopping(key);
            } catch (Exception e) {
                ResultQA qa = new ResultQA("0x001", "未匹配问题", "抱歉，亲，没有找到相关的商品，请您选择其他商品购买");
                result.add(qa);
                return result;
            }
        }
        //物流信息

        if (key.equals("我要查物流") ){
           try{
               String url = String.format("http://10.112.167.8:9900/chatbot/queryOrderInfo?uid=%s&&token=123", uid);
               String answerJson = HttpRequestUtils.sendGet(url);
               Map<String, Object> mapJson = JSONObject.parseObject(answerJson);
               String code = mapJson.get("code").toString();
               if (!code.equals("0")){
                   ResultQA qa = new ResultQA("0x0051", "物流问题", safeUrl, "-5", "-6");
                   result.add(qa);
                   return result;
               }
               String answer = mapJson.get("body").toString();
               //成功并且无订单
               if (!answer.contains("orderId")){

                   ResultQA qa = new ResultQA("0x0050", "物流问题", "您当前没有待收货的订单哦。", "-5", "-6");
                   result.add(qa);
                   return result;
               }

               ResultQA qa = new ResultQA("0x005", key, answer, "-5", "-6");
               result.add(qa);
               return result;
           }

           catch (Exception e) {
               System.err.println("error:"+e.getMessage());
               ResultQA qa = new ResultQA("0x001", "未匹配问题", default_ans.get((int) (Math.random() * default_ans.size())));
               result.add(qa);
               return result;
           }

        }

        //标记是否有返回
        Boolean su = false;
        System.out.println("kkkkkkkkkkk---1"+key);
        //标点过滤
      /* for (String bd : biaodian)
       {
         key = key.replace(bd, "");
       }*/
        //过滤掉 所有 标点  替换上面的方法
        key = filterPunc.cleanRegx(key);
        System.out.println("kkkkkkkkkkk000000"+key);
        try {
            //去除html标签
            String regEx_html = "<[^>]+>";
            Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            Matcher m_html = p_html.matcher(key);
            key = m_html.replaceAll("");

            //空字符串判断
            if (key.equals("")) {
                ResultQA qa = new ResultQA("0x001", "未匹配问题", "?");
                result.add(qa);
                return result;
            }

            //小写
            System.out.println("kkkkkkkkkkk"+key);
            key = key.toLowerCase();
            //  根据明显的词汇   标记物流订单问题  是售前还是售后
            String flagWL = "1";
            System.out.println("set_SQ:"+set_SQ.size());
            for(String str : set_SQ)
            {
                if(key.contains(str))
                {
                    flagWL = "0";
                }
            }
            //物流信息
            String flagWLB = "1";
            for(String str : listWuLiu)
            {
                if(key.contains(str))
                {
                    flagWLB = "0";
                }
            }

           if (flagWL.equals("1") &&  flagWLB.equals("0")  ){

                try{
                    String url = String.format("http://10.112.167.8:9900/chatbot/queryOrderInfo?uid=%s&&token=123", uid);
                    String answerJson = HttpRequestUtils.sendGet(url);
                    Map<String, Object> mapJson = JSONObject.parseObject(answerJson);
                    String code = mapJson.get("code").toString();
                    if (!code.equals("0")){
                        ResultQA qa = new ResultQA("0x0051", "物流问题", safeUrl, "-5", "-6");
                        result.add(qa);
                        return result;
                    }
                    String answer = mapJson.get("body").toString();
                    //成功并且无订单
                    if (!answer.contains("orderId")){

                        ResultQA qa = new ResultQA("0x0050", "物流问题", "您当前没有待收货的订单哦。","-5", "-6");
                        result.add(qa);
                        return result;
                    }

                    ResultQA qa = new ResultQA("0x005", key, answer, "-5", "-6");
                    result.add(qa);
                    return result;
                }

                catch (Exception e) {
                    System.out.println("error:"+e.getMessage());
                    System.out.println("11111111111111");
                    ResultQA qa = new ResultQA("0x001", "未匹配问题", default_ans.get((int) (Math.random() * default_ans.size())));
                    result.add(qa);
                    return result;
                }


            }



            /*
            上面的一些操作 可以看成是  query 预处理
            流程： （1）用户query 来了以后，做一次预处理
                   （2）先判断是否有进行智能导购的 条件，进行导购，

                   （3）导购完，或者不满足导购条件，进行咨询


             */
            //是否智能导购   判断什么情况下开启导购    query_set.contains(key) query中包含一些品牌信息
            /*
              当用户 的query中包含了 一些明显的想购买意图，或者某些 商品信息，就开始进行 导购流程

             */
            if (key.contains("想买") || key.contains("想购买") || key.contains("想要") || key.contains("要买") || key.contains("要购买") || key.contains("推荐")||
                    (key.contains("哪个") && key.contains("好")) || key.contains("买个") ||query_set.contains(key)) {
                System.out.println("我是导购");
                String information = key;//过滤掉一些无用的信息，提高分词效果
                information = information.replace("想买", "");
                information = information.replace("想购买", "");
                information = information.replace("要买", "");
                information = information.replace("推荐", "");
                information = information.replace("想要", "");
                information = information.replace("要购买", "");
                information = information.replace("我", "");
                information = information.replace("哪个", "");
                information = information.replace("好", "");
                information = information.replace("买个", "");
                information = information.replace("个", "");
                // 这是国美自己的分词   可以分出 品类，品牌
                String[] label = seg.pos(information);
                String in = "";
                for (String pos : label) {
                    in += pos + " ";
                }
                in = in.trim();


                /*
                   三个判断条件，各代表什么
                   (1) 当query中含有中心词，品类词，且不含品牌词，分词后长度小于三，就调用shopping_guide  ，调用fasttext模型，预测品类，答案返回 品类下的一些品牌
                   (2) 当用户中 含有品牌，且不含有中心词、品类词、颜色，且长度等于1 ，预测出最相关的 6个，比如输入，海尔，返回海尔洗衣机，冰箱等六个
                  （3）如果不上上面的两种情况，就去商城上搜索
                 */
                //facets 属性词
                if ((in.contains("/core") || in.contains("/cat")) && label.length < 3 && !in.contains("/brand") && !in.contains("/color") && !in.contains("/facets"))
                {
                    System.out.println("属性词");
                    // 根据
                    String ans = ElasticSearch_guide.shopping_guide(information);
                    ResultQA qa = new ResultQA("0x002", key, ans,"-7","-8");
                    result.add(qa);
                    return result;
                }
                else if (in.contains("/brand") && label.length == 1 && !in.contains("/core") && !in.contains("/cat") && !in.contains("/color"))
                {
                    System.out.println("属性词1");
                    String ans = ElasticSearch_guide.shopping_guide(information);
                    ResultQA qa = new ResultQA("0x003", key, ans,"-7","-8");
                    result.add(qa);
                    return result;
                } else
                    {
                        System.out.println("属性词2");
                    String[] segcut = seg.cut(information, GomeSegmenter.SegMode.SEARCH);
                    String query = "";
                    for (String pos : segcut)
                    {
                        query += pos + " ";
                    }
                    query = query.trim();
                    //    抓取网页商品
                        System.out.println(query);
                    return crawlShopping(query);
                }
            }

            //分词 正常分词
            String in = "";
            List<Term> term = ToAnalysis.parse(key).getTerms();
            for(Term word : term){
                String wc = word.getName();
                in += wc + " ";
            }
            in = in.trim();
            System.out.println("我是分词："+in);
            if(in.equals("")){
                System.out.println("222222222222222222222");
                ResultQA qa = new ResultQA("0x001", "未匹配问题", default_ans.get((int) (Math.random() * default_ans.size())));
                result.add(qa);
                return result;
            }
            //加一条 规则的 分词  应为这条数据分词错误
            in = in.replace("退 货运费", "退货 运费");
            //in = in.replace("坏 了", "坏了");

            if (flag == 0) {
                //es 检索  这部分是es方面的知识 es中存的问题列表是不是要 定时更新，怎么更新
                //BoolQueryBuilder qb = QueryBuilders.boolQuery().should(new QueryStringQueryBuilder(in).field("question"));
                BoolQueryBuilder qb = QueryBuilders.boolQuery().should(new QueryStringQueryBuilder(in).field("question")).should(QueryBuilders.termQuery("source_type", "0")).should(QueryBuilders.termQuery("source_type", "1")).mustNot(QueryBuilders.termQuery("source_type", "2"));
                builder.setQuery(qb);
                SearchResponse response = builder.execute().actionGet();
                //
                SearchHits hits = response.getHits();
                //存储 从es中搜索的答案 和问题
                Map<String, String> map = new HashMap<String, String>();
                Map<String, Double> que_score = new TreeMap<String, Double>();
                //定义从es 中搜出来的 相似问题的 列表
                List<String> similar = new ArrayList<String>();

                if (hits.totalHits() > 0)
                {
                    for (SearchHit hit : hits)
                    {
                        //question 是从es 中搜索到的相似问题
                        String question = hit.getSource().get("question").toString().trim();
                        //String question_zz = question;
                        //  把 es 中搜索出来的问题  ，过滤掉标点  然后去匹配
                        String question_filter = filterPunc.cleanRegx(question).trim().replace(" ","");

                        String ans = hit.getSource().get("answer").toString();
                        String cat1 = hit.getSource().get("cat1").toString();
                        String cat2 = hit.getSource().get("cat2").toString();

                        if(cat2.equals(""))
                        {
                            cat2 = cat1;
                        }

                        //System.out.println("cat2:"+cat2);

                        question = question.trim();



                        float score = hit.getScore();


                        if (score > 1.0)
                        {
                            //System.out.println("cat2:"+cat2);
                            // System.out.println("score:"+score+"     "+ans);
                            System.out.println("111111111:"+question+"     "+ans);
                            // 这里好像不是很妥当   要是输入了一个售前的，先试试
                            //  ###############################  语句 处理 ，过滤标点
                           if(set_wuliu.contains(question) && flagWL.equals("1"))
                            {
                               //物流信息

                                try{
                                    String url = String.format("http://10.112.167.8:9900/chatbot/queryOrderInfo?uid=%s&&token=123", uid);
                                    String answerJson = HttpRequestUtils.sendGet(url);
                                    Map<String, Object> mapJson = JSONObject.parseObject(answerJson);
                                    String code = mapJson.get("code").toString();
                                    if (!code.equals("0")){
                                        ResultQA qa = new ResultQA("0x0051", "物流问题", safeUrl, "-5", "-6");
                                        result.add(qa);
                                        return result;
                                    }
                                    String answer = mapJson.get("body").toString();
                                    //成功并且无订单
                                    if (!answer.contains("orderId")){

                                        ResultQA qa = new ResultQA("0x0050", "物流问题", "您当前没有待收货的订单哦。", "-5","-6");
                                        result.add(qa);
                                        return result;
                                    }

                                    ResultQA qa = new ResultQA("0x005", key, answer,"-5","-6");
                                    result.add(qa);
                                    return result;
                                }

                                catch (Exception e) {
                                    System.out.println("333333333333");
                                    ResultQA qa = new ResultQA("0x001", "未匹配问题", default_ans.get((int) (Math.random() * default_ans.size())));
                                    result.add(qa);
                                    return result;
                                }

                            }

                            //如果query 和es 搜出的某个问题完全相同，就通答案中随机取一个
                            System.out.println("in:"+in);
                            System.out.println("question_filter:"+question_filter);
                            if (in.replace(" ","").equals(question_filter))
                            {
                                System.out.println("question_filter:"+question_filter);
                                String answer = "";
                                if (ans.contains("%%%%%"))
                                   {
                                    String[] random = ans.split("%%%%%");
                                    int index = (int) (Math.random() * random.length);
                                    answer = random[index];
                                    }
                                else
                                    {
                                    answer = ans;
                                    }

                                ResultQA qa = new ResultQA(hit.getId(), question, answer,cat1,cat2);
                                result.add(qa);
                                return result;
                            }
                        //如果没有出现完全匹配的答案， 就把question 存到similar中，把问题和答案存到一个map中。
                            String id = hit.getId();
                            ans = id + "@@@@" +ans+"@@@@"+cat1+"@@@@"+cat2;
                            similar.add(question);
                            map.put(question, ans);
                        }
                    }
                    //如果es中 没有匹配到得分在1.0 以上的问句，就会 返回 安全答案
                    if (map.size() == 0 || similar.size() == 0) {
                        if(key.length() > 45)
                        {
                            ResultQA qa = new ResultQA("0x001", "未匹配问题,问题过长", peopleHelp);
                            result.add(qa);
                            return result;

                        }else {
                            System.out.println("44444444444");

                            ResultQA qa = new ResultQA("0x001", "未匹配问题", default_ans.get((int) (Math.random() * default_ans.size())));
                            result.add(qa);
                            return result;
                        }
                    }

                    // in 为分完词的 query ，sim为es中搜出来问题。然后遍历sim 求sim 和in的idf得分，把sim和得分存在que_score 中
                    for (String sim : similar) {
                        Double score = IDFCal.cosineSimilarity(in, sim);
                        que_score.put(sim, score);
                        //System.out.println(sim + "****" + score);
                    }

//                    System.out.println(que_score);
                    if(que_score.equals("")){
                        System.out.println("555555555555");
                        ResultQA qa = new ResultQA("0x001", "未匹配问题", default_ans.get((int) (Math.random() * default_ans.size())));
                        result.add(qa);
                        return result;
                    }

                    // 实现 map的按照 value值排序
                    List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(que_score.entrySet());
                    // 通过比较器来实现排序
                    Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
                        public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                            // 降序排序
                            return o2.getValue().compareTo(o1.getValue());
                        }
                    });


                    Map<String, String> res = new LinkedHashMap<String, String>();
                    int cnt = 0;

                    //如果分词后 包含  名词 和 加入的用户词典 就在set中保存这个词
                    Set<String> set = new HashSet<String>();
                    Result pos = ToAnalysis.parse(key);
                    for(Term tm : pos.getTerms()){
                            String os = tm.getNatureStr();
                            System.out.print(tm+"  "+ os+" ");
                            if(os.contains("n") || os.equals("userDefine")  || os.contains("v")  || os.contains("a")){
                                set.add(tm.getName());
                            }
                    }
                    System.out.println();
                    //list 中存的是 query 和es中搜索到的相似问题的重排后的  顺序
                    /*
                       逻辑结构：  （1）首先用 set保存 query 中的名词 和加入的 用户词典
                                   （2）如果 es中搜索的相似问题，中  也  包含这些 set中词（至少一个）
                                   （3）如果包含 或者 set 为空   就找query_sim 中找对应的最相似的四个问题
                                        如果 不为空，且 question中不包含 set中的词，就跳过这一句

                     */
                    /*
                        如果 question 中包含query 中的一些名词 ，tab 为true
                        如果 tab为true 或者set.size() == 0 （query中没用名词）
                             根据query_sim 获取  对应的原始问题
                                  如果原始问题为null 获得为“”
                                       在res中加入es搜索出来的question
                                  如果原始问题不为空
                                       在res中加入对应的 原始问题
                             循环 所有问题

	                   如果res 为0 返回为匹配

                     */
                    for (int i = 0; i < list.size(); i++)
                    {

                        if (cnt == 4)
                            break;
                        String question = list.get(i).getKey();
                        String ans_one = map.get(question);
                        question = question.trim();
                        String query_one = "";
                        System.out.println("rrrrrrrrrrrrrrr:"+question);
                        boolean tab = false;
                        if (set.size() != 0)
                        {
                            for (String str : set)
                            {
                                if (question.contains(str))
                                {
                                    tab = true;
                                }
                            }
                        }
                         //  query_sim 中是  相似的问题
                        if (tab || set.size() == 0) {
                            //获得相似的问题

                           query_one = query_sim.get(question);

                            System.out.println("question:"+question+"query_one"+query_one);
                            if(set_wuliu.contains(query_one) && !flagWL.equals("0"))
                            {
                                //物流信息

                                try{
                                    String url = String.format("http://10.112.167.8:9900/chatbot/queryOrderInfo?uid=%s&&token=123", uid);
                                    String answerJson = HttpRequestUtils.sendGet(url);
                                    Map<String, Object> mapJson = JSONObject.parseObject(answerJson);
                                    String code = mapJson.get("code").toString();
                                    if (!code.equals("0")){
                                        ResultQA qa = new ResultQA("0x0051", "物流问题", safeUrl,"-5", "-6");
                                        result.add(qa);
                                        return result;
                                    }
                                    String answer = mapJson.get("body").toString();
                                    //成功并且无订单
                                    if (!answer.contains("orderId")){

                                        ResultQA qa = new ResultQA("0x0050", "物流问题", "您当前没有待收货的订单哦。", "-5", "-6");
                                        result.add(qa);
                                        return result;
                                    }

                                    ResultQA qa = new ResultQA("0x005", key, answer, "-5", "-6");
                                    result.add(qa);
                                    return result;
                                }

                                catch (Exception e) {
                                    ResultQA qa = new ResultQA("0x001", "未匹配问题", default_ans.get((int) (Math.random() * default_ans.size())));
                                    result.add(qa);
                                    return result;
                                }

                            }
                            // 在这里判断 走物流接口   判断是否在
                            //为啥要用用户的query 换成 querysim存在对应关系的 问题
                            try {
                                if (query_one == null || query_one.equals("")) {
                                    //替换掉 空白符
                                    question = question.replaceAll("\\s", "");
                                    System.out.println("hhhhhhhhhhh:"+question);
                                    if (res.containsKey(question)) {
                                        continue;
                                    } else {
                                        res.put(question, ans_one);
                                        ++cnt;
                                    }

                                } else {
                                    query_one = query_one.replaceAll("\\s", "");
                                    if (res.containsKey(query_one)) {
                                        continue;
                                    } else {
                                        res.put(query_one, ans_one);
                                        ++cnt;
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("--------" + question + "-----------");
                                continue;
                            }
                        }else{
                            continue;
                        }
                    }

                    if(res.size() == 0){
                        System.out.println("fffffffffffffffffff:");
                        ResultQA qa = new ResultQA("0x001", "未匹配问题", "小美还在思考，怎么解决亲的问题，给小美一点时间哦，小美一直都在努力学习...");
                        result.add(qa);
                        return result;
                    }
                    System.out.println(res.size());
                    for (Map.Entry<String, String> entry : res.entrySet())
                    {
                        String question = entry.getKey();
                        String answer = entry.getValue();
                        System.out.println("fffffffffffffffffff:"+question);
                        System.out.println("yyyyyyyyyyyyyyyy:"+answer);
                        String[] split = answer.split("@@@@");
                        System.out.println(split.length);
                        String id = "";
                        String ans = "";
                        String cat1 = "";
                        String cat2 = "";
                        try{
                            id = split[0];
                            ans = split[1];
                            cat1 = split[2];
                            cat2 = split[3];
                          /*  System.out.println(id);
                            System.out.println(ans);
                            System.out.println(cat1);
                            System.out.println(cat2);*/

                        }catch (Exception e){
                            continue;
                        }
                        System.out.println("fffffffffffffffffff:"+question+"yyyyyyyyyyyyyyyy:"+ans+" sssssssss  "+cat1+"sssssss:  "+cat2);
                        ResultQA qa = new ResultQA(id, question, ans,cat1,cat2);
                        result.add(qa);
                    }


                    su = true;
                } else {
                    ResultQA qa = new ResultQA("0x001", "未匹配问题", default_ans.get((int) (Math.random() * default_ans.size())));
                    result.add(qa);
                    return result;
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        if (su) {
            return result;
        } else {
            ResultQA qa = new ResultQA("0x001", "未匹配问题", default_ans.get((int) (Math.random() * default_ans.size())));
            result.add(qa);
            return result;
        }
    }

    //添加索引
    public static Boolean addQuestionAnswer(String question, String answer) {
        String date = sf.format(new Date());

        logger.info("current time: {}, gome QA addQuestionAnswer question and answer: {}.", date, question + "####" + answer);
        try {

            String in = ToAnalysis.parse(question).toStringWithOutNature(" ");
            in = in.trim();

            String data = JsonUtil.model2Json_pc(new Blog_pc("业务分类", in, answer));
            client.prepareIndex("chatbot_v2_1", "chats").setSource(data).execute().actionGet();

        } catch (Exception e) {
            return false;
        }

        return true;
    }


    //线下业务需求的问答接口    和queryAnswer 有何不同
    public static List<ResultQA> queryAnswer2(String key, int flag) {

        String date = sf.format(new Date());
        logger.info("current time : {}, gome QA queryAnswer key:{}.", date, key);
        List<ResultQA> result = new ArrayList<ResultQA>();
        //标记是否有返回
        Boolean su = false;
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

            //空字符串判断
            if (key.equals("")) {
                ResultQA qa = new ResultQA("0x001", "未匹配问题", "?");
                result.add(qa);
                return result;
            }

            //ansj分词接口
            String in = ToAnalysis.parse(key).toStringWithOutNature(" ");
            in = in.trim();
            in = in.replace("退 货运费", "退货 运费");

            //情感分析
            Map<String, Double> sentiment = predictSentiment.predict(in);
            if (sentiment.containsKey("负向") && (sentiment.get("负向") > 0.97)) {
                ResultQA qa = new ResultQA("0x001", "未匹配问题", "若您需要联系人工服务，请<a target=\"_blank\" href=\"http://chat5.gome.com.cn/live800/chatClient/chatbox.jsp?companyID=3&customerID=3&info=userId%3D72047830291%26loginname%3D13717588721%26grade%3D5%26name%3D%E7%BE%8E%E7%BE%8E_451487663686818%26memo%3D%26hashCode%3Dbf78a171e793aa67a4623278ad45ccbe%26timestamp%3D1489980641692&page=0&enterurl=http://help.gome.com.cn/&areaCode=11010800%257C%25E5%258C%2597%25E4%25BA%25AC%25E5%25B8%2582%25E9%2580%259A%25E5%25B7%259E%25E5%258C%25BA%257C11010000%257C11000000%257C110102001&shopname=%25EF%25BF%25BD%25EF%25BF%25BD%25EF%25BF%25BD%25DF%25BF%25CD%25B7%25EF%25BF%25BD\"><strong><span style=\"color: rgb(51,102,255);\">点击这里</span></strong></a> 联系人工客服哦~ ");
                result.add(qa);
                return result;
            }

            if (flag == 0) {
                BoolQueryBuilder qb = QueryBuilders.boolQuery().should(new QueryStringQueryBuilder(in).field("question"));
                builder.setQuery(qb);
                SearchResponse response = builder.execute().actionGet();

                SearchHits hits = response.getHits();
                Map<String, String> map = new HashMap<String, String>();
                Map<String, Double> que_score = new TreeMap<String, Double>();
                List<String> similar = new ArrayList<String>();

                String firstCat = "";
                if (hits.totalHits() > 0) {
                    firstCat = hits.getAt(0).getSource().get("classify").toString();
                    for (SearchHit hit : hits) {
                        String question = hit.getSource().get("question").toString();
                        String ans = hit.getSource().get("answer").toString();

                        question = question.trim();

                        float score = hit.getScore();
                        if (score > 0.9) {
                            if (in.equals(question + " ？")) {
                                ResultQA qa = new ResultQA(hit.getId(), question, ans);
                                result.add(qa);
                                return result;
                            }
                            similar.add(question);
                            map.put(question, ans);
                        }
                    }

                    if (map.size() == 0 || similar.size() == 0) {

                        ResultQA qa = new ResultQA("0x001", "未匹配问题", default_ans.get((int) (Math.random() * default_ans.size())));
                        result.add(qa);
                        return result;
                    }

                    for (String sim : similar) {
                        que_score.put(sim, IDFCal.cosineSimilarity(in, sim));
//                        System.out.println(sim + "--------------" + IDFCal.cosineSimilarity(in, sim));
                    }

                    List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(que_score.entrySet());
                    // 通过比较器来实现排序
                    Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
                        public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                            // 降序排序
                            return o2.getValue().compareTo(o1.getValue());
                        }
                    });

                    //1-best answer
                    Map<String, String> res = new LinkedHashMap<String, String>();
                    int cnt = 0;
                    for (int i = 0; i < list.size(); i++) {
                        if (cnt == 3)
                            break;
                        String question = list.get(i).getKey();
                        String ans_one = map.get(question);
                        question = question.trim();
                        String query_one = "";
                        try {
                            query_one = query_sim.get(question);
                            query_one = query_one.replaceAll("\\s", "");
                        } catch (Exception e) {
                            System.out.println("---------" + question + "-------------");
                            continue;
                        }


                        if (res.containsKey(query_one)) {
                            continue;
                        } else {
                            res.put(query_one, ans_one);
                            ++cnt;
                        }
                    }


                    //2,3 answer
                    System.out.println(firstCat);
                    if (!firstCat.equals("") || firstCat != null) {
                        BoolQueryBuilder qb_2 = QueryBuilders.boolQuery().must(new QueryStringQueryBuilder(firstCat).field("classify"));
                        builder.setQuery(qb_2);
                        SearchResponse response_2 = builder.execute().actionGet();
                        SearchHits hits_2 = response_2.getHits();
                        int number = 1;
                        if (hits_2.totalHits() > 0) {
                            for (SearchHit hit : hits_2) {
                                if (number == 3)
                                    break;

                                String query = hit.getSource().get("question").toString();
                                String ans = hit.getSource().get("answer").toString();
                                String query_i = "";
//                                System.out.println(query+"\t"+ans);
                                try {
                                    query_i = query_sim.get(query);
                                    query_i = query_i.replaceAll("\\s", "");
                                } catch (Exception e) {
                                    continue;
                                }
                                if (res.containsKey(query_i)) {
                                    continue;
                                } else {
                                    res.put(query_i, ans);
                                    ++number;
                                }
                            }
                        }

                    }

                    int num = 0;
                    for (Map.Entry<String, String> entry : res.entrySet())
                    {
                        ResultQA qa = new ResultQA(++num + "", entry.getKey(), entry.getValue());
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

        if (su) {
            return result;
        } else {
            ResultQA qa = new ResultQA("0x001", "未匹配问题", default_ans.get((int) (Math.random() * default_ans.size())));
            result.add(qa);
            return result;
        }
    }

    //APP接口
    public static List<ResultQA> queryAnswerAPP(String key, int flag) {
        key = key.replaceFirst("<","");
        //获取时间
        String date = sf.format(new Date());
        //打印日志
        logger.info("current time : {}, gome QA queryAnswer key:{}.", date, key);
        //存储 答案 的 列表
        List<ResultQA> result = new ArrayList<ResultQA>();

        //推送商品    当用户点击了 某品牌下的一个 领域，比如海尔 有冰箱，洗衣机，点击了冰箱之后，就会调用 下面的 if语句
        if (key.startsWith("%%%%") && key.endsWith("%%%%")) {
            try {
                return crawlShopping(key);
            } catch (Exception e) {
                ResultQA qa = new ResultQA("0x001", "未匹配问题", "抱歉，亲，没有找到相关的商品，请您选择其他商品购买");
                result.add(qa);
                return result;
            }
        }
        //标记是否有返回
        Boolean su = false;
        //标点过滤
        for (String bd : biaodian)
        {
            key = key.replace(bd, "");
        }

        try {
            //去除html标签
            String regEx_html = "<[^>]+>";
            Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            Matcher m_html = p_html.matcher(key);
            key = m_html.replaceAll("");

            //空字符串判断
            if (key.equals("")) {
                ResultQA qa = new ResultQA("0x001", "未匹配问题", "?");
                result.add(qa);
                return result;
            }

            //小写
            key = key.toLowerCase();


            //分词 正常分词
            String in = "";
            List<Term> term = ToAnalysis.parse(key).getTerms();
            for(Term word : term){
                String wc = word.getName();
                in += wc + " ";
            }
            in = in.trim();


            if(in.equals("")){
                ResultQA qa = new ResultQA("0x001", "未匹配问题", default_ans.get((int) (Math.random() * default_ans.size())));
                result.add(qa);
                return result;
            }
            //加一条 规则的 分词  应为这条数据分词错误
            in = in.replace("退 货运费", "退货 运费");


            if (flag == 0) {
                //es 检索  这部分是es方面的知识 es中存的问题列表是不是要 定时更新，怎么更新
//                BoolQueryBuilder qb = QueryBuilders.boolQuery().should(new QueryStringQueryBuilder(in).field("question"));
                BoolQueryBuilder qb = QueryBuilders.boolQuery().should(new QueryStringQueryBuilder(in).field("question")).should(QueryBuilders.termQuery("source_type", "0")).should(QueryBuilders.termQuery("source_type", "2")).mustNot(QueryBuilders.termQuery("source_type", "1"));
                builder.setQuery(qb);
                SearchResponse response = builder.execute().actionGet();
                //
                SearchHits hits = response.getHits();
                //存储 从es中搜索的答案 和问题
                Map<String, String> map = new HashMap<String, String>();
                Map<String, Double> que_score = new TreeMap<String, Double>();
                //定义从es 中搜出来的 相似问题的 列表
                List<String> similar = new ArrayList<String>();

                if (hits.totalHits() > 0)
                {
                    for (SearchHit hit : hits)
                    {
                        //question 是从es 中搜索到的相似问题
                        String question = hit.getSource().get("question").toString();
                        String ans = hit.getSource().get("answer").toString();
                        String cat1 = hit.getSource().get("cat1").toString();
                        String cat2 = hit.getSource().get("cat2").toString();
                       // System.out.println("question"+question);

                        question = question.trim();
                        float score = hit.getScore();


                        if (score > 1.0)
                        {
                            System.out.println("question"+question);
                            //如果query 和es 搜出的某个问题完全相同，就通答案中随机取一个
                            if (in.equals(question))
                            {
                                String answer = "";
                                if (ans.contains("%%%%%"))
                                {
                                    String[] random = ans.split("%%%%%");
                                    int index = (int) (Math.random() * random.length);
                                    answer = random[index];
                                }
                                else
                                {
                                    answer = ans;
                                }

                                ResultQA qa = new ResultQA(hit.getId(), question, answer,cat1,cat2);
                                result.add(qa);
                                return result;
                            }
                            //如果没有出现完全匹配的答案， 就把question 存到similar中，把问题和答案存到一个map中。
                            String id = hit.getId();
                            ans = id + "@@@@" +ans+"@@@@"+cat1+"@@@@"+cat2;
                            similar.add(question);
                            map.put(question, ans);
                        }
                    }
                    //如果es中 没有匹配到得分在1.0 以上的问句，就会 返回 安全答案
                    if (map.size() == 0 || similar.size() == 0) {
                        System.out.println("aaaaaaaaaaaaaaaaaa");
                        ResultQA qa = new ResultQA("0x001", "未匹配问题", default_ans.get((int) (Math.random() * default_ans.size())));
                        result.add(qa);
                        return result;
                    }

                    // in 为分完词的 query ，sim为es中搜出来问题。然后遍历sim 求sim 和in的idf得分，把sim和得分存在que_score 中
                    for (String sim : similar) {
                        Double score = IDFCal.cosineSimilarity(in, sim);
                        que_score.put(sim, score);
//                        System.out.println(sim + "****" + score);
                    }

//                    System.out.println(que_score);
                    if(que_score.equals("")){
                        ResultQA qa = new ResultQA("0x001", "未匹配问题", default_ans.get((int) (Math.random() * default_ans.size())));
                        result.add(qa);
                        return result;
                    }

                    // 实现 map的按照 value值排序
                    List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(que_score.entrySet());
                    // 通过比较器来实现排序
                    Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
                        public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                            // 降序排序
                            return o2.getValue().compareTo(o1.getValue());
                        }
                    });


                    Map<String, String> res = new LinkedHashMap<String, String>();
                    int cnt = 0;

                    //如果分词后 包含  名词 和 加入的用户词典 就在set中保存这个词
                    Set<String> set = new HashSet<String>();
                    Result pos = ToAnalysis.parse(key);
                    for(Term tm : pos.getTerms()){
                        String os = tm.getNatureStr();
                        if(os.contains("n") || os.equals("userDefine")){
                            set.add(tm.getName());
                        }
                    }
                    //list 中存的是 query 和es中搜索到的相似问题的重排后的  顺序
                    /*
                       逻辑结构：  （1）首先用 set保存 query 中的名词 和加入的 用户词典
                                   （2）如果 es中搜索的相似问题，中  也  包含这些 set中词（至少一个）
                                       （3）如果包含 或者 set 为空   就找query_sim 中找对应的最相似的四个问题
                                           如果 不为空，且 question中不包含 set中的词，就跳过这一句

                     */
                    for (int i = 0; i < list.size(); i++)
                    {
                        if (cnt == 4)
                            break;
                        String question = list.get(i).getKey();
                        String ans_one = map.get(question);
                        question = question.trim();
                        String query_one = "";

                        boolean tab = false;
                        if (set.size()!=0)
                        {
                            for (String str : set)
                            {
                                if (question.contains(str))
                                {
                                    tab = true;
                                }
                            }
                        }
                        //  query_sim 中是  相似的问题
                        if (tab || set.size() == 0) {
                            //获得相似的问题
                            query_one = query_sim.get(question);
                            //为啥要用用户的query 换成 querysim存在对应关系的 问题
                            try {
                                if (query_one == null || query_one == "") {
                                    //替换掉 空白符
                                    question = question.replaceAll("\\s", "");
                                    if (res.containsKey(question)) {
                                        continue;
                                    } else {
                                        res.put(question, ans_one);
                                        ++cnt;
                                    }

                                } else {
                                    query_one = query_one.replaceAll("\\s", "");
                                    if (res.containsKey(query_one)) {
                                        continue;
                                    } else {
                                        res.put(query_one, ans_one);
                                        ++cnt;
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("--------" + question + "-----------");
                                continue;
                            }
                        }else{
                            continue;
                        }
                    }


                    if(res.size() == 0){
                        System.out.println("ddddddddddddddddddddd");
                        ResultQA qa = new ResultQA("0x001", "未匹配问题", "小美还在思考，怎么解决亲的问题，给小美一点时间哦，小美一直都在努力学习...");
                        result.add(qa);
                        return result;
                    }

                    for (Map.Entry<String, String> entry : res.entrySet())
                    {
                        String question = entry.getKey();
                        String answer = entry.getValue();
                        String[] split = answer.split("@@@@");
                        String id = "";
                        String ans = "";
                        String cat1 = "";
                        String cat2 = "";
                        try{
                            id = split[0];
                            ans = split[1];
                            cat1 = split[2];
                            cat2 = split[3];
                        }catch (Exception e){
                            continue;
                        }
                        ResultQA qa = new ResultQA(id, question, ans,cat1,cat2);
                        result.add(qa);
                    }


                    su = true;
                } else {
                    ResultQA qa = new ResultQA("0x001", "未匹配问题", default_ans.get((int) (Math.random() * default_ans.size())));
                    result.add(qa);
                    return result;
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        if (su) {
            return result;
        } else {
            ResultQA qa = new ResultQA("0x001", "未匹配问题", default_ans.get((int) (Math.random() * default_ans.size())));
            result.add(qa);
            return result;
        }
    }

    //搜索提示
    public static List<ResultRto> queryHint(String key) {
        String date = sf.format(new Date());
        logger.info("current time: {}, gome QA queryHint key:{}.", date, key);
        List<ResultRto> list = new ArrayList<ResultRto>();

        try {

            String regEx_html = "<[^>]+>";
            Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            Matcher m_html = p_html.matcher(key);
            key = m_html.replaceAll("");


            String in = ToAnalysis.parse(key).toStringWithOutNature(" ");
            in = in.trim();

            BoolQueryBuilder qb = QueryBuilders.boolQuery().should(new QueryStringQueryBuilder(in).field("question"));
            builder.setQuery(qb);
            SearchResponse response = builder.execute().actionGet();

            SearchHits hits = response.getHits();
            int count = 0;
            //搜索出来的 结果不用按得分排序吗，
            if (hits.totalHits() > 0) {
                for (SearchHit hit : hits) {
                    if (++count > 5) {
                        break;
                    }

                    String question = hit.getSource().get("question").toString();
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
    public static Boolean deleteIndex(String _id) {
        String date = sf.format(new Date());
        logger.info("current time: {}, gome QA deleteIndex id:{}.", date, _id);

        try {
            client.prepareDelete("chatbot_v2", "chats", _id).get();
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    //以下的代码是追一的

    public static List<ResultQA> queryAnswer_YiBot(String key) {

        String date = sf.format(new Date());
        logger.info("current time : {}, gome QA queryAnswer key:{}.", date, key);
        List<ResultQA> result = new ArrayList<ResultQA>();

        if (key.equals("")) {
            ResultQA qa = new ResultQA("0x001", "未匹配问题", "?");
            result.add(qa);
            return result;
        }

        String answer = getAnswer(key);
        System.out.println(answer);
        List<ResultQA> list = new ArrayList<ResultQA>();
        try {
            list = jsonStringToList(answer);
        } catch (Exception e) {
            ResultQA qa = new ResultQA("0x001", "未匹配问题", default_ans.get((int) (Math.random() * default_ans.size())));
            result.add(qa);
            return result;
        }


        return list;
    }

    //json串解析
    private static List<ResultQA> jsonStringToList(String rsContent) throws Exception
    {
        List<ResultQA> rsList = new ArrayList<ResultQA>();

        Map<String, List<Object>> map = JSONUtils.stringToCollect(rsContent);

        List<Object>  all_information = map.get("info");

        for(int i = 0; i < all_information.size(); i++)
        {
//          System.out.println(all_information.get(i).toString());
            String out = all_information.get(i).toString();
            Map<String, Object> information = JSONUtils.stringToCollect(out);
            String id = information.get("id").toString();
            String question = information.get("question").toString();
            String answer = information.get("answer").toString();

            ResultQA qa = new ResultQA(id, question, answer);
            rsList.add(qa);
        }

        return rsList;
    }


//主程序
    public static void main(String[] args) {

//
        //String ss = "郑品按摩椅A07-1智能按摩椅家用全身多功能豪华零重力太空舱音乐环绕(香槟色)  配送单号：27720660822018年2月7日下订单。电话说该商品没有生产，而该电商还在销售。请答复，18089750270";
        //System.out.println(ss.length());
        List<ResultQA> list = ElasticSearch_sent2vec.queryAnswer("000","美通卡可以激活么", 0);
            //  List<ResultQA> list = ElasticSearch_sent2vec.queryAnswerAPP("只有一级分类", 0);
            for (ResultQA re : list) {
                System.out.println(re.getId() + "\t" + re.getQuestion() + "\t" + re.getAnswer() + "\t" + re.getCat1() + "\t" + re.getCat2());
            }



    }
}














