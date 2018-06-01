package com.gome.filter;

import com.gome.util.RedisUtils;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lixiang-ds3 on 2017/6/13.
 */
public class addCatId_idRedis {

    public static void main(String[] args){
        InputStream inputStream = null;
        InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;
//        Set<String> set = new HashSet<String>();
        Jedis jedis= RedisUtils.getJedis();
        try {
            inputStream = new FileInputStream("E:\\platform\\workspaceidea\\gome_qa_trunk\\src\\main\\resources\\catId_pid.txt");
            inputReader = new InputStreamReader(inputStream, "utf8");
            bufferReader = new BufferedReader(inputReader);

            String line = "";
            while ((line = bufferReader.readLine()) != null) {

                line = line.trim();
                String[] id = line.split("\t");
                String catId = "";
                String pid_skuid = "";
                try {
                    catId = id[0];
                    pid_skuid = id[1];
                }catch(Exception e){
                    continue;
                }
                catId = catId.trim();
                pid_skuid = pid_skuid.trim();
                if(catId.equals("") || pid_skuid.equals("")){
                    continue;
                }

                String key = "sku_" + pid_skuid;
                jedis.set(key, catId);
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
