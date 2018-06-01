package com.gome.filter;

import com.gome.util.RedisUtils;
import redis.clients.jedis.Jedis;

import java.io.*;

/**
 * Created by lixiang-ds3 on 2017/6/13.
 */
public class addAttr_value {

    public static void main(String[] args){
        InputStream inputStream = null;
        InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;
//        Set<String> set = new HashSet<String>();
        Jedis jedis= RedisUtils.getJedis();
        try {
            inputStream = new FileInputStream("E:\\SPZX\\attr.txt");
            inputReader = new InputStreamReader(inputStream, "utf8");
            bufferReader = new BufferedReader(inputReader);

            String line = "";
            while ((line = bufferReader.readLine()) != null) {

                line = line.trim();
                String[] id = line.split("\t");
                String attr = "";
                String attr_en = "";
                try {
                    attr = id[0];
                    attr_en = id[1];
                }catch(Exception e){
                    continue;
                }
                attr = attr.trim();
                attr_en = attr_en.trim();
                if(attr.equals("") || attr_en.equals("")){
                    continue;
                }

                String key = "At_" + attr;
                System.out.println(key + " " + attr_en);
                jedis.set(key, attr_en);
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
}
