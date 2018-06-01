package com.gome.filter;

import com.gome.process.Read_WriteText;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lixiang-ds3 on 2017/6/8.
 */
public class filterId {

    public static Set<String> load(String path) {

        InputStream inputStream = null;
        InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;
        Set<String> set = new HashSet<String>();
        try {
            inputStream = new FileInputStream(path);
            inputReader = new InputStreamReader(inputStream, "utf8");
            bufferReader = new BufferedReader(inputReader);

            String line = "";
            while ((line = bufferReader.readLine()) != null) {

                line = line.trim();
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
        return set;
    }

    public static void main(String[] args){

        Set<String> set = filterId.load("E:\\platform\\workspaceidea\\gome_qa_trunk\\src\\main\\resources\\catId_pid.txt");
        Set<String> set2 = filterId.load("E:\\SPZX\\catId_pid.txt");

        for(String str : set2){
            System.out.println(str);
            if(set.contains(str)){
                continue;
            }

            Read_WriteText.writeTxtFile("D:\\tt.txt", str, "utf8", true);
        }
    }
}
