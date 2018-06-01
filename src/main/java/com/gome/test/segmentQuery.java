package com.gome.test;

import com.gome.process.ESUtil;
import com.gome.process.Read_WriteText;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.library.DicLibrary;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by lixiang-ds3 on 2017/7/22.
 */
public class segmentQuery {
    private static segmentQuery singleton;
    private static Set<String> set = new HashSet<String>();
    private static final String MAIN_DICT = "/key_word3.txt";
    private static Set<String> biaodian = ESUtil.categoryIds;

    static{
        segmentQuery.getInstance();

        for(String str : set) {
            DicLibrary.insert("dic", str);
        }
    }

    private segmentQuery() {
        this.loadDict();
    }

    public static segmentQuery getInstance() {
        if (singleton == null) {
            synchronized (segmentQuery.class) {
                if (singleton == null) {
                    singleton = new segmentQuery();
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
                set.add(line);
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
                if(line.equals("")){
                    continue;
                }

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

    public static void segmentwentiduiying(){
        List<String> set = load("D:\\server\\key_word\\wentiduiying1016.txt");
        int count = 0;
        for(String str : set){
//            str = str.replace(" ", "");
            String[] input = str.split("\t");
            String query = input[0];
            String  answer = input[1];
            String in = ToAnalysis.parse(query).toStringWithOutNature(" ");
            String out = ToAnalysis.parse(answer).toStringWithOutNature(" ");
            in = in.trim();
            out = out.trim();
            String result = in + "\t" + out;
            System.out.println(result);
           Read_WriteText.writeTxtFile("D:\\server\\key_word\\wentiduiying1016_seg.txt", result, "utf8", true);
        }

    }

    public static void segmenthebing() {
        List<String> set = load("D:\\server\\key_word\\hebing20171016.txt");
        int count = 0;
        for (String str : set) {
            str = str.trim();
//            str = str.replace("？", "");
            if (str.equals("")) {
                continue;
            }
//            str = str.replace(" ", "");

            String[] input = str.split("\t");
            String catid = input[0];
            String query = input[1];
            String ans = input[2];
            query = query.replace(" ", "");
            String in = ToAnalysis.parse(query).toStringWithOutNature(" ");
            in = in.trim();
            String result = catid + "\t" + in + "\t" + ans;
            System.out.println(result);
            Read_WriteText.writeTxtFile("D:\\server\\key_word\\hebing20171016_seg.txt", result, "utf8", true);
        }
    }

    public static void classSplit(){
        List<String> set = load("D:\\server\\key_word\\hebing20171016_seg.txt");
        int count = 0;
        for(String str : set){
            str = str.trim();
            if(str.equals("")){
                continue;
            }

            String[] input = str.split("\t");
            String catid = input[0];
            String  query = input[1];
            String ans = input[2];

            String result = "";
            if(catid.contains("##")){
                catid = catid.replace("##", "\t");
                result = catid + "\t" + query + "\t" + ans;
            }else{
                result = catid + "\t" + query + "\t" + ans;
            }

            System.out.println(result);
            Read_WriteText.writeTxtFile("D:\\server\\key_word\\hebing20171023_seg.txt", result, "utf8", true);
        }

    }

    public static void main(String[] args){

//        segmentwentiduiying();

         String str = "投金宝的含义是什么";
         Result result = ToAnalysis.parse(str);
         for(Term term : result.getTerms()){
             System.out.println(term.getName() + "\t" + term.getNatureStr());
         }
    }
}
