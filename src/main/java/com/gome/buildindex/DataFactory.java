package com.gome.buildindex; /**
 * Created by lixiang-ds3 on 2016/12/9.
 */
import com.gome.buildindex.Blog;
import com.gome.buildindex.JsonUtil;
import com.gome.process.ElasticSearch_sent2vec;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import org.ansj.library.DicLibrary;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class DataFactory {

    private static Map<String, String> map = new HashMap<String, String>();
    private static DataFactory singleton;
    private static final String MAIN_DICT = "/12.txt";
    static JiebaSegmenter segmenter = new JiebaSegmenter();

    private DataFactory() {
        this.loadDict();
    }


    public static DataFactory getInstance() {
        if (singleton == null) {
            synchronized (DataFactory.class) {
                if (singleton == null) {
                    singleton = new DataFactory();
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
                String[] info = line.split("\t");
                String price = "";
                String in = "";
                try {
                    String key = info[0];
                    key = key.trim();
                    List<SegToken> seg = segmenter.process(key, JiebaSegmenter.SegMode.SEARCH);

                    for (SegToken segToken : seg) {
                        in += segToken.word.getToken() + " ";
                    }
                    System.out.println(count++);
                    price  = info[1];
                    price = price.trim();
                }catch(Exception e){
                    continue;
                }
                if(map.containsKey(in)){
                    String  ans = map.get(in) + "%%%%%" + price;
                    map.put(in, ans);
                }else {
                    map.put(in, price);
                }
            }
        }
        catch (IOException e) {
            System.err.println(String.format(Locale.getDefault(), "%s load failure!", MAIN_DICT));
        } finally {
            try {
                if (null != is)
                    is.close();
            }
            catch (IOException e) {
                System.err.println(String.format(Locale.getDefault(), "%s close failure!", MAIN_DICT));
            }
        }
    }


    public static List<String> getInitJsonData() {
        List<String> list = new ArrayList<String>();
        DataFactory.getInstance();
        for(Map.Entry<String, String> entry : map.entrySet()) {
            int m = 1;
            String data = "";
            try {
               data = JsonUtil.model2Json(new Blog(m, entry.getKey(), entry.getValue(), "1"));
            }catch(Exception e){
                continue;
            }
            list.add(data);
        }
        return list;
    }

    public static List<String> getInitJsonData_product() {
        List<String> list = new ArrayList<String>();

        InputStream inputStream = null;
        InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;

        try {
            inputStream = new FileInputStream("C:\\Users\\lixiang-ds3\\Desktop\\智能客服\\商品咨询语料\\result_bijiben2.txt");
            inputReader = new InputStreamReader(inputStream, "utf8");
            bufferReader = new BufferedReader(inputReader);
            int count  = 0;
            String line = "";

            while ((line = bufferReader.readLine()) != null) {
                line = line.trim();
                String[] output = line.split("\t");
                String catId = output[0];
                String attr = output[1];

                String data = JsonUtil.model2Json_product(new Blog_product(catId, attr));
                list.add(data);
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
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
        return list;
    }

    public static List<String> getInitJsonData_final() {
        List<String> list = new ArrayList<String>();

        InputStream inputStream = null;
        InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;

        try {
            inputStream = new FileInputStream("D:\\智能客服\\商品咨询语料\\final.txt");
            inputReader = new InputStreamReader(inputStream, "utf8");
            bufferReader = new BufferedReader(inputReader);
            int count  = 0;
            String line = "";


            while ((line = bufferReader.readLine()) != null) {

                line = line.trim();
                String[] output = line.split("\t");
                String data = "";
                try {
                    String catId = output[0];
                    String pid_skuid = output[1];
                    String title = output[2];
                    String attr = output[3];
                    data = JsonUtil.model2Json_final(new Blog_final(catId, pid_skuid, title, attr));
                }catch (Exception e){
                    System.out.println(++count);
                    continue;
                }
                list.add(data);
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
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
        return list;
    }

    public static List<String> getInitJsonData_attr() {
        List<String> list = new ArrayList<String>();

        InputStream inputStream = null;
        InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;

        try {
            inputStream = new FileInputStream("C:\\Users\\lixiang-ds3\\Desktop\\智能客服\\商品咨询语料\\bijiben_catergory.txt");
            inputReader = new InputStreamReader(inputStream, "utf8");
            bufferReader = new BufferedReader(inputReader);
            int count  = 0;
            String line = "";

            while ((line = bufferReader.readLine()) != null) {
                line = line.trim();
                String[] output = line.split("\t");
                String catId = output[0];
                String attr = output[1];
                String ans = output[2];
                attr = attr.toLowerCase();

                List<SegToken> seg = segmenter.process(attr, JiebaSegmenter.SegMode.SEARCH);
                String in = "";
                for (SegToken segToken : seg) {
                    in += segToken.word.getToken() + " ";
                }
                in = in.trim();

                String data = JsonUtil.model2Json_attr(new Blog_attr(catId, in, ans));
                list.add(data);
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
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
        return list;
    }

    public static List<String> getInitJsonData_pc() {
        List<String> list = new ArrayList<String>();
        InputStream inputStream = null;
        InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;

        try {
            inputStream = new FileInputStream("D:\\李响\\key_word\\hebing20171016_seg.txt");
            inputReader = new InputStreamReader(inputStream, "utf8");
            bufferReader = new BufferedReader(inputReader);
            String line = "";

            while ((line = bufferReader.readLine()) != null) {
                line = line.trim();
                String[] output = line.split("\t");
                String cat = output[0];
                String question = output[1];
                String ans = output[2];

//                String in = ToAnalysis.parse(question).toStringWithOutNature(" ");
//                in = in.trim();

//                System.out.println(question);
                String data = JsonUtil.model2Json_pc(new Blog_pc(cat, question, ans));
                list.add(data);
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
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
        return list;
    }


    public static void main(String[] args){

    }
}