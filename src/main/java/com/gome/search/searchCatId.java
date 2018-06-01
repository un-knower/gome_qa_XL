package com.gome.search;

import com.gome.search.Search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @desc 查找树
 *
 * AC 多模匹配 分为三步
 * 1. 字典树的构造,
 * 按照关键字生成一个查找树.
 * 2.失败链的构造,
 * 最大后缀表示,生成查找失败节点的下一跳(和kmp模式匹配差不多)
 * 3.输出
 *
 */
public class searchCatId {

    private static searchCatId singleton;
    private static Search tree = new Search();
    private static final String MAIN_DICT = "/catId_pid.txt";
    private static Map<String, String> raw_data = new HashMap<String, String>();

    private searchCatId() {
        this.loadDict();
    }

    public static searchCatId getInstance() {
        if (singleton == null) {
            synchronized (searchCatId.class) {
                if (singleton == null) {
                    singleton = new searchCatId();
                    return singleton;
                }
            }
        }
        return singleton;
    }

    public void loadDict() {

        InputStream is = this.getClass().getResourceAsStream(MAIN_DICT);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            long s = System.currentTimeMillis();
            int count = 0;
            while (br.ready()) {
                String line = br.readLine();
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
//                System.out.println(pid_skuid);
//                System.out.println(++count);
                raw_data.put(pid_skuid, catId);
            }
        }
        catch (IOException e) {
            System.err.println(String.format(Locale.getDefault(), "%s load failure!", MAIN_DICT));
        }
        finally {
            try {
                if (null != is)
                    is.close();
            }
            catch (IOException e) {
                System.err.println(String.format(Locale.getDefault(), "%s close failure!", MAIN_DICT));
            }
        }
    }
/*
 * AC多模匹配
 * */

    public String Search(String pid_skuid){
        if(pid_skuid.equals(""))
            return "";
        return raw_data.get(pid_skuid);
    }

    public static void main(String args[]) {
        searchCatId brand = searchCatId.getInstance();
        System.out.println(brand.Search("A0005734763-pop8008303692"));
    }
}

