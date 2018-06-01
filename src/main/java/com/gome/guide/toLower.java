package com.gome.guide;

import com.gome.process.Read_WriteText;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lixiang-ds3 on 2017/7/14.
 */
public class toLower {

    public static void load(String path) {

        InputStream inputStream = null;
        InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;
        Map<String, String> set = new HashMap<String, String>();
        try{

            inputStream = new FileInputStream(path);
            inputReader = new InputStreamReader(inputStream, "utf8");
            bufferReader = new BufferedReader(inputReader);

            String line = "";
            int count = 0;
            while ((line = bufferReader.readLine()) != null) {

                if(count++ % 10000 == 0){
                    System.out.println("------------"+count+"-----------------");
                }
                String result = line.toLowerCase();
                result = result.replace("/", " ");
                Read_WriteText.writeTxtFile("E:\\platform\\workspaceidea\\fastText_java\\model\\query_core_setLower.txt", result, "utf8", true);

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
        load("E:\\platform\\workspaceidea\\fastText_java\\model\\query_core_set.txt");
    }
}
