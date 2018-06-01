package com.gome.process;

import com.gome.analysis.segment.GomeSegmenter;
import com.gome.buildindex.ResultQA;
import com.gome.guide.ResultGuide;
import com.gome.guide.predict;
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

import static com.gome.process.ElasticSearch_faq.default_ans;

/**
 * Created by lixiang-ds3 on 2017/7/4.
 */
public class ElasticSearch_guide {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearch_guide.class);
    private static SimpleDateFormat sf = null;
    private static ElasticSearch_guide singleton;
    private static Map<String, String> raw_data = null;
    //“cat21655557	品牌##美骆世家,探拓者,阿迪达斯,骆驼（CAMEL）%%%%闭合方式##系带,一脚蹬” 类似 这样的文本
    private static final String MAIN_DICT = "/filter_facters.txt";
    private static SearchRequestBuilder builder = null;
    public static Client client = null;
    private static GomeSegmenter segmenter = null;

    static {
        client = ESUtil.getClient();
        sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        raw_data = new HashMap<String, String>();
        builder = client.prepareSearch("chatbot_final").setTypes("chats").setSearchType(SearchType.DEFAULT)
                .setFrom(0).setSize(1000);


        segmenter = GomeSegmenter.getInstance();
    }

    private ElasticSearch_guide() {
        this.loadDict();
    }

    public static ElasticSearch_guide getInstance() {
        if (singleton == null) {
            synchronized (ElasticSearch_guide.class) {
                if (singleton == null) {
                    singleton = new ElasticSearch_guide();
                    return singleton;
                }
            }
        }
        return singleton;
    }

    public void loadDict() {

        InputStream is = this.getClass().getResourceAsStream(MAIN_DICT);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            long s = System.currentTimeMillis();
            while (br.ready()) {
                String line = br.readLine();
                line = line.trim();
                String[] info = line.split("\t");
                raw_data.put(info[0], info[1]);
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


    public static String shopping_guide(String query) {
//        //当前日期
        String coreWord = "";
        //国美分词
        String[] seg = segmenter.pos(query);
        int flag = -1;

        for (String pos : seg) {
            if (pos.contains("/core") || pos.contains("/cat")) {
                coreWord += pos.substring(0, pos.indexOf("/")) + " ";

                flag = 0;//品类、核心词
            }

            if (pos.contains("/brand")) {

                coreWord += pos.substring(0, pos.indexOf("/")) + " ";
                flag = 1;//品牌、核心词
            }
        }

        coreWord = coreWord.trim();

        if (flag == 0) {
            ResultGuide guide = predict.predict(coreWord, 0);
            String catid = guide.getCatid();

            logger.info("current catId : {}, current brand:{}", guide.getCatid(), guide.getBrand());


            if (raw_data.containsKey(catid)) {
                String ans = coreWord + "===" + raw_data.get(catid);
                return ans;
            } else {
                return coreWord;
            }
        }

        if (flag == 1) {
            ResultGuide guide = predict.predict(coreWord, 1);
            String catid = guide.getCatid();

            if(catid != ""){
                String ans = coreWord + "====" + catid;
                return ans;
            }else{
                return coreWord;
            }
        }

        return "";
    }

//暂时没有用到
    public static ResultQA answer_guide(String faceters) {

        Set<String> set = new HashSet<String>();
        Map<String, String> map = new HashMap<String, String>();
        Map<String, String> map_sec = new HashMap<String, String>();
        if (faceters.contains("####")) {
            String[] facet = faceters.split("####");
            String cat = "";
            String choice = "";
            try {
                cat = facet[0];
                choice = facet[1];
                choice = choice.replaceAll("（[A-Za-z]+）", "");
            }catch(Exception e){
                return new ResultQA("0x001", "未匹配问题", default_ans.get((int) (Math.random() * default_ans.size())));
            }
            BoolQueryBuilder qb = QueryBuilders.boolQuery().must(new QueryStringQueryBuilder(cat).field("catId"));
            builder.setQuery(qb);
            SearchResponse response = builder.execute().actionGet();

            SearchHits hits = response.getHits();

            if (hits.totalHits() > 0) {
                  for (SearchHit hit : hits) {
                      String id = hit.getSource().get("skuid").toString();
                      String title = hit.getSource().get("title").toString();
                      String attr = hit.getSource().get("attr").toString();
//                      System.out.println(id);
                      if(choice.contains(" ")){
                          String[] out = choice.split(" ");
                          if(attr.contains(out[0]) && attr.contains(out[1])){
                              map.put(id, title);
                          }else if(attr.contains(out[0])){
                              map_sec.put(id, title);
                          }
                      }else{
                          if(attr.contains(choice)){
                              map.put(id, title);
                          }
                      }
                }

                String result = "";
                int cnt = 0;
                for(Map.Entry<String, String> entry : map.entrySet()){
                    String id = entry.getKey();
                    String title = entry.getValue();
                    String url = "http://item.gome.com.cn/" + id + ".html";
                    if(++cnt == 4){
                        break;
                    }else{
                        result += url+ "####"+ title + " ";
                        System.out.println(url + "####" + title);
                    }
                }

                if(cnt < 3){
                    for(Map.Entry<String, String> entry : map_sec.entrySet()){
                        String id = entry.getKey();
                        String title = entry.getValue();
                        String url = "http://item.gome.com.cn/" + id + ".html";
                        if(++cnt == 4){
                            break;
                        }else{
                            result += url+ "####"+ title + " ";

                        }
                    }
                }

                result = result.trim();
                return new ResultQA("2", "guide", result);

            }else{
                return new ResultQA("0x001", "未匹配问题", default_ans.get((int) (Math.random() * default_ans.size())));
            }

        } else {
            return new ResultQA("0x001", "未匹配问题", default_ans.get((int) (Math.random() * default_ans.size())));
        }

    }


    public static void main(String[] args){
        ElasticSearch_guide.getInstance();
//
        System.out.println(ElasticSearch_guide.shopping_guide("笔记本 电脑"));
//        System.out.println(ElasticSearch_guide.answer_guide("cat10000092####品牌:惠普（HP）"));
//        for(Map.Entry<String, String> map : raw_data.entrySet()){
//            System.out.println(map.getKey() + "\t" + map.getValue());
//        }
//
//        String str = "康宝（canbo）";
//        str = str.replaceAll("（[A-Za-z]+）", "");
//        System.out.println(str);

    }
}
