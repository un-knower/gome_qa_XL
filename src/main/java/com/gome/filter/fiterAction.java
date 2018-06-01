package com.gome.filter;

import com.gome.process.Read_WriteText;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lixiang-ds3 on 2017/8/15.
 */
public class fiterAction {

    public static List<String> load(String path) {

        InputStream inputStream = null;
        InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;
        List<String> set = new ArrayList<String>();
        try {
            inputStream = new FileInputStream(path);
            inputReader = new InputStreamReader(inputStream, "utf8");
            bufferReader = new BufferedReader(inputReader);

            String line = "";
            while ((line = bufferReader.readLine()) != null) {

                line = line.trim();
                line = line.replace("(", "");
                line = line.replace(")", "");
                if(line.startsWith("9") || line.startsWith("A")){
                    set.add(line);
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
        return set;
    }
    public static void main(String[] args){
        List<String> action = load("E:\\idCount.txt");
        for(String act : action){
            String[] lit = act.split(",");
            if(Integer.parseInt(lit[1]) < 5){
                continue;
            }

            String id = lit[0];
            String[] li = id.split("-");
            if(li.length < 2){
                continue;
            }

            if(li[1].length() < 8){
                continue;
            }

            String result = id + "\t" + lit[1];
            System.out.println(result);
            Read_WriteText.writeTxtFile("E:\\action2.txt", result, "utf8", true);
        }
    }
}
