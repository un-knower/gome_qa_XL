package com.gome.filter;

import com.gome.process.Read_WriteText;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lixiang-ds3 on 2017/7/20.
 */
public class filterAction {

    public static void main(String[] args){
        InputStream inputStream = null;
        InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;
        Set<String> set = new HashSet<String>();
        Map<String, String> map = new HashMap<String, String>();
        try {
            inputStream = new FileInputStream("E:\\action.txt");
            inputReader = new InputStreamReader(inputStream, "utf8");
            bufferReader = new BufferedReader(inputReader);

            String line = "";
            int count = 0;
            while ((line = bufferReader.readLine()) != null) {

                if(++count % 10000 == 0){
                    System.out.println(count);
                }
                line = line.trim();
                String[] action = line.split(",");
                try {
                    String pid = action[0];
                    pid = pid.replace("1-", "");
                    Integer number = Integer.parseInt(action[2]);
                    String skuid = action[18];
                    if(skuid.equals("")){
                        continue;
                    }
                    String pid_skuid = pid + "-" + skuid;
//                    System.out.println(pid_skuid + "##" + number);
                    if (map.containsKey(pid_skuid)) {
                        String num = map.get(pid_skuid);
                        String[] cnt = num.split("####");
                        String sales = cnt[0];
                        String ct = cnt[1];
                        Integer sale = Integer.parseInt(sales);
                        Integer c = Integer.parseInt(ct);
                        c = c + 1;
                        if (sale < number) {
                            map.put(pid_skuid, number+"####"+c);
                        }else{
                            map.put(pid_skuid, sale+"####"+c);
                        }
                    } else {
                        map.put(pid_skuid, number+"####"+1);
                    }
                }catch(Exception e){
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

        for(Map.Entry<String, String> entry : map.entrySet()){
            String id = entry.getKey();
            String number = entry.getValue();

            Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
            Matcher m = p.matcher(id);
            if (m.find()) {
                continue;
            }

            if(number.equals("0####1")){
                continue;
            }
            Read_WriteText.writeTxtFile("E:\\id_number.txt", entry.getKey()+"\t"+entry.getValue(), "utf8", true);
        }
    }
}
