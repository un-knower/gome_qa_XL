package com.gome.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by lixiang-ds3 on 2017/3/13.
 */
public class IDFCal {

    private static IDFCal singleton;
    private static final String MAIN_DICT = "/query3.txt";
    private static ArrayList<Document2> documents = new ArrayList<Document2>();

    private static Corpus corpus = null;

    static{
        IDFCal.getInstance();
        corpus = new Corpus(documents);
    }

    IDFCal() {
        this.loadDict();
    }

    public static IDFCal getInstance() {
        if (singleton == null) {
            synchronized (IDFCal.class) {
                if (singleton == null) {
                    singleton = new IDFCal();
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
                Document2 query2 = new Document2(line);
                documents.add(query2);
            }
        }
        catch (IOException e) {
            System.err.println(String.format(Locale.getDefault(), "%s load failure!", MAIN_DICT));
        }
        finally {
            try {
                if (null != is)
                    is.close();
            }
            catch (IOException e) {
                System.err.println(String.format(Locale.getDefault(), "%s close failure!", MAIN_DICT));
            }
        }
    }



    public static Double idfScore(String term){
        return corpus.getInverseDocumentFrequency(term);
    }
// d1   d2 是分完词的句子
    private static double getDotProduct(String d1, String d2) {
        double product = 0;
        Map<String, Double> weights1 = getTfIdf(d1);
        Map<String, Double> weights2 = getTfIdf(d2);

        for (String term : weights1.keySet()) {
            try {
                product += weights1.get(term) * weights2.get(term);
            }catch(Exception e){
                continue;
            }
        }

        return product;
    }

    private static Map<String,Double> getTfIdf(String d1) {
        Map<String, Double> map = new HashMap<String, Double>();
        d1 = d1.trim();
        String[] d = d1.split(" ");
        for(String str : d){
            map.put(str, idfScore(str));
        }

        return map;
    }


    private static double getMagnitude(String str) {
        double magnitude = 0;
        Map<String, Double> weights = getTfIdf(str);

        for (double weight : weights.values()) {
            magnitude += weight * weight;
        }

        return Math.sqrt(magnitude);
    }

    public static double cosineSimilarity(String d1, String d2) {

        return getDotProduct(d1, d2) / (getMagnitude(d1) * getMagnitude(d2));
    }
}
