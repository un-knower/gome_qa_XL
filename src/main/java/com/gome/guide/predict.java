package com.gome.guide;

import com.gome.fasttext.FastText;
import com.gome.fasttext.Pair;
import com.gome.process.ElasticSearch_guide;
import com.gome.process.ElasticSearch_sent2vec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by lixiang-ds3 on 2017/7/12.
 */
public class predict {

    private static FastText fasttext_cat = new FastText();

    private static Map<String, String> catId3 = new HashMap<String, String>();
    private static final String KeyWord_DICT = "/catId3.txt";

    private static Map<String, String> facets_els = new HashMap<String, String>();
    private static final String FACETS_DICT = "/filter_facters2.txt";

    private static predict singleton;
    static{
        try {

            String filePath;
            String os = System.getProperties().get("os.name").toString();
            if (os != null && os.toLowerCase().indexOf("linux") > -1){
                filePath="/app/tag/datakey/query_catId.bin";
            }else{
                filePath = "E:/query_catId.bin";
            }
            fasttext_cat.loadModel(filePath);
            predict.getInstance();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private predict() {
        this.loadDict();
        this.loadDict2();
    }

    public static predict getInstance() {
        if (singleton == null) {
            synchronized (predict.class) {
                if (singleton == null) {
                    singleton = new predict();
                    return singleton;
                }
            }
        }
        return singleton;
    }

    public void loadDict() {

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
                catId3.put(in, out);
            }
        }
        catch (IOException e) {
            System.err.println(String.format(Locale.getDefault(), "%s load failure!", KeyWord_DICT));
        }
        finally {
            try {
                if (null != is)
                    is.close();
            }
            catch (IOException e) {
                System.err.println(String.format(Locale.getDefault(), "%s close failure!", KeyWord_DICT));
            }
        }
    }

    public void loadDict2() {

        InputStream is = this.getClass().getResourceAsStream(FACETS_DICT);
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

                if(out.contains("%%%%")){
                    String[] facets = out.split("%%%%");
                    String els = facets[1];
                    facets_els.put(in, els);
                }
            }
        }
        catch (IOException e) {
            System.err.println(String.format(Locale.getDefault(), "%s load failure!", FACETS_DICT));
        }
        finally {
            try {
                if (null != is)
                    is.close();
            }
            catch (IOException e) {
                System.err.println(String.format(Locale.getDefault(), "%s close failure!", FACETS_DICT));
            }
        }
    }

//根据品牌，品牌关键词，用fasttext模型 预测属于哪个品类?
    public static ResultGuide predict(String query, int flag){

       String result = query.toLowerCase();
       result = result.replace("/", " ");
        String[] test= result.split(" ");

        String catid = "";
        if(flag == 0) {
            List<Pair<Float, String>> list_cat = fasttext_cat.predict(test, 1);
            catid = list_cat.get(0).getValue();

            if(catid.contains("__label__")){
                catid = catid.replace("__label__", "");
            }
        }

       if(flag == 1) {
           List<Pair<Float, String>> list_cat = fasttext_cat.predict(test, 6);
           int len = list_cat.size();

           for(int i = 0; i < len; i++) {
               String res = list_cat.get(i).getValue();
               if (res.contains("__label__")) {
                   res = res.replace("__label__", "");
                   if(res.equals("")){
                       continue;
                   }
                   try{
                       String cat = catId3.get(res);
                       if(cat.equals("null")){
                           continue;
                       }
                       if(facets_els.containsKey(res)){
                           catid += cat +":" + facets_els.get(res) + "@@";
                       }else{
                           catid += cat + "@@";
                       }

                   }catch (Exception e){
                       continue;
                   }
               }
           }
       }

        return new ResultGuide(catid,"","");
   }

   public static void main(String[] args){
       ResultGuide guide = predict.predict("Haier", 1);
   }
}
