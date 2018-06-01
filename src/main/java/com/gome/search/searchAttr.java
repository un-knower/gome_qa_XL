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
public class searchAttr {

    private static searchAttr singleton;
    private static Search tree = new Search();
    private static final String MAIN_DICT = "/bijiben_attr.txt";
    private static Map<String, String> raw_data = new HashMap<String, String>();

    private searchAttr() {
        this.loadDict();
    }

    public static searchAttr getInstance() {
        if (singleton == null) {
            synchronized (searchAttr.class) {
                if (singleton == null) {
                    singleton = new searchAttr();
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
            while (br.ready()) {
                String line = br.readLine();
                line = line.trim();
                line = line.toLowerCase();
                tree.add(line);
            }
            tree.build();
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

    public Boolean Search(String search){
        String brandId = "";
        if(search.equals("")){
            return false;
        }
        search = search.toLowerCase();
        List<String> list = tree.search(search);
        if(list.size() > 0){
            return true;
        }
        return false;
    }

    public static void main(String args[]) {
        searchAttr brand = searchAttr.getInstance();
        System.out.println(brand.Search("智能平台"));
    }
}

