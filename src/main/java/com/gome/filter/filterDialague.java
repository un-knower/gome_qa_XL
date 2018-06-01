package com.gome.filter;

import com.gome.analysis.segment.GomeSegmenter;
import com.gome.analysis.segment.SegToken;
import com.gome.process.Read_WriteText;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lixiang-ds3 on 2017/8/30.
 */
public class filterDialague {

    private static GomeSegmenter seg = GomeSegmenter.getInstance();


    public static void main(String[] args) {

        InputStream inputStream = null;
        InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;
        Set<String> set = new HashSet<String>();
        List<String> list = new ArrayList<String>();
        Map<String, String> map = new HashMap<String, String>();
        try {
            inputStream = new FileInputStream("D:\\周报\\dialogues.txt");
            inputReader = new InputStreamReader(inputStream, "utf8");
            bufferReader = new BufferedReader(inputReader);

            String line = "";
            int count = 0;

            while ((line = bufferReader.readLine()) != null) {
                boolean flag = false;
                if (++count % 10000 == 0) {
                    System.out.println(count);
                }
                line = line.trim();
                line = line.toLowerCase();

                 String[] diag = line.split("\t");
                 String key = diag[0];
                 String value = diag[1];

                if (key.contains("退款") && !key.contains("京东")) {
                    System.out.println(line);
                    map.put(key, value);
//                    Read_WriteText.writeTxtFile("D:\\周报\\query.txt", , "utf8", true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferReader != null) {
                    bufferReader.close();
                }
                if (inputReader != null) {
                    inputReader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        for(Map.Entry<String, String> entry : map.entrySet()){
            String result = entry.getKey()+"\t" + entry.getValue();
            Read_WriteText.writeTxtFile("D:\\周报\\query2.txt", result , "utf8", true);
        }
//        for (String str : set) {
//            list.add(str);
//        }
//        Collections.sort(list);
//        for (String str : list) {
//            Read_WriteText.writeTxtFile("D:\\周报\\query_high17.txt", str, "utf8", true);
//
//        }
    }
}
