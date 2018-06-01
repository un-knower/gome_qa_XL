package com.gome.process;

import com.gome.domain.WordEntry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

import static java.lang.StrictMath.max;

/**
 * Created by lixiang-ds3 on 2017/3/13.
 */
public class Sent2vecCal {
        private static Word2VEC w1 = null;
        static{
            w1 = new Word2VEC() ;
            try {
                String filePath;
                String os = System.getProperties().get("os.name").toString();
                if (os != null && os.toLowerCase().indexOf("linux") > -1){
                	filePath="/app/tag/datakey/vectors.bin";
                }else{
                    filePath = "D:\\data\\vectors.bin";
                }
            	System.out.println("-----filePath-----"+filePath);
                w1.loadGoogleModel(filePath) ;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    private static double getDotProduct(float[] d1, float[] d2) {
        double product = 0;

        for(int i = 0; i < d1.length; i++){
            product += d1[i] * d2[i];
        }

        return product;
    }


//
//
    private static double getMagnitude(float[] str) {
        double magnitude = 0;

        for(int i = 0; i < str.length; i++){
            magnitude += str[i] * str[i];
        }

        return Math.sqrt(magnitude);
    }

    public static double  Euclidean_distance(float[] d1, float[] d2){
        double sim = 0.0;


        for(int i = 0; i < d1.length; i++){
            sim += d1[i] * d2[i];
        }
        return Math.sqrt(sim);
    }
//
    public static double cosineSimilarity(String d1, String d2) {

        String[] str = d1.split(" ");
        String[] str2 = d2.split("");

        float[] v1 = new float[200];
        float[] v2 = new float[200];

        int c1 = 0;
        int c2 = 0;

        for(String en: str){
            if(w1.getWordVector(en) == null){
                continue;
            }
            c1++;
            float[] vector =  w1.getWordVector(en);
            for(int i = 0; i < vector.length; i++){
                v1[i] += vector[i];
            }
        }

        if(c1 == 0){
            return 0.0;
        }
        for(int i = 0; i < v1.length; i++){
            v1[i] = v1[i] / c1;
//            System.out.println("----------------" + v1[i]);
        }

        for(String en: str2){
            if(w1.getWordVector(en) == null){
                continue;
            }

            c2++;
            float[] vector =  w1.getWordVector(en);
            for(int i = 0; i < vector.length; i++){
                v2[i] += vector[i];
            }
        }

        if(c2 == 0){
            return 0.0;
        }
        for(int i = 0; i < v2.length; i++){
            v2[i] = v2[i] / c2;
//            System.out.println("++++++++++++" +v2[i]);
        }

//        return Euclidean_distance(v1, v2);
       return getDotProduct(v1, v2) / (getMagnitude(v1) * getMagnitude(v2));
    }

    public static double cosineSimilarity2(String d1, String d2) {

        String[] str = d1.split(" ");
        String[] str2 = d2.split("");

        float[] v1 = new float[200];
        float[] v2 = new float[200];

        int c1 = 0;
        int c2 = 0;
        double score = 0.0;
        for(String en: str){
            if(w1.getWordVector(en) == null){
                continue;
            }
            c1++;
            float[] vector1 =  w1.getWordVector(en);

            double sum = 0.0;
            for(String ch: str2){
                if(w1.getWordVector(ch) == null){
                        continue;
                }

                c2++;
                float[] vector2 =  w1.getWordVector(ch);
                double sim = getDotProduct(vector1, vector2) / (getMagnitude(vector1) * getMagnitude(vector2));
                sum = max(sum, sim);
            }

            score += sum;
        }
        return score;
    }
}
