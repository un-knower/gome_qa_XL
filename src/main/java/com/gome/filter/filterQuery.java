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
public class filterQuery {

    private static GomeSegmenter seg = GomeSegmenter.getInstance();


    public static void main(String[] args){

        InputStream inputStream = null;
        InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;
        Set<String> set = new HashSet<String>();
        List<String> list = new ArrayList<String>();
        Map<String, String> map = new HashMap<String, String>();
        try {
            inputStream = new FileInputStream("D:\\周报\\query_high16.txt");
            inputReader = new InputStreamReader(inputStream, "utf8");
            bufferReader = new BufferedReader(inputReader);

            String line = "";
            int count = 0;
            int cnt = 0;

            while ((line = bufferReader.readLine()) != null) {
                boolean flag = false;
                if(++count % 10000 == 0){
                    System.out.println(count);
                }
                line = line.trim();
                line = line.toLowerCase();

//                if(line.contains("淘宝") || line.contains("天猫") || line.contains("苏宁") || line.contains("京东") || line.contains("国美")
//                        || line.contains("分享") || line.contains("收藏") || line.contains("彩票") || line.contains("qq")){
//
//                    System.out.println(line);
//                    continue;
//                }

//                Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
//                Matcher m = p.matcher(line);
//                while (m.find()) {
//                    flag = true;
//                }
//
//                if(!flag && !line.contains("/") && !line.contains("iphone") && !line.contains("ipad") && !line.contains("ipod") && !line.contains("watch") && !line.contains("apple")
//                        && !line.contains("nike") && !line.contains("htc") && !line.contains("candies") && !line.contains("macbook")
//                        && !line.contains("new balance") && !line.contains("adidas") && !line.contains("samsung") ){
//
                    String[] label = seg.pos(line);
                    String in = "";
                    for(String pos : label) {
                        in += pos + " ";
                    }
                    in = in.trim();
//
                    if(!in.contains("/")){
                        System.out.println(line);
                        Read_WriteText.writeTxtFile("D:\\周报\\query.txt", in, "utf8", true);
                        continue;
                    }


//                if(line.length() > 25){
//                    String[] label = seg.pos(line);
//                    String in = "";
//                    for(String pos : label){
//                        in += pos + " ";
//                    }
//                    in = in.trim();
//
//                    if(!in.contains("/")){
//                        System.out.println(line);
//                        continue;
//                    }
//                }
//
                set.add(line);
//                String[] action = line.split(",");
//                try {
//                    String pid = action[0];
//                    pid = pid.replace("1-", "");
//                    Integer number = Integer.parseInt(action[2]);
//                    String skuid = action[18];
//                    if(skuid.equals("")){
//                        continue;
//                    }
//                    String pid_skuid = pid + "-" + skuid;
////                    System.out.println(pid_skuid + "##" + number);
//                    if (map.containsKey(pid_skuid)) {
//                        String num = map.get(pid_skuid);
//                        String[] cnt = num.split("####");
//                        String sales = cnt[0];
//                        String ct = cnt[1];
//                        Integer sale = Integer.parseInt(sales);
//                        Integer c = Integer.parseInt(ct);
//                        c = c + 1;
//                        if (sale < number) {
//                            map.put(pid_skuid, number+"####"+c);
//                        }else{
//                            map.put(pid_skuid, sale+"####"+c);
//                        }
//                    } else {
//                        map.put(pid_skuid, number+"####"+1);
//                    }
//                }catch(Exception e){
//                    continue;
//                }
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

        for(String str : set){
            list.add(str);
        }
        Collections.sort(list);
        for(String str : list){
            Read_WriteText.writeTxtFile("D:\\周报\\query_high17.txt", str, "utf8", true);

        }
//        for(Map.Entry<String, String> entry : map.entrySet()){
//            String id = entry.getKey();
//            String number = entry.getValue();
//
//            Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
//            Matcher m = p.matcher(id);
//            if (m.find()) {
//                continue;
//            }
//
//            if(number.equals("0####1")){
//                continue;
//            }
//            Read_WriteText.writeTxtFile("E:\\id_number.txt", entry.getKey()+"\t"+entry.getValue(), "utf8", true);
//        }
    }
}
