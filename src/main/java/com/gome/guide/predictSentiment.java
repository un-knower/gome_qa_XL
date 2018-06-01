package com.gome.guide;

import com.gome.fasttext.FastText;
import com.gome.fasttext.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lixiang-ds3 on 2017/8/16.
 */

public class predictSentiment {

    private static FastText sentiment = new FastText();


//    static{
//        try {
//
//            String filePath;
//            String os = System.getProperties().get("os.name").toString();
//            if (os != null && os.toLowerCase().indexOf("linux") > -1){
////                filePath="/app/tag/datakey/setiment.model.bin";
//            }else{
////                filePath = "D:\\data\\setiment.model.bin";
//            }
////            System.out.println("-----filePath-----"+filePath);
////            sentiment.loadModel(filePath);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    public static Map<String, Double> predict(String query){

        String result = query.toLowerCase();
        result = result.replace("/", " ");
        String[] test= result.split(" ");
        List<Pair<Float, String>> sent = sentiment.predict(test, 1);
        Map<String, Double> map = new HashMap<String, Double>();

        Double score =  Math.exp(sent.get(0).getKey());
        String catid = sent.get(0).getValue();

        System.out.println(catid + "\t" + score);
//        if(catid.contains("#")){
//            catid = catid.replace("#", "");
//        }
//        map.put(catid, score);
//        System.out.println(catid);
        return map;
    }


    public static void main(String[] args){
        Map<String, Double> guide = predict("不错");
    }
}
