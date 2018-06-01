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
public class filterKeyWord {

    private static GomeSegmenter seg = GomeSegmenter.getInstance();


    public static void main(String[] args) {

        InputStream inputStream = null;
        InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;
        Set<String> set = new HashSet<String>();
        List<String> list = new ArrayList<String>();
        Map<String, String> map = new HashMap<String, String>();
        try {
            inputStream = new FileInputStream("D:\\server\\key_word\\key_word3.txt");
            inputReader = new InputStreamReader(inputStream, "utf8");
            bufferReader = new BufferedReader(inputReader);

            String line = "";
            int count = 0;

            while ((line = bufferReader.readLine()) != null) {
                if(line.equals("")){
                    continue;
                }
                boolean flag = false;
                if (++count % 10000 == 0) {
                    System.out.println(count);
                }
                line = line.trim();
                line = line.toLowerCase();

                set.add(line);

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


//        for(Map.Entry<String, String> entry : map.entrySet()){
//            String result = entry.getKey()+"\t" + entry.getValue();
//            Read_WriteText.writeTxtFile("D:\\周报\\query2.txt", result , "utf8", true);
//        }
//        for (String str : set) {
//            list.add(str);
//        }
//        Collections.sort(list);
        for (String str : set) {
            Read_WriteText.writeTxtFile("D:\\server\\key_word\\key_word4.txt", str, "utf8", true);

        }
    }
}
