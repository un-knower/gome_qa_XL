package com.gome.guide;

import com.gome.analysis.segment.GomeSegmenter;
import com.gome.process.Read_WriteText;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lixiang-ds3 on 2017/7/9.
 */
public class query_brand {

    private static GomeSegmenter segment;
    static {
        segment = GomeSegmenter.getInstance();
    }
    public static void load(String path) {

        InputStream inputStream = null;
        InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;
        Map<String, String> set = new HashMap<String, String>();
        try {
            inputStream = new FileInputStream(path);
            inputReader = new InputStreamReader(inputStream, "utf8");
            bufferReader = new BufferedReader(inputReader);

            String line = "";
            int count = 0;
            while ((line = bufferReader.readLine()) != null) {

                line = line.trim();
                String[] str = line.split(",");
                try {
                    String method = str[5];
                    if (!method.equals("search")) {
                        continue;
                    } else {
                        count++;
                        if(count % 1000 == 0){
                            System.out.println("---------" + count + "------------");
                        }
                        String title = str[1];
                        String query = str[4];
                        if(query.equals("")){
                            continue;
                        }
                        String brand = "";
                        String[] pos = segment.pos(title);
                        for(String s : pos){
                            if(s.contains("/brand")){
                                brand = s.replace("/brand", "");
                                break;
                            }
                        }

                        if(!brand.equals("")) {
                            if(brand.contains("/")){
                                int index = brand.indexOf("/");
                                brand = brand.substring(0, index);
                            }
                            String result = query + " __label__" + brand;
                            Read_WriteText.writeTxtFile("E:\\query_brand.txt", result, "utf8", true);
                        }
                    }
                }catch (Exception e){
                    continue;
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

    }


    public static void main(String[] args){
        load("E:\\action.txt");
    }
}
